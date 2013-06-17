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
import com.bvcom.transmit.parse.video.MonitorProgramQueryParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * ʵʱ��Ƶ�࿴
 * @author Bian Jiang
 *
 */
public class MonitorProgramQueryHandle {

    private static Logger log = Logger.getLogger(MonitorProgramQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public MonitorProgramQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. ����ʵʱ��Ƶ�࿴Э��
     * 2. ȡ��û�з����rtvsIndex, ����������˾�ͨ��ʱ������ȡ�����һ��
     * 2. ͨ��Index���ж��Ƿ��IPM����ָ��
     *    a. IndexΪ�շ������ݸ�SMG, 
     *    b. ���Index��ֵ������ص�rtvsIndex��SMG����ֹͣ����ٷ��͸�IPM������ݡ�
     * 3. ����rtvsURL������
     */
    public void downXML() {
		String upString = "";
		String smgDownString = "";
		int isErrFlag = 0;
        SysInfoVO sysInfoVO = coreData.getSysVO();
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("ʵʱ��Ƶ�࿴StringToXML Error: " + e.getMessage());
        }
        MonitorProgramQueryParse monitorProgramQuery = new MonitorProgramQueryParse();
        List<MonitorProgramQueryVO> MonitorProgramList = monitorProgramQuery.getDownXml(document);
        
		String rtvsURL = "";
		
		RTVSResetConfigParse RTVSReset = null;
		
		int isFlag = 0; // 1: �ɹ� 0:ʧ��
		
		for(int i=0; i<MonitorProgramList.size(); i++) {
			MonitorProgramQueryVO vo = (MonitorProgramQueryVO)MonitorProgramList.get(i);
		
			// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
			int runType = 0;
			
			if(vo.getIndex() > 0) {
				runType = 3;
			} else {
				runType = 2;
			}
			
			if(vo.getIndex() > 0) {
				// �����ֲ�����
				try {
					vo = GetProgramPatrolInfo(vo);
				} catch (DaoException e) {
					log.error("ȡ����ѯ�����鲥��ַ�Ͷ˿ں�: " + e.getMessage());
				}
				
			} else {
				int priority = 0;
				try {
					priority = Integer.valueOf(this.bsData.getPriority());
				} catch (Exception ex) {
					log.error("һ��һ����ȡ�����ȼ�����: " + ex.getMessage());
				}
				// һ��һ����
		        try {
		        	SetAutoRecordChannelVO SetAutoRecordChannelVO = new SetAutoRecordChannelVO();
		        	// �ж��Ƿ����һ��һ��� 
		        	SetAutoRecordChannelVO.setFreq(vo.getFreq());
		        	SetAutoRecordChannelVO.setServiceID(vo.getServiceID());
		        	isFlag = SetAutoRecordChannelHandle.isHaveProgramInRemapping(SetAutoRecordChannelVO);
		        	
		        	if(isFlag == 1) {
			        	vo = GetProgramInfoByDownIndex(vo, this.downString);
			        	vo.setRtvsIP(SetAutoRecordChannelVO.getUdp());
			        	vo.setRtvsPort(SetAutoRecordChannelVO.getPort());
		        	}
					// 0:���� 1:һ��һ���Ӷ໭�� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��(������) 6: һ��һ�����ֶ�ѡ̨
		        	// ���ȼ� 1�Ƕ໭�棬2���ֶ�ѡ̨��
					if (priority == 1) {
						// һ��һ���໭��
						vo.setStatusFlag(1);
						log.info("һ��һ���໭�����ȼ�: " + priority);
					} else if (priority == 2){
						// �ֶ�ѡ̨�е�һ��һ���
						vo.setStatusFlag(6);
						log.info("һ��һ��������ȼ�: " + priority);
					}
				} catch (DaoException e) {
					log.error("ʵʱ��Ƶ�࿴ ȡ��ʵʱ��Ƶ��Ŵ���: " + e.getMessage());
				}

			}
			
	        // RTVS�޸���������IP�Ͷ˿�
	        RTVSReset = new RTVSResetConfigParse();

	        String rtvsString = RTVSReset.createForDownXML(bsData, vo);
	        
	        try {
	        	upString = utilXML.SendDownXML(rtvsString, vo.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
	        	isFlag = 1;
	        } catch (Exception e) {
	            log.error("�·�RTVS�޸���������IP�Ͷ˿ڳ���" + vo.getRTVSResetURL());
	            isFlag = 0;
	        }
			// TODO Ŀǰֻ�������·�һ��ָ��
			break;
		}
		
		if(isFlag == 1) {
	        try {
	            document = utilXML.StringToXML(upString);
	            rtvsURL = RTVSReset.getReturnURL(document);
	        } catch (CommonException e) {
	            log.error("��ƵURL StringToXML Error: " + e.getMessage());
	            upString = "";
	        }
		}
		
		if(!upString.equals("") && isErrFlag == 0 && !rtvsURL.equals("") && isFlag==1) {
			upString = monitorProgramQuery.getReturnXML(this.bsData, 0, rtvsURL);
		} else {
			upString = monitorProgramQuery.getReturnXML(this.bsData, 1, rtvsURL);
		}
        
        try {
            // �ȴ�һ���ӣ���SMG���������ֹRTVS��û�нӵ�����������
        	Thread.sleep(CommonUtility.VIDEO_CHANGE_SLEEPTIME);
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("ʵʱ��Ƶ�࿴�·��ϱ���Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        utilXML = null;
        upString = null;
        monitorProgramQuery = null;
        MonitorProgramList = null;
    }
    
//    private  MonitorProgramQueryVO GetProgramInfoByDownIndex(MonitorProgramQueryVO vo) throws DaoException {
//
//		Statement statement = null;
//		Connection conn = DaoSupport.getJDBCConnection();
//
//		ResultSet rs = null;
//			
//		// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
//		int runType = 0;
//		
//		if(vo.getIndex() > 0) {
//			runType = 3;
//		} else {
//			runType = 2;
//		}
//		
//		StringBuffer strBuff = new StringBuffer();
//		
//		int isFlag = 0;
//		
//		if(vo.getFreq() != 0) {
//			strBuff.append("select * from monitorprogramquery where statusFlag = 1 and Freq= " + vo.getFreq() + " and ServiceID=" + vo.getServiceID() + "   order by lastDatatime");
//			try {
//				statement = conn.createStatement();
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				while(rs.next()){
//					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
//					
//					vo.setRtvsIP(rs.getString("rtvsIP"));
//					
//					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
//					
//					vo.setSmgURL(rs.getString("smgURL"));
//					
//					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
//					
//					vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
//					
//					vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
//					isFlag = 1;
//					break;
//				}
//				
//			} catch (Exception e) {
//				log.error("ʵʱ��Ƶ�࿴ ȡ��ʵʱ��Ƶ��Ŵ���: " + e.getMessage());
//				isFlag = 0;
//			} finally {
//				DaoSupport.close(rs);
//				DaoSupport.close(statement);
//		
//			}
//			strBuff = null;
//		}
//		
//		if (isFlag == 0) {
//			// û�п��е�RTVS, ����ʱ��ȡ��ʱ����õ�һ��
//			strBuff = new StringBuffer();
//			strBuff.append("select *  from monitorprogramquery where statusFlag = 1  order by lastDatatime");
//			try {
//				statement = conn.createStatement();
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				while(rs.next()){
//					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
//					
//					vo.setRtvsIP(rs.getString("rtvsIP"));
//					
//					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
//					
//					vo.setSmgURL(rs.getString("smgURL"));
//					
//					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
//					
//					vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
//					
//					vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
//					break;
//				}
//				
//			} catch (Exception e) {
//				log.error("ʵʱ��Ƶ�࿴ ȡ��ʵʱ��Ƶ��Ŵ���: " + e.getMessage());
//			} finally {
//				DaoSupport.close(rs);
//				DaoSupport.close(statement);
//		
//			}
//		}
//
//		strBuff = null;
//		
//		// �����Ѿ������rtvsIndex Flag
//		strBuff = new StringBuffer();
//		
//		String rtvsIP = "";
//		int rtvsPort = 0;
//		
//		if(vo.getFreq() != 0 && vo.getServiceID() != 0) {
//			rtvsIP = "239.0.0." + (vo.getFreq()/4000);
//			rtvsPort = vo.getServiceID() + 2000;
//			vo.setRtvsIP(rtvsIP);
//			vo.setRtvsPort(rtvsPort);
//		}
//		
//		// update transmit.monitorprogramquery c set statusFlag = 1, lastDatatime = '2009-08-17 15:30:00' where rtvsIndex = 1
//		strBuff.append("update monitorprogramquery c set ");
//		strBuff.append(" xml = '" + downString + "', ");
//		// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
//		strBuff.append(" RunType = " + runType + ", ");
//		strBuff.append(" Freq = " + vo.getFreq() + ", ");
//		strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
//		strBuff.append(" rtvsIP = '" + vo.getRtvsIP() + "', ");
//		strBuff.append(" rtvsPort = " + vo.getRtvsPort() + ", ");
//		strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
//		strBuff.append(" where rtvsIndex = " + vo.getRtvsIndex());
//		
//		try {
//			statement = conn.createStatement();
//			
//			statement.execute(strBuff.toString());
//			
//		} catch (Exception e) {
//			log.error("ʵʱ��Ƶ�࿴ ����ʵʱ��Ƶ���״̬����: " + e.getMessage());
//			log.error("����SQL: " + strBuff.toString());
//		} finally {
//			DaoSupport.close(statement);
//			DaoSupport.close(conn);
//		}
//		strBuff = null;
//		return vo;
//	}
    
    /**
     * ȡ��ʵʱ��ƵURL�������
     * @param MonitorProgramQueryVO
     * @param downXML �����·���XMLЭ��
     * @param runType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
     * @return MonitorProgramQueryVO
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetProgramInfoByDownIndex(MonitorProgramQueryVO vo, String downXML) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// �����Ƿ��Ѿ�����һ��һ����
		strBuff.append("select * from monitorprogramquery where statusFlag = 1 order by lastDatatime");
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				try {
					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				} catch (Exception ex) {
					vo.setRtvsPort(0);
				}
				vo.setSmgURL(rs.getString("smgURL"));
				
				try {
					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				} catch (Exception ex) {
					vo.setSmgIndex(0);
				}
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				vo.setIsReStartRTVS(0);
				break;
			}
			
		} catch (Exception e) {
			log.error("ʵʱ��Ƶ�࿴ ȡ��ʵʱ��Ƶ��Ŵ���: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);		
		}
		strBuff = null;

		return vo;
	}
    
    /**
     *  ȡ��ʵʱ��Ƶ�鲥��ַ�Ͷ˿ں�
     * @param vo
     * @return
     * @throws DaoException
     */
//    public static MonitorProgramQueryVO GetRTVSIpPort(MonitorProgramQueryVO vo) throws DaoException {
//
//		Statement statement = null;
//		Connection conn = DaoSupport.getJDBCConnection();
//
//		ResultSet rs = null;
//		
//		StringBuffer strBuff = new StringBuffer();
//		
//		// ȡ����ؽ�ĿƵ����Ϣ
//		strBuff.append("select *  from monitorprogramquery where RunType = 1 order by lastDatatime");
//		
//		try {
//			statement = conn.createStatement();
//			
//			rs = statement.executeQuery(strBuff.toString());
//			
//			while(rs.next()){
//				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
//				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
//				
//				vo.setRtvsIP(rs.getString("rtvsIP"));
//				
//				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
//				
//				vo.setSmgURL(rs.getString("smgURL"));
//				
//				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
//				
//				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
//				
//				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
//				
//				break;
//			}
//			
//		} catch (Exception e) {
//			log.error("ʵʱ��Ƶ�࿴ ȡ��ʵʱ��Ƶ��Ŵ���: " + e.getMessage());
//		} finally {
//			DaoSupport.close(rs);
//			DaoSupport.close(statement);
//			DaoSupport.close(conn);
//		}
//		return vo;
//	}
    
    
    public static void updataProgramPatrolGroup(List<MonitorProgramQueryVO> programPatrolList, String upXMLString) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		int count = 0;
		upXMLString = upXMLString.replaceAll("�ɹ�", "");
		
		StringBuffer strBuff = null;
		// ȡ����ؽ�ĿƵ����Ϣ
		
		for(int i=0; i<programPatrolList.size(); i++) {
			MonitorProgramQueryVO vo = programPatrolList.get(i);
			strBuff = new StringBuffer();
			// ȡ����ؽ�ĿƵ����Ϣ
			strBuff.append("select *  from monitorprogramquery where statusFlag = 2 and patrolGroupIndex=" + vo.getPatrolGroupIndex() + "  order by lastDatatime");
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
					break;
				}
				
			} catch (Exception e) {
				log.error("ʵʱ��Ƶ�࿴ ȡ��ʵʱ��Ƶ��Ŵ���: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
			
			try {
				strBuff = new StringBuffer();
				strBuff.append("update monitorprogramquery c set ");
				strBuff.append(" xml = '" + upXMLString + "', ");
				// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
				strBuff.append(" RunType = 3, ");
				strBuff.append(" Freq = " + vo.getFreq() + ", ");
				strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
				strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
				strBuff.append(" where rtvsIndex = " + vo.getRtvsIndex());
			
				statement = conn.createStatement();
				
				statement.execute(strBuff.toString());
				
			} catch (Exception e) {
				log.error("ʵʱ��Ƶ�࿴ ����ʵʱ��Ƶ���״̬����: " + e.getMessage());
				log.error("����SQL: " + strBuff.toString());
				e.printStackTrace();
			} finally {
				DaoSupport.close(statement);
			}
			strBuff = null;
			if ((count-1) == i) {
				break;
			}
		}
		
		DaoSupport.close(conn);
		return;
	}
    
    /**
     *  ȡ����ѯ�����鲥��ַ�Ͷ˿ں�
     * @param vo
     * @return
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetProgramPatrolInfo(MonitorProgramQueryVO vo) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// ȡ����ѯ������Ϣ
		strBuff.append("select *  from monitorprogramquery where statusFlag = 2 and patrolGroupIndex= " + vo.getPatrolGroupIndex() + " order by lastDatatime");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				
				vo.setFreq(Integer.parseInt(rs.getString("Freq")));
				
				vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				break;
			}
			
		} catch (Exception e) {
			log.error("ȡ����ѯ���Ӵ���: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return vo;
	}
    
    /**
     *  ȡ���ֶ�ѡ̨���Զ��ֲ��鲥��ַ�Ͷ˿ں�
     * @param vo
     * @param statusFlag 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
     * @return
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetChangeProgramInfo(MonitorProgramQueryVO vo, int statusFlag) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// ȡ���ֶ�ѡ̨���Զ��ֲ���Ϣ
		// 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
		strBuff.append("select *  from monitorprogramquery where statusFlag = " + statusFlag);
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				
				vo.setFreq(Integer.parseInt(rs.getString("Freq")));
				
				vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				break;
			}
			
		} catch (Exception e) {
			log.error("ȡ���ֶ�ѡ̨���Զ��ֲ�����: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return vo;
	}

    /**
     *  ȡ���ֶ�ѡ̨���Զ��ֲ��鲥��ַ�Ͷ˿ں�
     * @param vo
     * @param statusFlag 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
     * @return
     * @throws DaoException
     */
    public static List<MonitorProgramQueryVO> GetChangeProgramInfoList(List<MonitorProgramQueryVO> list) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// ȡ���ֶ�ѡ̨���Զ��ֲ���Ϣ
		// 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
		strBuff.append("select *  from monitorprogramquery where statusFlag in(1,2,3,4,5,6);");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				MonitorProgramQueryVO vo= new MonitorProgramQueryVO();
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				list.add(vo);
			}
			
		} catch (Exception e) {
			log.error("ȡ���ֶ�ѡ̨���Զ��ֲ�����: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return list;
	}   
    
    /**
     *  ȡ���ֶ�¼���鲥��ַ�Ͷ˿ں�
     * @param vo
     * @return
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetManualRecordProgramInfo(MonitorProgramQueryVO vo) throws DaoException {

		Statement statement = null;
		if (vo.getFreq() != 0 && vo.getServiceID() != 0) {
			
			Connection conn = DaoSupport.getJDBCConnection();
	
			ResultSet rs = null;
			
			StringBuffer strBuff = new StringBuffer();
			
			// ȡ���ֶ�ѡ̨���Զ��ֲ���Ϣ
			// 0:���� 1:RTVSʹ�� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨���Զ��ֲ�
			strBuff.append("select *  from monitorprogramquery where Freq = " + vo.getFreq() + " and ServiceID=" + vo.getServiceID());
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
					
//					vo.setRtvsURL(rs.getString("rtvsURL"));
					
					vo.setRtvsIP(rs.getString("rtvsIP"));
					
					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
					
					vo.setSmgURL(rs.getString("smgURL"));
					
					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
					
					vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
					
					vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
					
					return vo;
				}
				
			} catch (Exception e) {
				log.error("ȡ���ֶ�ѡ̨���Զ��ֲ�����: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		}
		return vo;
	}
    
    public static List GetWatchAndSeeVOList() throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		List monitorProgramList = new ArrayList();
		
		StringBuffer strBuff = new StringBuffer();
		
		// ȡ����ѯ������Ϣ
		strBuff.append("select *  from monitorprogramquery ");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				MonitorProgramQueryVO vo = new MonitorProgramQueryVO();
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				
				monitorProgramList.add(vo);
				
				break;
			}
			
		} catch (Exception e) {
			log.error("ȡ����ѯ���Ӵ���: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return monitorProgramList;
	}
    
}
