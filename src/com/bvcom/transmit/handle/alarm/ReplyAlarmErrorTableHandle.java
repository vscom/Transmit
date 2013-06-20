package com.bvcom.transmit.handle.alarm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.vo.alarm.ReplyAlarmErrorTableVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class ReplyAlarmErrorTableHandle {
	
	private static Logger log = Logger.getLogger(ReplyAlarmErrorTableHandle.class.getSimpleName());
	
    public void upReplyAlarmErrorTable(String upXml, String errMsg) throws DaoException {
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("insert into replyalarmerrortable(replyXML, errorMsg, lastDateTime) ");
		strBuff.append(" values(");
		strBuff.append("'" + upXml + "', ");
		strBuff.append("'" + errMsg + "', ");
		strBuff.append("'" + CommonUtility.getDateTime() + "')");
		try {
			statement = conn.createStatement();
			statement.executeUpdate(strBuff.toString());
		} catch (Exception e) {
			log.error("报警信息入库错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
    }
    
    public void delReplyAlarmErrorTable(ReplyAlarmErrorTableVO vo) throws DaoException {
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		strBuff.append("delete from replyalarmerrortable where id=" + vo.getId());
		
		try {
			statement = conn.createStatement();
			conn.setAutoCommit(false);
			statement.execute(strBuff.toString());
			conn.commit();
		} catch (Exception e) {
			log.error("删除报警上报失败表(replyalarmerrortable)信息错误: " + e.getMessage());
			log.error("删除报警上报失败表(replyalarmerrortable)信息错误 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
    }
    
    public List getReplyAlarmErrorTaskList() {
    	List alarmList = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		Connection conn;
		
		try {
			conn = DaoSupport.getJDBCConnection();
		
			try {
				StringBuffer strBuff = new StringBuffer();
				strBuff.append("select * from replyalarmerrortable order by  lastdatetime;");
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				while(rs.next()){
					ReplyAlarmErrorTableVO vo = new ReplyAlarmErrorTableVO();
					
					vo.setId(Integer.parseInt(rs.getString("id")));
					vo.setReplyXML(rs.getString("replyXML"));
					vo.setErrorMsg(rs.getString("errorMsg"));
					vo.setLastDateTime(rs.getString("lastDateTime"));
					alarmList.add(vo);
				}
				
			} catch (Exception e) {
				log.error("报警上报信息查询错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		} catch (DaoException e1) {
			log.error("报警上报信息查询错误: " + e1.getMessage());
		}
    	
    	return alarmList;
    }
    
}
