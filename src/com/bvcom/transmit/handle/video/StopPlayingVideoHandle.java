package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class StopPlayingVideoHandle {
	
    private static Logger log = Logger.getLogger(StopPlayingVideoHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public StopPlayingVideoHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    public StopPlayingVideoHandle(){
    	
    }
    
    
    private List SMGSendList = new ArrayList();//SMG���б���Ϣ
    
    /**
     * 1. ȡ��RTVM���еĵ�ַ
     * 2. ����ƵֹͣЭ�鷢������rtvm
     */
    @SuppressWarnings({ "unchecked", "static-access" })
	public void downXML() {

    	 SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
    	 SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
    	 List channelSendList = new ArrayList();//Channel Index List
         int channel = 0;
         
    	 SMGSendList = new ArrayList();
    	 
    	//����һ��һʵʱ��Ƶ���ҵ������״̬recordtype=4
    	// ȡ����������صĽ�Ŀ��Ϣ
		@SuppressWarnings("unused")
		//1����Ҫ��ȡRECORDTYPE!=4������RECORDTYPE=2�Ľ�Ŀ��Ϣ�������·�ָ����忨
		List<SetAutoRecordChannelVO> voList = getProgramInfoByIndex();

		try{
			//2012-07-20������ɾ��SMG�Ľ�Ŀ��Ϣ
			for(int i=0; i< voList.size(); i++)  {//�õ��������xml�Ķ��������е�һ������
	        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)voList.get(i);
	        	@SuppressWarnings("unused")
				int channelIndex = (int)vo.getIndex();
	        
	        	 // ɾ��ͨ��ʹ�ñ�� TODO ɾ������ͨ��������
				handle.GetIndexByProgram(vo, true);
				channelIndex = vo.getDevIndex();
				handle.updateDelFlagChannelRemappingByProgram(vo);
				
				 //���忨�Ŵ���channelSendList�У��ұ�֤channelSendList���ظ���ͨ����
				if (channel != channelIndex && channelIndex != 0) {
					boolean isHasIndex = false;
					for(int j=0; j<channelSendList.size(); j++) {
						int no = Integer.valueOf((Integer)channelSendList.get(j)) ;
						if(channelIndex == no) {
							isHasIndex = true;
							break;
						}
					}
					if(!isHasIndex) {
						channelSendList.add(channelIndex);
					}
	   			channel = channelIndex;
				} else {
					channel = channelIndex;
				}
	
				//*************�·�smg(��Ҫ����Щ�忨����Ϣ)**************
				CommonUtility.checkSMGChannelIndex(channelIndex, SMGSendList);
	   		
			}
		}catch(Exception ex){
			
		}
		String url = "";
		String smgDownString="";
		
		if (channelSendList.size() != 0) {
				try{
					List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = handle.GetProgramInfoByIndex(channelSendList, true);
					smgDownString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set", true);
					
	        		for(int l=0;l<SMGSendList.size();l++)
	  		        {
	  		            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(l);
	  		            try {
	  		                if (!url.equals(smg.getURL().trim()))
	  		                {
		  		                smgDownString = smgDownString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
		  		            	smgDownString = smgDownString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
		  		                // �Զ�¼���·� timeout 1000*30 ��ʮ��
		  		                utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(smgDownString), smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
		  		                url = smg.getURL().trim();
	  		                }
	  		                // �����������
	  		                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
	  		                	// ����ת���·�
	  		                	utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(smgDownString), smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	  		                }
	  		                try {
	  							Thread.sleep(1000 * 1);
	  						} catch (InterruptedException e) {
	  	
	  						}
	  		            } catch (CommonException e) {
	  		                log.error("�·��������Զ�¼��SMG����" + smg.getURL());
	  		            }
	  		        }
				}catch(Exception ex){
					
				}
		}
		
		//ͬʱ����һ��һ��Ŀ����recordtype=4 �޸�Ϊ��ʼ״̬
		//BY TQY �������
		updateMosaicChannelMapping(voList);
		
    	// ��������
        @SuppressWarnings("unused")
		String upString = "";
        boolean isErr = false;
        List<MonitorProgramQueryVO> rtvsList =new ArrayList<MonitorProgramQueryVO>();
        try {
        	rtvsList = MonitorProgramQueryHandle.GetChangeProgramInfoList(rtvsList);
		} catch (DaoException e1) {
			isErr = true;
		}
        
		//��RTVM�·���Ƶֹͣ����
        if (isErr) {
            // ʧ��
            upString = utilXML.getReturnXML(bsData, 1);
        } else {
        	for (int i = 0; i < rtvsList.size(); i++) {
		        try {
		        	upString = utilXML.SendDownXML(this.downString, rtvsList.get(i).getRTVSResetURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
		        } catch (CommonException e) {
		            isErr = true;
		        }
        	}
        }
        
        //BY TQY 
     	//��IPM�·���ֹͣ�ϱ��ֲ���Ŀ��Ϣ
    	List ipmList=coreData.getIPMList();
    	for(int i=0;i<ipmList.size();i++){
    		IPMInfoVO ipm = (IPMInfoVO) ipmList.get(i);
    		if(ipm.getRecordType()==3){
    			try {
				  upString=utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
				} catch (CommonException e) {
					log.error("��������ѵ�·��໭����"+e.getMessage());
					isErr = true;
				}
    		}
    	}
    	//by tqy 2012-07-09 ����ɾ���ֲ��忨��Ϣ
    	
    	
        String returnstr="";
        if(isErr){
        	returnstr = utilXML.getReturnXML(this.bsData, 1);
        	try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
            }
        }else{
        	returnstr = utilXML.getReturnXML(this.bsData, 0);
        	try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
            }
        }
        
        bsData = null;
        this.downString = null;
        SMGSendList = null;
        utilXML = null;
    }
    
    /**
     * ȡ�÷��ص�XML��Ϣ
     * @param head XML���ݶ��� 
     * @param value 0:�ɹ� 1:ʧ��
     * @return XML�ı���Ϣ
     */
    public String getReturnXML(String url, MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
        }
        strBuf.append("<ReturnInfo> \r\n <MosaicUrl URL=\"" + url	+ "\" /> \r\n</ReturnInfo>\r\n");
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
    
    /*
	 *������������ؽ�Ŀ��Ϣ������һ��һ��Ŀ��
	 * 
	 */
	public void updateMosaicChannelMapping(List<SetAutoRecordChannelVO>  voList)
	{
		for(int i =0;i<voList.size();i++)
		{
			@SuppressWarnings("unused")
			SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)voList.get(i);
			try {
				this.delChannelRemappingIndex(vo);
			} catch (DaoException e) {
				e.printStackTrace();
			}
		}
	}
	
    
    public  void delChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, RecordType=0,Action=null,");
		strBuff.append(" DownIndex = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where channelindex = " + vo.getIndex());
		strBuff.append(" and devIndex = " + vo.getDevIndex());
		//System.out.println(strBuff.toString());
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    /**
	 * ȡ����������صĽ�Ŀ��Ϣ
	 * recordType 0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��)	3: ����¼��  4: �����˺ϳ��ֲ�
	 * @return
	 */
	public List<SetAutoRecordChannelVO> getProgramInfoByIndex() {

		List<SetAutoRecordChannelVO> voList = new ArrayList<SetAutoRecordChannelVO>();

		Statement statement = null;
		Connection conn = null;

		ResultSet rs = null;
		try {
			conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff = new StringBuffer();
			// ȡ����ؽ�ĿƵ����Ϣ
			strBuff.append("select *  from channelremapping where delflag =0 and  RecordType = 4 ;");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				while (rs.next()) {
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					//if (freq == 0 || serverID == 0) {
					//	continue;
					//}
					SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
					vo.setServiceID(serverID);
					vo.setFreq(freq);
					vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
					vo.setSymbolRate(Integer.parseInt(rs.getString("symbolrate")));
					vo.setQAM(Integer.parseInt(rs.getString("qam")));
					vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
					vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
					vo.setAction(rs.getString("Action"));
					vo.setRecordType(Integer.parseInt(rs.getString("RecordType")));
					vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
					voList.add(vo);
				}

			} catch (Exception e) {
				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return voList;
	}

}
