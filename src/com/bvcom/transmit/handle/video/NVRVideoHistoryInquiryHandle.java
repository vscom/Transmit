package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.rec.NVRVideoHistoryInquiryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class NVRVideoHistoryInquiryHandle {
	private static Logger log = Logger.getLogger(NVRVideoHistoryInquiryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRVideoHistoryInquiryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    /**
     * TSC ���ɲ����б��ظ�java
     * 1.����xml�õ�ͨ��index�б�
     * 2.�·�xml����Ӧ��tsc
     * 3.tsc�ϱ���xmlת�������ģ��ظ���
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		 // ��������
		@SuppressWarnings("unused")
		String upString = "";
        
        Document document = null;
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
        
        
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("��ʷ��Ƶ�鿴StringToXML Error: " + e.getMessage());
        }
        NVRVideoHistoryInquiryParse nvrHistory = new NVRVideoHistoryInquiryParse();
        List<ProvisionalRecordTaskSetVO> nvrHistorylist = nvrHistory.getIndexByDownXml(document);
        
		long t1 = System.currentTimeMillis();

		String startDataTime = "";
		String endDataTime = "";
		
		String url = "";
        for(int i=0; i< nvrHistorylist.size(); i++){
        	ProvisionalRecordTaskSetVO vo = nvrHistorylist.get(i);
        	
        	int index = 0;
        	
        	startDataTime = vo.getStartTime();
        	endDataTime = vo.getEndTime();
        	
        	// Del By Bian Jiang ��Ŀ��Ϣ���ӽ�Ŀӳ���ȡ�� 2010.9.8
//        	if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
//        		try {
//					index = getIndexByProgram(vo);
//					this.downString = this.downString.replaceAll("Index=\"0\"", "Index=\"" + index + "\"");
//				} catch (DaoException e) {
//					
//				}
//        	} else if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_0)) {
//        		// ͨ��ӳ��ȡ�õ�ǰ��Ŀ��Ϣ
//        		try {
//					index = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
//					this.downString = this.downString.replaceAll("Index=\"" + vo.getIndex() + "\"", "Index=\"" + index + "\"");
//				} catch (DaoException e) {
//				}
//        	}
        	
			try {
				vo = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
			} catch (DaoException e1) {

			}
			
			/**
			 * ���ݼ��������Ŀ, TSC����ҪIndex��
			 * By: Bian Jiang
			 * 2011.4.7
			 */
//			this.downString = this.downString.replaceAll("Index=\"" + index + "\"", "Index=\"" + vo.getIndex() + "\"");
			
			
			//�޸ĸ�tsc�·�ʱ�� �ж����ĸ�tsc¼�Ƶľ͸��ĸ�tsc�·�  
        	for(int j=0;j<TSCSendList.size();j++){
        		TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(j);
    			try {
    				int tscIndex=getTscIndex(vo);
    				if(tscIndex >= tsc.getIndexMin() && tscIndex <= tsc.getIndexMax() ){
    					if(!url.equals(tsc.getURL())) {
//                      ��ʷ��Ƶ�鿴�·� timeout 1000*20 ��ʮ��
    						upString = utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
    						//break;
    						url = tsc.getURL();
    						if(upString.equals("")) {
    							log.info("������ϢΪ��: " + tsc.getURL());
    							continue;
    						} else {
    							break;
    						}
    					}
    				}
                    
                } catch (CommonException e) {
                    log.error("�·���ʷ��Ƶ�鿴��TSC����" + tsc.getURL());
                    upString = "";
                }
        	}
        	
        	
        }
        
        long t2 = System.currentTimeMillis();
        
      //�ϱ��ظ���xml������
        try {
        	int javaSleepTime = 0;
        	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	} else {
//        		javaSleepTime = CommonUtility.NVRVideoSleepTime(startDataTime, endDataTime);
//        		log.info("--> TSC��Ƶ�鿴ʱ��: " + ((t2-t1)) + " ms,  Java Sleep Time: " + (javaSleepTime/1000) + "s");
        	}
			
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("��ʷ��Ƶ��ظ�ʧ��: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        nvrHistory = null;
        
    }
	
	//��ȡ�ý�Ŀ����tsc���ĸ�ͨ��
	public static int getTscIndex(ProvisionalRecordTaskSetVO vo){
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		int tscIndex=0;
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();

			String sqlStr = "SELECT TscIndex FROM channelremapping a where Freq = "+vo.getFreq()+" and ServiceID = "+vo.getServiceID()+";";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				tscIndex=rs.getInt("TscIndex");
			}
		} catch (Exception e) {
			log.error("���ұ�������״̬���ϴ���: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
		
		return tscIndex;
	}
	
	
    public static ProvisionalRecordTaskSetVO getIndexByProgramForChannelRemap(ProvisionalRecordTaskSetVO vo) throws DaoException {

		int index = 0;
		int devIndex = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select channelindex, DevIndex  from channelremapping where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());

		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				index = Integer.parseInt(rs.getString("channelindex"));
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				vo.setIndex(index);
				return vo;
			}
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		log.info("�Զ�¼�� ȡ�ý�Ŀ���ͨ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return vo;
	}
    
	public static int getIndexByProgram(ProvisionalRecordTaskSetVO vo) throws DaoException  {

		int index = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// TODO ���ܳ��ֶ��Ƶ�����ͬһ����Ŀ
		strBuff.append("select channelindex from channelprogramstatus where Freq = " + vo.getFreq() + " and  ");
		strBuff.append("ServiceID = " + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				index = Integer.parseInt(rs.getString("channelindex"));
			}
			
			if(index == 0) {
				strBuff = new StringBuffer();
				strBuff.append("select channelindex from channelprogramstatus where channelFlag = 0 ");
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					index = Integer.parseInt(rs.getString("channelindex"));
					break;
				}
			}
			
		} catch (Exception e) {
			log.error("�Զ�¼���ѯͨ�����ݿ����: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		log.info("�Զ�¼���ѯͨ�����ݿ�ɹ�!");
		
		vo.setIndex(index);
		return index;
	}

	public MSGHeadVO getBsData() {
		return bsData;
	}

	public void setBsData(MSGHeadVO bsData) {
		this.bsData = bsData;
	}

	public String getDownString() {
		return downString;
	}

	public void setDownString(String downString) {
		this.downString = downString;
	}

	public UtilXML getUtilXML() {
		return utilXML;
	}

	public void setUtilXML(UtilXML utilXML) {
		this.utilXML = utilXML;
	}
}
