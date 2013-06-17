package com.bvcom.transmit.handle.alarm;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.alarm.AlarmSearchPSetParse;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.video.domain.AlarmTimeDao;
import com.bvcom.transmit.util.AlarmSwitchMemory;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;

/**
 * ������ѯ
 * 
 * @author Bian Jiang
 * 
 */
public class AlarmSearchPSetHandle {

	private static Logger log = Logger.getLogger(AlarmSearchPSetHandle.class
			.getSimpleName());

	private MSGHeadVO bsData = new MSGHeadVO();

	private String downString = new String();

	private UtilXML utilXML = new UtilXML();

	public AlarmSearchPSetHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}

	/**
	 * 1. �·���IPM 2. ����IPM���ص���Ϣ���滻Freq����Ϣ 3. �ϱ�����
	 * 
	 */
	public void downXML() {
		String upString = "";
		List IPMSendList = new ArrayList();

		boolean isErr = false;

		Document document = null;
		try {
			document = utilXML.StringToXML(this.downString);
		} catch (CommonException e) {
			log.error("������ѯStringToXML Error: " + e.getMessage());
		}
		;

		AlarmSearchPSetParse AlarmSearchPSetParse = new AlarmSearchPSetParse();
		List<AlarmSearchPSetVO> voList = AlarmSearchPSetParse
				.getDownList(document);

		for (int i = 0; i < voList.size(); i++) {
			AlarmSearchPSetVO AlarmSearchPSet = voList.get(i);
			CommonUtility.checkIPMChannelIndex(AlarmSearchPSet.getIndex(),
					IPMSendList);
			break;
		}

		for (int i = 0; i < IPMSendList.size(); i++) {
			IPMInfoVO smg = (IPMInfoVO) IPMSendList.get(i);
			try {
				// ������ѯ��Ϣ�·� timeout 1000*10 10��
				upString = utilXML.SendDownXML(this.downString, smg.getURL(),
						CommonUtility.CONN_WAIT_TIMEOUT, bsData);
			} catch (CommonException e) {
				log.error("��IPM�·�������ѯ���ó���" + smg.getURL());
				upString = "";
			}
		}

		try {
			if (upString == null || upString.equals("")) {
				upString = utilXML.getReturnXML(bsData, 1);
			}
			document = utilXML.StringToXML(upString);
		} catch (CommonException e) {
			log.error("������ѯStringToXML Error: " + e.getMessage());
			isErr = true;
		}
		;

		if (!isErr) {
			// �滻Ƶ�����Ϣ
			AlarmSearchPSetParse.replaceFreqInfo(document, voList);
			document.setXMLEncoding("GB2312");
			upString = document.asXML();
		} else {
			upString = utilXML.getReturnXML(bsData, 1);
		}

		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("������ѯ�ظ�ʧ��: " + e.getMessage());
		}

	}

	/**
	 * ��Ŀ���������ϱ� 1. �����ݿ�ȡ��Freq���б���Ϣ 2. ����XML������Ϣ 3. �����޸ĺ��XML
	 * 
	 */
	public String upXML() {
		// ���ݿ�ȡ����ϸƵ����Ϣ
		List<AlarmSearchPSetVO> rsList = new ArrayList<AlarmSearchPSetVO>();

		Document document = null;
		try {
			document = utilXML.StringToXML(this.downString);
		} catch (CommonException e) {
			log.error("������ѯStringToXML Error: " + e.getMessage());
			// TODO �ϱ�������Ϣ
		}

		// ȡ���ϱ�Ƶ����Ϣ
		AlarmSearchPSetParse AlarmSearchPSetParse = new AlarmSearchPSetParse();
		List<AlarmSearchPSetVO> freqList = AlarmSearchPSetParse
				.getUpList(document);

		this.getFreqInfoFromDB(freqList, rsList);

		if (rsList.size() > 0) {
			// ����XML��Ϣ��Freq����Ϣ
			AlarmSearchPSetParse.replaceFreqInfo(document, rsList);
		} else {
			return "";
		}

		return document.asXML();
	}

	/**
	 * �����ݿ��ѯͨ����ϸ��Ϣ
	 * 
	 * @param freqList
	 * @param rsList
	 */
	private void getFreqInfoFromDB(List freqList, List rsList) {
		StringBuffer strBuff = new StringBuffer();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		int freq = 0;
		int serviceID = 0;

		try {
			conn = DaoSupport.getJDBCConnection();
			// SELECT * FROM transmit.channelstatus c where channelindex = 1 or
			// channelindex = 3

			for (int i = 0; i < freqList.size(); i++) {
				AlarmSearchPSetVO AlarmSearchPSet = (AlarmSearchPSetVO) freqList
						.get(i);
				strBuff = new StringBuffer();
				strBuff.append("SELECT * FROM channelscanlist c where ");
				strBuff.append(" lastflag = 1 and Freq = "
						+ AlarmSearchPSet.getFreq() + " and ServiceID = "
						+ AlarmSearchPSet.getServiceID());

				try {
					statement = conn.createStatement();
					rs = statement.executeQuery(strBuff.toString());

					while (rs.next()) {

						if (rs.getString("Freq") == null
								|| rs.getString("Freq").equals("")
								|| rs.getString("Freq").equals("0")) {
							continue;
						} else {
							freq = Integer.parseInt(rs.getString("Freq"));
							if (freq == 0) {
								continue;
							}
						}
						AlarmSearchPSetVO rsAlarmSearchPSet = new AlarmSearchPSetVO();

						rsAlarmSearchPSet.setFreq(Integer.parseInt(rs
								.getString("Freq")));
						if (rs.getString("ServiceID") == null
								|| rs.getString("ServiceID").equals("")
								|| rs.getString("ServiceID").equals("0")) {
							continue;
						} else {
							serviceID = Integer.parseInt(rs
									.getString("ServiceID"));
							if (serviceID == 0) {
								continue;
							}
						}
						rsAlarmSearchPSet.setServiceID(Integer.parseInt(rs
								.getString("ServiceID")));
						rsAlarmSearchPSet.setVideoPID(Integer.parseInt(rs
								.getString("VideoPID")));
						rsAlarmSearchPSet.setAudioPID(Integer.parseInt(rs
								.getString("AudioPID")));
						rsList.add(rsAlarmSearchPSet);
						break;
					}

				} catch (Exception e) {
					log.error("������Ϣ��ѯ��Ŀ��Ϣ����: " + e.getMessage());
					/**���������Ϣ���ļ�*/
		    		PrintWriter pw;
					try {
						pw = new PrintWriter(new File("D:/AlarmSearchPSetHandle.log"));
						e.printStackTrace(pw);
						pw.flush();
						pw.close();
					} catch (Exception e1) {
					}
					/******/
				} finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
				}

				strBuff = null;
			}

		} catch (DaoException e1) {
			log.error("ͨ��״̬��ѯ���ݿ����: " + e1.getMessage());
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 * �жϸü����еı�����¼ ����״̬ �������״̬Ϊ0�����ñ���
	 * �����ж� ��������ͼ��������
	 * �ж��Ƿ��ظ������ظ�����
	 * @param alarmList
	 * @return List New List
	 */
	public static List getFreqFromDB(List alarmList, String type) {

		
		//TODO ���ӹ��� ����������ͼ �������� Ji Long 2011-06-16
		AlarmTimeDao dao=new AlarmTimeDao();
		alarmList=dao.select(alarmList);
		
		 Statement statement = null;
		 ResultSet rs = null;
		 Connection conn = null;
		List newList = new ArrayList();

		 try {
			conn = DaoSupport.getJDBCConnection();
	
			for (int i = 0; i < alarmList.size(); i++) {
				AlarmSearchPSetVO alarmSearchPSet = (AlarmSearchPSetVO) alarmList
						.get(i);
				/*
				 * StringBuffer strBuff = new StringBuffer(); strBuff.append("SELECT *
				 * FROM channelremapping c where "); strBuff.append("statusFlag = 1
				 * and Freq = " + alarmSearchPSet.getFreq());
				 *  // ����ǽ�Ŀ�����ļ���ServiceID if (type.equals("AlarmSearchPSet")) {
				 * strBuff.append(" and ServiceID = " +
				 * alarmSearchPSet.getServiceID()); }
				 * 
				 * try { statement = conn.createStatement(); rs =
				 * statement.executeQuery(strBuff.toString()); boolean isTrue =
				 * false; while (rs.next()) { isTrue = true; break; } if(isTrue) {
				 */
				// TODO
				// �����ж� ����ı�����Ӧ��Ƶ�ʻ��Ŀ �ı��������Ѿ��ر�
				// �򶪵��ñ���
				if (alarmIsList(alarmSearchPSet)) {
	//				log.info("�ñ��������Ѿ��ر�����Ƶ��" + alarmSearchPSet.getFreq() + "��Ŀid"
	//						+ alarmSearchPSet.getServiceID() + "��������"
	//						+ alarmSearchPSet.getAlarmType() + "");
				} else {
					try {
//						conn = DaoSupport.getJDBCConnection();
						StringBuffer strBuff = new StringBuffer();
						// �����ظ����� ����
						boolean falg = false;
						int serviceID = alarmSearchPSet.getServiceID();
						int freq = alarmSearchPSet.getFreq();
						int alarmValue = alarmSearchPSet.getValue();
						int alarmType = alarmSearchPSet.getType();
						// if(serviceID==0&&videoPID==0&&audioPID==0){
						try {
							statement = conn.createStatement();
							strBuff.append("select id FROM alarmsearchtable ");
							strBuff.append(" where freq = " + freq + " ");
							strBuff.append(" and ServiceID = " + serviceID + " ");
							strBuff.append(" and AlarmValue = " + alarmValue + " ");
							strBuff.append(" and AlarmType = " + alarmType + " ");
							strBuff.append(" and AlarmEndTime is " + "null" );
							rs = statement.executeQuery(strBuff.toString());
							if (rs.next()) {
								//log.info("�ñ����Ѵ��ڲ���״̬һ�� ����");
								continue;
							} else {
								falg = true;
								strBuff = new StringBuffer();
							}
						} catch (Exception e) {
							log.error("�����ظ���������: " + e.getMessage());
						}
						if(falg ){
							AlarmSearchPSetVO vo = new AlarmSearchPSetVO();
							vo.setAlarmType(alarmSearchPSet.getAlarmType());
							vo.setIndex(alarmSearchPSet.getIndex());
							vo.setFreq(alarmSearchPSet.getFreq());
							// if (type.equals("AlarmSearchPSet")) {
							vo.setServiceID(alarmSearchPSet.getServiceID());
							vo.setVideoPID(alarmSearchPSet.getVideoPID());
							vo.setAudioPID(alarmSearchPSet.getAudioPID());
							// }
							vo.setAlarmID(alarmSearchPSet.getAlarmID());
							vo.setReturnValue(alarmSearchPSet.getReturnValue());
							vo.setComment(alarmSearchPSet.getComment());
							vo.setType(alarmSearchPSet.getType());
							vo.setDesc(alarmSearchPSet.getDesc());
							vo.setValue(alarmSearchPSet.getValue());
							vo.setTime(alarmSearchPSet.getTime());
							newList.add(vo);
						}
					} catch (Exception e) {
						log.error("������Ϣ���˴���: " + e.getMessage());
					}finally{
						try {
							DaoSupport.close(statement); 
							DaoSupport.close(rs);
						}catch (DaoException e) {
							log.error("�ر����ݿ�ʧ��: " + e.getMessage()); 
						}
					}
					
				}
				/*
				 * } } catch (Exception e) { log.error("������Ϣ��ѯƵ����Ϣ����: " +
				 * e.getMessage()); } finally { DaoSupport.close(rs);
				 * DaoSupport.close(statement); }
				 * 
				 * strBuff = null; }
				 *  } catch (DaoException e1) { log.error("������Ϣ��ѯƵ����Ϣ����: " +
				 * e1.getMessage()); } finally { try { DaoSupport.close(conn); }
				 * catch (DaoException e) { log.error("�ر����ݿ�ʧ��: " + e.getMessage()); } }
				 */
			}
		 } catch (Exception e) {
				log.error("������Ϣ���˴���: " + e.getMessage());
		 }finally{
			try {
				DaoSupport.close(conn); 
			}catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage()); 
			}
		}
		return newList;
	}

	private static boolean alarmIsList(AlarmSearchPSetVO vo) {
		boolean temp = false;
		List<AlarmSwitch> list = AlarmSwitchMemory.alarmSwitchList;
		for (AlarmSwitch as : list) {
			if (as.getAlarmType() == vo.getType()) {
				if (vo.getServiceID() != 0) {
//					log.info("ȷ����Ŀ�յ�Э����Ƶ��" + vo.getFreq() + ", serviceid"
//							+ vo.getServiceID() + ", ��������" + vo.getType());
//					log.info("ȷ����Ŀ�ڴ��б���Ƶ��" + as.getFreq() + ", serviceid"
//							+ as.getServiceID() + ", ��������" + as.getAlarmType()
//							+ ", ����״̬" + as.getSwitchValue());
					if (as.getFreq().equals(vo.getFreq() + "")
							&& as.getServiceID().equals(vo.getServiceID() + "")
							&& as.getSwitchValue() == 0
							&& as.getSwitchType() == 1) {
						temp = true;
						break;
					}
				} else {
//					log.info("ȷ��Ƶ���յ�Э����Ƶ��" + vo.getFreq() + ", serviceid"
//							+ vo.getServiceID() + ", ��������" + vo.getType());
//					log.info("ȷ��Ƶ���ڴ��б���Ƶ��" + as.getFreq() + ", serviceid"
//							+ as.getServiceID() + ", ��������" + as.getAlarmType()
//							+ ", ����״̬" + as.getSwitchValue());
					if (as.getFreq().equalsIgnoreCase(vo.getFreq() + "")
							&& as.getSwitchValue() == 0
							&& as.getSwitchType() == 2) {
						temp = true;
						break;
					}
				}
			}
		}
		//log.info("��һ���жϺ�ý�Ŀ����״̬" + temp);
		if (temp == false) {
			for (AlarmSwitch as : list) {
				if (as.getAlarmType() == vo.getType()) {
					if (vo.getServiceID() != 0) {
//						log.info("���н�Ŀ�յ�Э����Ƶ��" + vo.getFreq() + ", serviceid"
//								+ vo.getServiceID() + ", ��������" + vo.getType());
//						log.info("���н�Ŀ�ڴ��б���Ƶ��" + as.getFreq() + ", serviceid"
//								+ as.getServiceID() + ", ��������"
//								+ as.getAlarmType() + ", ����״̬"
//								+ as.getSwitchValue());
						if ((as.getFreq().equals("arr")||as.getFreq().equals("ALL"))
								&& as.getServiceID().equals(vo.getServiceID() + "")
								&& as.getSwitchValue() == 0
								&& as.getSwitchType() == 1) {
							temp = true;
							break;
						}
					} else {
//						log.info("����Ƶ���յ�Э����Ƶ��" + vo.getFreq() + ", serviceid"
//								+ vo.getServiceID() + ", ��������" + vo.getType());
//						log.info("����Ƶ���ڴ��б���Ƶ��" + as.getFreq() + ", serviceid"
//								+ as.getServiceID() + ", ��������"
//								+ as.getAlarmType() + ", ����״̬"
//								+ as.getSwitchValue());
						if ((as.getFreq().equals("arr")||as.getFreq().equals("ALL"))
								&& as.getSwitchValue() == 0) {
							temp = true;
							break;
						}
					}
				}
			}
		}
		//log.info("���շ���ʱ �ý�Ŀ����״̬" + temp);
		return temp;
	}
}
