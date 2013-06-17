package com.bvcom.transmit.handle.si;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.si.ChannelScanQueryParse;
import com.bvcom.transmit.util.CleanChannelAndTSC;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;

public class ChannelScanQueryHandle {

	private static Logger log = Logger.getLogger(ChannelScanQueryHandle.class
			.getSimpleName());

	MSGHeadVO bsData = new MSGHeadVO();

	String downString = new String();

	UtilXML utilXML = new UtilXML();

    MemCoreData coreData = MemCoreData.getInstance();
    
	public ChannelScanQueryHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}
	
	public ChannelScanQueryHandle() {
	}

	public MSGHeadVO setBsData(MSGHeadVO bsData){
		this.bsData = bsData;
		return this.bsData;
	}
	/**
	 * Ƶ��ɨ�账��
	 * 1. �·�Ƶ��ɨ��
	 * 2. ����Ƶ��ɨ����
	 * 3. ɨ�������
	 * 4. ɨ�����ϱ�����
	 *
	 */
	public void downXML() {

		List SMGSendList = new ArrayList();

		CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);
		UtilXML xmlUtil = new UtilXML();

		String returnStr = "";
		
        SysInfoVO sysVO = coreData.getSysVO();

        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("��ʷ��Ƶ�鿴StringToXML Error: " + e.getMessage());
        }
        

        ChannelScanQueryParse ChannelScanQueryParse = new ChannelScanQueryParse();

        ChannelScanQueryVO vo = ChannelScanQueryParse.getDownObject(document);
        
        String channelScanPath = "";
        
		try {
			File readFilePath = null;
			if (bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
				channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_3;
			}
			else if (bsData.getVersion().equals(CommonUtility.XML_VERSION_2_5)) {
				channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_5;
				this.downString = this.downString.replaceAll("Version=\"2.5\"", "Version=\"2.3\"");
//				System.out.println(" ------ ����� Э�� -------"+bsData);
			}
			else {
				channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_0;
			}
			readFilePath = new File(channelScanPath);
			returnStr = CommonUtility.readStringFormFile(readFilePath);

		} catch (CommonException e1) {
			// e1.printStackTrace();
			log.error("ȡ��Ƶ��ɨ����Ϣʧ��: " + channelScanPath);
		}

		if (vo.getScanTime() == null || vo.getScanTime().equals("") || returnStr.trim().equals("") || vo.getScanType() == 0) {
			
			for (int i = 0; i < SMGSendList.size(); i++) {
				SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
				try {
					// TODO ���������ָ��Ƶ��ɨЭ�� ����������ڲ����п��ܸ�һЩ�������Ƶ�� �� 52.5 �� 2011-08-15 Ji Long 
					// ��ָ��ɨ �յ��� Ƶ���� ��ȫƵ��ɨ �յ���Ƶ���� �ϳ�Ϊ һ��  ���ظ�ƽ̨ �����浽ָ���ļ�Ŀ¼
					//ChannelScanQueryFlag
					AutoAnalysisTimeQueryConfigFile  autoAnalysisTimeQueryConfigFile=new AutoAnalysisTimeQueryConfigFile(); 
					String str =autoAnalysisTimeQueryConfigFile.getChannelScanQueryFlag();
					String strr="";
					if(str.split(",")[0].equals("1")){
						ChannelScanQueryParse channelScanQueryParse=new ChannelScanQueryParse();
						String sendStr=channelScanQueryParse.createChannelScanXML(str, bsData);
						// Ƶ��ɨ����Ϣ�·� timeout 1000*60*5 �����
						strr = utilXML.SendDownXML(sendStr, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT2,bsData);
					}
					
					// Ƶ��ɨ����Ϣ�·� timeout 1000*60*50 ��ʮ����
					returnStr = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT,bsData);
					if(returnStr == null || returnStr.equals("")) {
						log.error("ȡ��Ƶ��ɨ���б�ʧ��: " + smg.getURL());
						continue;
					}
					
					//���ɨ����Ƶ�����Ȳ�Ϊ0 �� ������ƴ��
					if(str.split(",").equals("1")){
						if(strr.length()!=0){
							//ƴ�����ε�Ƶ����
						}
					}
					
					returnStr = CommonUtility.RegReplaceString(returnStr, "ScanTime");
					
					if (vo.getScanType() == 1) {
						/**
						 * ScanType=0Ϊ��
						 * ��ScanType=1ʱΪ��ϸɨ��
						 */
						CommonUtility.StoreIntoFile(returnStr, channelScanPath);
		                CommonUtility.StoreIntoFile(returnStr, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
					}
					break;
				} catch (CommonException e) {
					log.error("��SMG�·�Ƶ��ɨ�����" + smg.getURL());
				}
			}

		}
		
		if (vo.getScanType() == 1) {
			/**
			 * ScanType=0Ϊ��
			 * ��ScanType=1ʱΪ��ϸɨ��
			 */
	        try {
	        	if(returnStr == null || returnStr.equals("")) {
	        		log.error("ȡ��Ƶ��ɨ���б�ʧ��, ������������ԡ�");
	        		return;
	        	}
	        	CommonUtility.StoreIntoFile(returnStr, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
	        	
	        	returnStr = returnStr.replaceAll("'", " ");
	        	
	            document = utilXML.StringToXML(returnStr);
	            List<ChannelScanQueryVO> ChannelScanQueryVOList = ChannelScanQueryParse.getReturnObject(document);
	            delChannelScanTable();
	            
	            //added by tqy ������ServiceType������������ԭ����ṹ
	            try{
	            	updateChannelScanTable(ChannelScanQueryVOList);
	            }
	            catch(Exception ex){
	            	upChannelScanTable(ChannelScanQueryVOList);
	            }
	            
	        } catch (Exception e) {
	            log.error("Ƶ��ɨ�������ʧ�� Error: " + e.getMessage());
	        }
		}
		
		try {
        	// �ж��Ƿ�������XMLЭ�� Add Start By Bian Jiang 2011.2.21
            int msgEnd = returnStr.indexOf(CommonUtility.XML_MSG_END) + CommonUtility.XML_MSG_END.length();
            
            if (msgEnd != CommonUtility.XML_MSG_END.length()) {
            	try {
            		returnStr = returnStr.substring(0, msgEnd);	
            	} catch (Exception ex) {
            		log.warn("Ƶ���б���Ϣ�������: " + ex.getMessage());
            	}
            }
           // �ж��Ƿ�������XMLЭ�� Add End By Bian Jiang 2011.2.21
			
			// ���Ҽ�����Ķ���ǰ�˸��£������˶�ʱ����жϣ����Բ���ÿ�ζ��޸�ʱ��
			utilXML.SendUpXML(utilXML.replaceXMLMsgHeader(returnStr, bsData),bsData);
		} catch (CommonException e) {
			log.error("�Ϸ�Ƶ��ɨ����Ϣʧ��: " + e.getMessage());
		}
		
		//��� ����Զ�¼���Ŀ��Ϣ  �͸�TSC�·�ɾ��Э�� 
		CleanChannelAndTSC  ccat=new CleanChannelAndTSC(bsData);
		ccat.chean();
		
		bsData = null;
		downString = null;
		SMGSendList = null;
		utilXML = null;
	}

	/**
	 * �������Ƶ��ɨ���
	 * @param ��Ҫ���µ�XML����
	 * @throws DaoException 
	 */
	public static void upChannelScanTable(List<ChannelScanQueryVO> ChannelScanQueryVOList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for(int i=0; i<ChannelScanQueryVOList.size(); i++) {
			
			StringBuffer strBuff = new StringBuffer();
			ChannelScanQueryVO vo = ChannelScanQueryVOList.get(i);
			// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime)
			// values(1, 1, 1, 'test', 1, 1, 1, 1, 1, '2000-01-01 08:54:55', '2000-01-01 08:54:55')
			
			strBuff.append("insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime, LastFlag)");
			strBuff.append(" values(");
			strBuff.append(vo.getFreq() + ", ");
			strBuff.append("'" + vo.getQAM() + "', ");
			strBuff.append(vo.getSymbolRate() + ", ");
			strBuff.append("'" +  vo.getProgram() + "', ");
			strBuff.append(vo.getServiceID() + ", ");
			strBuff.append(vo.getVideoPID() + ", ");
			strBuff.append(vo.getAudioPID() + ", ");
			strBuff.append(vo.getEncrypt() + ", ");
			strBuff.append(vo.getHDTV() + ", ");
			strBuff.append("'" +  vo.getScanTime() + "', ");
			strBuff.append("'" + CommonUtility.getDateTime() + "', ");
			strBuff.append("1)");
	
			try {
				statement = conn.createStatement();
	
				statement.executeUpdate(strBuff.toString());
	
			} catch (Exception e) {
				log.error("Ƶ��ɨ��������ݿ����: " + e.getMessage());
				log.error("Ƶ��ɨ��������ݿ���� SQL��\n" + strBuff.toString());
			} finally {
				DaoSupport.close(statement);
			}
		}
		DaoSupport.close(conn);
		log.info("ɨ�����������ݿ�ɹ�!");
	}

	
	public static void updateChannelScanTable(List<ChannelScanQueryVO> ChannelScanQueryVOList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for(int i=0; i<ChannelScanQueryVOList.size(); i++) {
			
			StringBuffer strBuff = new StringBuffer();
			ChannelScanQueryVO vo = ChannelScanQueryVOList.get(i);
			// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV,ServiceType, ScanTime, LastTime,LastFlag)
			// values(1, 1, 1, 'test', 1, 1, 1, 1, 1, '2000-01-01 08:54:55', '2000-01-01 08:54:55')
			
			strBuff.append("insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ServiceType,ScanTime, LastTime, LastFlag)");
			strBuff.append(" values(");
			strBuff.append(vo.getFreq() + ", ");
			strBuff.append("'" + vo.getQAM() + "', ");
			strBuff.append(vo.getSymbolRate() + ", ");
			strBuff.append("'" +  vo.getProgram() + "', ");
			strBuff.append(vo.getServiceID() + ", ");
			strBuff.append(vo.getVideoPID() + ", ");
			strBuff.append(vo.getAudioPID() + ", ");
			strBuff.append(vo.getEncrypt() + ", ");
			strBuff.append(vo.getHDTV() + ", ");
			
			//added by tqy
			strBuff.append("'" + vo.getServiceType() + "', ");
			
			strBuff.append("'" +  vo.getScanTime() + "', ");
			strBuff.append("'" + CommonUtility.getDateTime() + "', ");
			strBuff.append("1)");
	
			try {
				statement = conn.createStatement();
	
				statement.executeUpdate(strBuff.toString());
	
			} catch (Exception e) {
				log.error("Ƶ��ɨ��������ݿ����: " + e.getMessage());
				log.error("Ƶ��ɨ��������ݿ���� SQL��\n" + strBuff.toString());
			} finally {
				DaoSupport.close(statement);
			}
		}
		DaoSupport.close(conn);
		log.info("ɨ�����������ݿ�ɹ�!");
	}
	/**
	 * �������Ƶ��ɨ���
	 * @param ��Ҫ���µ�XML����
	 * @throws DaoException 
	 */
	public static void upChannelScanTable(ChannelScanQueryVO vo) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		StringBuffer strBuff = new StringBuffer();
		// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime)
		// values(1, 1, 1, 'test', 1, 1, 1, 1, 1, '2000-01-01 08:54:55', '2000-01-01 08:54:55')
		
		strBuff.append("insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime, LastFlag)");
		strBuff.append(" values(");
		strBuff.append(vo.getFreq() + ", ");
		strBuff.append("'" + vo.getQAM() + "', ");
		strBuff.append(vo.getSymbolRate() + ", ");
		strBuff.append("'" +  vo.getProgram() + "', ");
		strBuff.append(vo.getServiceID() + ", ");
		strBuff.append(vo.getVideoPID() + ", ");
		strBuff.append(vo.getAudioPID() + ", ");
		strBuff.append(vo.getEncrypt() + ", ");
		strBuff.append(vo.getHDTV() + ", ");
		strBuff.append("'" +  vo.getScanTime() + "', ");
		strBuff.append("'" + CommonUtility.getDateTime() + "', ");
		strBuff.append("1)");

		try {
			statement = conn.createStatement();

			statement.executeUpdate(strBuff.toString());

		} catch (Exception e) {
			log.error("Ƶ��ɨ��������ݿ����: " + e.getMessage());
			log.error("Ƶ��ɨ��������ݿ���� SQL��\n" + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		DaoSupport.close(conn);
		log.info("ɨ�����������ݿ�ɹ�!");
	}
	
	/**
	 * ����Ƶ��ɨ���
	 * @throws DaoException 
	 */
	public static void delChannelScanTable() throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		StringBuffer strBuff = new StringBuffer();

		// update channelscanlist set LastFlag = 0
		strBuff.append("update channelscanlist set LastFlag = 0");

		try {
			statement = conn.createStatement();

			statement.executeUpdate(strBuff.toString());

		} catch (Exception e) {
			log.error("����Ƶ��ɨ�����ݿ����: " + e.getMessage());
			log.error("����Ƶ��ɨ�����ݿ���� SQL��\n" + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		log.info("ɨ�����������ݿ�ɹ�!");
	}
	
	/**
	 * ʵʱƵ��ɨ��
	 */
    public void channelScanNow() {
        
    	log.info("��ʼ����Ƶ��ɨ��: ");
        List SMGSendList = new ArrayList();
        
        CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);

        String upString = "";
        
        upString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        upString += "<Msg Version=\"2.3\" MsgID=\"1000\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"110000G01\" DstCode=\"11000M01\" SrcURL=\"http://10.24.32.28:8089/servlet/receiver\" Priority=\"1\">";
        upString += " <ChannelScanQuery ScanTime=\"\" ScanType=\"1\" SymbolRate=\"6875\" QAM=\"QAM64\" />     ";
        upString += "</Msg>";
        

        UtilXML utilXML = new UtilXML();
        
        String retXML = "";
        
        for (int i=0; i< SMGSendList.size(); i++) {
            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
            try {
                Document document = null;
                SysInfoVO sysVO = coreData.getSysVO();
                
                // Ƶ��ɨ����Ϣ�·� timeout 1000*60*10 ʮ����
                retXML = utilXML.SendDownXML(upString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
                
            	if(retXML.equals("")) {
            		log.error("Ƶ��ɨ����Ϊ��");
            		break;
            	}
            	
            	retXML = CommonUtility.RegReplaceString(retXML, "ScanTime");
            	
            	// �ж��Ƿ�������XMLЭ�� Add Start By Bian Jiang 2011.2.21
                int msgEnd = retXML.indexOf(CommonUtility.XML_MSG_END) + CommonUtility.XML_MSG_END.length();
                
                if (msgEnd != CommonUtility.XML_MSG_END.length()) {
                	try {
                		retXML = retXML.substring(0, msgEnd);
                	} catch (Exception ex) {
                		 log.warn("Ƶ���б���Ϣ�������: " + ex.getMessage());
                	}
                }
               // �ж��Ƿ�������XMLЭ�� Add End By Bian Jiang 2011.2.21
                
				if (bsData.getVersion() == CommonUtility.XML_VERSION_2_3) {
					CommonUtility.StoreIntoFile(retXML, CommonUtility.CHANNEL_SCAN_PATH_2_3);
				} else {
					CommonUtility.StoreIntoFile(retXML, CommonUtility.CHANNEL_SCAN_PATH_2_0);
				}

                CommonUtility.StoreIntoFile(retXML, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
                
                try {
                    ChannelScanQueryParse ChannelScanQueryParse = new ChannelScanQueryParse();
                    
                	retXML = retXML.replaceAll("'", " ");
                	
                    document = utilXML.StringToXML(retXML);
                    List<ChannelScanQueryVO> ChannelScanQueryVOList = ChannelScanQueryParse.getReturnObject(document);
                    ChannelScanQueryHandle.delChannelScanTable();
                    ChannelScanQueryHandle.upChannelScanTable(ChannelScanQueryVOList);
                } catch (Exception e) {
                    log.error("Ƶ��ɨ�������ʧ�� Error: " + e.getMessage());
                }
                
                // FIXED ֻ��һ��ͨ����Ƶ��ɨ��
                break;
            } catch (Exception e) {
                log.error("��SMG�·�Ƶ��ɨ�����" + smg.getURL());
            }
        }
        //��� ����Զ�¼���Ŀ��Ϣ  �͸�TSC�·�ɾ��Э�� 
        //����Զ�¼���е�SrcURL,�����ظ����ƽ̨
        //BY TQY ����
        this.bsData.setSrcURL("");
		CleanChannelAndTSC  ccat=new CleanChannelAndTSC(bsData);
		ccat.chean();
    }
}
