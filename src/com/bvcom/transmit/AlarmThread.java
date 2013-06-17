package com.bvcom.transmit;

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
import com.bvcom.transmit.handle.alarm.ReplyAlarmErrorTableHandle;
import com.bvcom.transmit.parse.alarm.AlarmSearchFSetParse;
import com.bvcom.transmit.parse.alarm.AlarmSearchPSetParse;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;

/**
 * ��������Ϣ
 * 
 * @author BVCOM
 * 
 */
public class AlarmThread extends Thread {

	private static Logger log = Logger.getLogger(AlarmThread.class
			.getSimpleName());

	// private String upString = new String();
	private List alarmList = new ArrayList();
	MSGHeadVO bsData = null;

	UtilXML xmlUtil = new UtilXML();

	public AlarmThread(List List, MSGHeadVO bsData) {
		this.alarmList = List;
		this.bsData = bsData;
	}

	public void run() {
		UtilXML utilXML = new UtilXML();

		Document upDoc = null;

		AlarmSearchPSetParse alarmSearchPSetParse = new AlarmSearchPSetParse();
		AlarmSearchFSetParse alarmSearchFSetParse = new AlarmSearchFSetParse();
		
		// 1. ��Ŀ��Ƶ�ʱ����жϣ� ��Ҫ���ж�ServerID
		// 2. ��alarmsearchtable���ݿ�� �Ѿ��з�������û�лָ��� ������
		boolean isSuccess=true;
		try {
			// ���±������ݿ�
			isSuccess=updateAlarmSearchTable(alarmList);
		} catch (Exception ex) {
			log.error("���±������ݿ�ʧ��: " + ex.getMessage());
		}

		try {
			// ȡ���ϱ����ݵ�AlarmID
			getAlarmIDFromAlarmSearchTable(alarmList);
		} catch (Exception ex) {
			log.error("ȡ���ϱ�����AlarmIDʧ��: " + ex.getMessage());
		}
		
		String upString = "";

		if (bsData.getStatusQueryType().equals("AlarmSearchPSet")) {
			// ��Ŀ��ر���

			upString = alarmSearchPSetParse
					.createForUpXML(bsData, alarmList, 0);
		} else if (bsData.getStatusQueryType().equals("AlarmSearchFSet")) {
			// Ƶ����ر���
			upString = alarmSearchFSetParse
					.createForUpXML(bsData, alarmList, 0);
		}

		try {
			if (upString != null && !upString.equals("")) {
				// log.info("�����ϱ�XML: " + upString);
				// �رձ�����־��Ϣ
				// log.info("�����ϱ���Ϣ:\n" +
				// xmlUtil.replaceAlarmXMLMsgHeader(getString, bsData));
				//log.info("�ϱ����ı�����Ϣ��"+upString);
				if(isSuccess){
					boolean flag = xmlUtil.SendUpXML(xmlUtil.replaceAlarmXMLMsgHeader(upString,
							bsData, 0), bsData);
					//����ϱ����ɹ�����ı����ɹ�״̬ IsSuccess  0:�ɹ� 1��ʧ��
					if(flag){
						log.info("�����ϱ��ɹ���");
					}else{
						upAlarmISSuccess(alarmList);
						log.info("�����ϱ�ʧ�ܣ���¼alarmsearchtable�������ݿ�");
					}
					
				}else{
					ReplyAlarmErrorTableHandle replyAlarmErrorTableHandle = new ReplyAlarmErrorTableHandle();
            		try {
            			replyAlarmErrorTableHandle.upReplyAlarmErrorTable(upString, " ");
            		} catch (Exception e) {
            			log.error("ReplyAlarmErrorTable�����ϱ������������ʧ��"+ e.getMessage());
            		}
				}
			} else {
				log.debug("��������upString: " + upString);
			}
		} catch (Exception e) {
			log.error("������Ŀ���޻򿪹��ϱ���Ϣʧ��: " + e.getMessage());
		}
		alarmList = null;
		// log.info("alarmList Size: " + alarmList.size());

		if ((CommonUtility.getAlarmDatebaseDumpCount() % 10000) == 0) {
			// ÿ10000��������¼����ʼ�Ա������ݿ���е��� alarmsearchtable -->
			// alarmhistorysearchtable
			log.warn("\n\n\n##### Start ##### �Ա������ݿ���е��� alarmsearchtable --> alarmhistorysearchtable |||-_-||| ");
			try {
				dumpAlarmDatebaseDump();
			} catch (Exception ex) {
				log.error("�Ա������ݿ���е���ʧ��: " + ex.getMessage());
			}
			log.warn("###### End ###### �Ա������ݿ���е��� alarmsearchtable --> alarmhistorysearchtable |||-_-||| \n\n");
		}
	}

