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
 * 处理报警信息
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
		
		// 1. 节目和频率报警判断， 主要是判断ServerID
		// 2. 从alarmsearchtable数据库里， 已经有发生并且没有恢复， 不处理
		boolean isSuccess=true;
		try {
			// 更新报警数据库
			isSuccess=updateAlarmSearchTable(alarmList);
		} catch (Exception ex) {
			log.error("更新报警数据库失败: " + ex.getMessage());
		}

		try {
			// 取得上报数据的AlarmID
			getAlarmIDFromAlarmSearchTable(alarmList);
		} catch (Exception ex) {
			log.error("取得上报数据AlarmID失败: " + ex.getMessage());
		}
		
		String upString = "";

		if (bsData.getStatusQueryType().equals("AlarmSearchPSet")) {
			// 节目相关报警

			upString = alarmSearchPSetParse
					.createForUpXML(bsData, alarmList, 0);
		} else if (bsData.getStatusQueryType().equals("AlarmSearchFSet")) {
			// 频率相关报警
			upString = alarmSearchFSetParse
					.createForUpXML(bsData, alarmList, 0);
		}

		try {
			if (upString != null && !upString.equals("")) {
				// log.info("报警上报XML: " + upString);
				// 关闭报警日志信息
				// log.info("报警上报信息:\n" +
				// xmlUtil.replaceAlarmXMLMsgHeader(getString, bsData));
				//log.info("上报中心报警信息："+upString);
				if(isSuccess){
					boolean flag = xmlUtil.SendUpXML(xmlUtil.replaceAlarmXMLMsgHeader(upString,
							bsData, 0), bsData);
					//如果上报不成功则更改报警成功状态 IsSuccess  0:成功 1：失败
					if(flag){
						log.info("报警上报成功！");
					}else{
						upAlarmISSuccess(alarmList);
						log.info("报警上报失败，记录alarmsearchtable报警数据库");
					}
					
				}else{
					ReplyAlarmErrorTableHandle replyAlarmErrorTableHandle = new ReplyAlarmErrorTableHandle();
            		try {
            			replyAlarmErrorTableHandle.upReplyAlarmErrorTable(upString, " ");
            		} catch (Exception e) {
            			log.error("ReplyAlarmErrorTable报警上报出错数据入库失败"+ e.getMessage());
            		}
				}
			} else {
				log.debug("报警数据upString: " + upString);
			}
		} catch (Exception e) {
			log.error("报警节目门限或开关上报信息失败: " + e.getMessage());
		}
		alarmList = null;
		// log.info("alarmList Size: " + alarmList.size());

		if ((CommonUtility.getAlarmDatebaseDumpCount() % 10000) == 0) {
			// 每10000个报警记录，开始对报警数据库进行导表 alarmsearchtable -->
			// alarmhistorysearchtable
			log.warn("\n\n\n##### Start ##### 对报警数据库进行导表 alarmsearchtable --> alarmhistorysearchtable |||-_-||| ");
			try {
				dumpAlarmDatebaseDump();
			} catch (Exception ex) {
				log.error("对报警数据库进行导表失败: " + ex.getMessage());
			}
			log.warn("###### End ###### 对报警数据库进行导表 alarmsearchtable --> alarmhistorysearchtable |||-_-||| \n\n");
		}
	}

	/**
	 * 开始对报警数据库进行导表 alarmsearchtable --> alarmhistorysearchtable
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
				log.debug("报警数据库导表SQL: " + strBuff.toString());
				statement.executeUpdate(strBuff.toString());
			}
		} catch (Exception e) {
			log.error("报警数据库导表错误: " + e.getMessage());
			log.error("报警数据库导表出错SQL: " + strBuff.toString());
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
					log.debug("删除报警数据库SQL: " + strBuff.toString());
					statement.executeUpdate(strBuff.toString());
				}

			} catch (Exception e) {
				log.error("删除报警数据库错误: " + e.getMessage());
				log.error("删除报警数据库出错SQL: " + strBuff.toString());
				isErr = true;
			} finally {
				DaoSupport.close(statement);
			}
			strBuff = null;
		}

		DaoSupport.close(conn);
	}

	/**
	 * 取得报警ID
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
				// 节目相关报警
				strBuff.append(" and ServiceID = "
						+ alarmSearchPSet.getServiceID() + " ");
			}

			strBuff.append(" and AlarmType = " + alarmSearchPSet.getType()
					+ " ");
			strBuff.append(" order by id desc limit 1");

			try {
				statement = conn.createStatement();

				rs = statement.executeQuery(strBuff.toString());
				log.debug("取得报警ID: " + strBuff.toString());
				while (rs.next()) {
					alarmSearchPSet.setAlarmID(rs.getString("id"));
					log.debug("AlarmID: " + alarmSearchPSet.getAlarmID());
				}
			} catch (Exception e) {
				log.error("取得节目高清标记错误1: " + e.getMessage());
				log.error("取得节目高清标记错误1 SQL: " + strBuff.toString());
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
	 * 更新报警数据库
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
			// 过滤重复报警 吉龙
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
					//log.info("该报警已存在并且状态一致 则丢弃");
					continue;
				} else {
					falg = true;
					strBuff = new StringBuffer();
				}
			} catch (Exception e) {
				log.error("过滤重复报警错误: " + e.getMessage());
				/**输出错误信息到文件*/
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
					// 报警发生
					/*
					 * insert into alarmsearchtable(freq, serviceID, videoPID,
					 * audioPID, AlarmType, AlarmDesc, AlarmValue, AlarmTime,
					 * Lastdatatime) values(1, 1, 1, 1, 1, '描述', 1, '2002-08-17
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
					// 报警恢复
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
					//判断该报警恢复 的发生状态是否已经正常上报  Ji Long 2011-06-28
					//如果没有正常上报则该恢复信息也存入报警补报表 
					//等待报警发生状态上报成功后才上报 恢复
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
						log.debug("查询报警是否补报错误: " + e.getMessage());
						log.debug("查询报警是否补报SQL: " + sql.toString());
					}finally{
						DaoSupport.close(statement);
					}
				} else {
					continue;
				}
				try {
					synchronized (this) {
						statement = conn.createStatement();
						log.debug("报警入库SQL: " + strBuff.toString());
						statement.executeUpdate(strBuff.toString());
					}

				} catch (Exception e) {
					log.error("报警信息入库错误: " + e.getMessage());
					log.error("报警信息入库出错SQL: " + strBuff.toString());
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
				// 节目相关报警
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
				log.error("删除上报失败报警信息错误1: " + e.getMessage());
				log.error("删除上报失败报警信息错误1 SQL: " + strBuff.toString());
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