	/**
	 * ��ʼ�Ա������ݿ���е��� alarmsearchtable --> alarmhistorysearchtable
	 */
	private void dumpAlarmDatebaseDump() throws DaoException {
		// insert into alarmhistorysearchtable select * from alarmsearchtable
		// where Alarmtype = 2
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		boolean isErr = false;
		String hisTime = CommonUtility.getHourOfDayBefOrAftNHour(CommonUtility.getDateTime(), -12);

		StringBuffer strBuff = new StringBuffer();
		strBuff.append("insert into alarmhistorysearchtable select * from alarmsearchtable where AlarmValue = 2 and lastdatetime < '"+ hisTime + "'");
		try {
			synchronized (this) {
				statement = conn.createStatement();
				log.debug("�������ݿ⵼��SQL: " + strBuff.toString());
				statement.executeUpdate(strBuff.toString());
			}
		} catch (Exception e) {
			log.error("�������ݿ⵼�����: " + e.getMessage());
			log.error("�������ݿ⵼�����SQL: " + strBuff.toString());
			isErr = true;
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;

		if (!isErr) {
			strBuff = new StringBuffer();
			strBuff.append("delete from alarmsearchtable where AlarmValue = 2 and lastdatetime < '"+ hisTime + "'");
			try {
				synchronized (this) {
					statement = conn.createStatement();
					log.debug("ɾ���������ݿ�SQL: " + strBuff.toString());
					statement.executeUpdate(strBuff.toString());
				}

			} catch (Exception e) {
				log.error("ɾ���������ݿ����: " + e.getMessage());
				log.error("ɾ���������ݿ����SQL: " + strBuff.toString());
				isErr = true;
			} finally {
				DaoSupport.close(statement);
			}
			strBuff = null;
		}

		DaoSupport.close(conn);
	}

	/**
	 * ȡ�ñ���ID
	 * 
	 * @param alarmList
	 * @throws DaoException
	 */
	private void getAlarmIDFromAlarmSearchTable(List alarmList)
			throws DaoException {
		if (alarmList == null || alarmList.size() <= 0) {
			if (alarmList != null) {
				log.debug("getAlarmIDFromAlarmSearchTable alarmList.size(): "
						+ alarmList.size());
			} else {
				log.debug("getAlarmIDFromAlarmSearchTable alarmList.size(): "
						+ alarmList);
			}
			return;
		}
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		ResultSet rs = null;

		for (int i = 0; i < alarmList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			AlarmSearchPSetVO alarmSearchPSet = (AlarmSearchPSetVO) alarmList
					.get(i);

			strBuff.append("select id FROM alarmsearchtable ");
			strBuff.append(" where freq = " + alarmSearchPSet.getFreq() + " ");

			if (alarmSearchPSet.getAlarmType().equals("AlarmSearchPSet")) {
				// ��Ŀ��ر���
				strBuff.append(" and ServiceID = "
						+ alarmSearchPSet.getServiceID() + " ");
			}

			strBuff.append(" and AlarmType = " + alarmSearchPSet.getType()
					+ " ");
			strBuff.append(" order by id desc limit 1");

			try {
				statement = conn.createStatement();

				rs = statement.executeQuery(strBuff.toString());
				log.debug("ȡ�ñ���ID: " + strBuff.toString());
				while (rs.next()) {
					alarmSearchPSet.setAlarmID(rs.getString("id"));
					log.debug("AlarmID: " + alarmSearchPSet.getAlarmID());
				}
			} catch (Exception e) {
				log.error("ȡ�ý�Ŀ�����Ǵ���1: " + e.getMessage());
				log.error("ȡ�ý�Ŀ�����Ǵ���1 SQL: " + strBuff.toString());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			rs = null;
			statement = null;
		}

		DaoSupport.close(conn);
	}

	/**
	 * ���±������ݿ�
	 * 
	 * @param alarmList
	 * @throws DaoException
	 */
	private boolean updateAlarmSearchTable(List alarmList) throws DaoException {
		boolean isSuccess=true;
		if (alarmList == null || alarmList.size() <= 0) {

			if (alarmList != null) {
				log.debug("updateAlarmSearchTable alarmList.size(): "
						+ alarmList.size());
			} else {
				log.debug("updateAlarmSearchTable alarmList.size(): "
						+ alarmList);
			}
			return true;
		}
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		for (int i = 0; i < alarmList.size(); i++) {
			AlarmSearchPSetVO alarmSearchPSet = (AlarmSearchPSetVO) alarmList
					.get(i);
			StringBuffer strBuff = new StringBuffer();
			StringBuffer sql =new StringBuffer();
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
				/**���������Ϣ���ļ�*/
	    		PrintWriter pw;
				try {
					pw = new PrintWriter(new File("D:/AlarmThreadException.log"));
					e.printStackTrace(pw);
					pw.flush();
					pw.close();
				} catch (Exception e1) {
				}
				/******/
			}finally{
				DaoSupport.close(statement);
				DaoSupport.close(rs);
			}
			// }else{
			//				
			// }
			if (falg) {
				if (alarmSearchPSet.getValue() == 1) {
					// ��������
					/*
					 * insert into alarmsearchtable(freq, serviceID, videoPID,
					 * audioPID, AlarmType, AlarmDesc, AlarmValue, AlarmTime,
					 * Lastdatatime) values(1, 1, 1, 1, 1, '����', 1, '2002-08-17
					 * 15:30:00', '2002-08-17 15:30:00')
					 */
					strBuff
							.append("insert into alarmsearchtable(freq, serviceID, videoPID, audioPID, AlarmType, AlarmDesc, AlarmValue, AlarmStartTime, Lastdatetime) ");
					strBuff.append(" values(");
					strBuff.append(alarmSearchPSet.getFreq() + ", ");
					strBuff.append(alarmSearchPSet.getServiceID() + ", ");
					strBuff.append(alarmSearchPSet.getVideoPID() + ", ");
					strBuff.append(alarmSearchPSet.getAudioPID() + ", ");
					strBuff.append(alarmSearchPSet.getType() + ", ");
					strBuff.append("'" + alarmSearchPSet.getDesc() + "', ");
					strBuff.append(alarmSearchPSet.getValue() + ", ");
					strBuff.append("'" + alarmSearchPSet.getTime() + "', ");
					strBuff.append("'" + CommonUtility.getDateTime() + "')");

				} else if (alarmSearchPSet.getValue() == 2) {
					// �����ָ�
					strBuff.append("update alarmsearchtable set ");
					strBuff.append(" VideoPID = "
							+ alarmSearchPSet.getVideoPID() + ", ");
					strBuff.append(" AudioPID = "
							+ alarmSearchPSet.getAudioPID() + ", ");
					strBuff.append(" AlarmDesc = '" + alarmSearchPSet.getDesc()
							+ "', ");
					strBuff.append(" AlarmValue = "
							+ alarmSearchPSet.getValue() + ", ");
					strBuff.append(" AlarmEndTime = '"
							+ alarmSearchPSet.getTime() + "', ");
					strBuff.append(" Lastdatetime = '"
							+ CommonUtility.getDateTime() + "' ");

					strBuff.append(" where freq = " + alarmSearchPSet.getFreq()
							+ " ");
					strBuff.append(" and ServiceID = "
							+ alarmSearchPSet.getServiceID() + " ");
					strBuff.append(" and AlarmType = "
							+ alarmSearchPSet.getType() + " ");
					strBuff.append(" and AlarmValue = 1 ");
					//�жϸñ����ָ� �ķ���״̬�Ƿ��Ѿ������ϱ�  Ji Long 2011-06-28
					//���û�������ϱ���ûָ���ϢҲ���뱨�������� 
					//�ȴ���������״̬�ϱ��ɹ�����ϱ� �ָ�
					sql.append("SELECT IsSuccess FROM alarmsearchtable where ");
					sql.append(" where freq = " + alarmSearchPSet.getFreq());
					sql.append(" and ServiceID = " + alarmSearchPSet.getServiceID());
					sql.append(" and AlarmType = " + alarmSearchPSet.getType());
					sql.append(" and AlarmValue = 1 ");
					try {
						statement = conn.createStatement();
						rs=statement.executeQuery(sql.toString());
						while(rs.next()){
							int success=rs.getInt("IsSuccess");
							if(success==1){
								isSuccess=false;
							}
						}
					} catch (Exception e) {
						log.debug("��ѯ�����Ƿ񲹱�����: " + e.getMessage());
						log.debug("��ѯ�����Ƿ񲹱�SQL: " + sql.toString());
					}finally{
						DaoSupport.close(statement);
					}
				} else {
					continue;
				}
				try {
					synchronized (this) {
						statement = conn.createStatement();
						log.debug("�������SQL: " + strBuff.toString());
						statement.executeUpdate(strBuff.toString());
					}

				} catch (Exception e) {
					log.error("������Ϣ������: " + e.getMessage());
					log.error("������Ϣ������SQL: " + strBuff.toString());
				} finally {
					DaoSupport.close(statement);
				}
				strBuff = null;
				sql=null;
			}
		}

		DaoSupport.close(conn);
		return isSuccess;
	}
	/**
	 * 
	 * @param alarmList
	 * @throws DaoException
	 */
	private void upAlarmISSuccess(List alarmList)throws DaoException {

		if (alarmList == null || alarmList.size() <= 0) {
			if (alarmList != null) {
				log.debug("getAlarmIDFromAlarmSearchTable alarmList.size(): "
						+ alarmList.size());
			} else {
				log.debug("getAlarmIDFromAlarmSearchTable alarmList.size(): "
						+ alarmList);
			}
			return;
		}
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		ResultSet rs = null;

		for (int i = 0; i < alarmList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			AlarmSearchPSetVO alarmSearchPSet = (AlarmSearchPSetVO) alarmList.get(i);

			strBuff.append("update alarmsearchtable ");
			strBuff.append("set IsSuccess = 1 ");
			strBuff.append(" where freq = " + alarmSearchPSet.getFreq() + " ");

			if (alarmSearchPSet.getAlarmType().equals("AlarmSearchPSet")) {
				// ��Ŀ��ر���
				strBuff.append(" and ServiceID = "
						+ alarmSearchPSet.getServiceID() + " ");
			}
			strBuff.append(" and AlarmType = " + alarmSearchPSet.getType()
					+ " ");
			strBuff.append(" order by id desc limit 1");
			try {
				statement = conn.createStatement();
				statement.executeUpdate(strBuff.toString());
			} catch (Exception e) {
				log.error("ɾ���ϱ�ʧ�ܱ�����Ϣ����1: " + e.getMessage());
				log.error("ɾ���ϱ�ʧ�ܱ�����Ϣ����1 SQL: " + strBuff.toString());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			rs = null;
			statement = null;
		}

		DaoSupport.close(conn);
	
	}
}
