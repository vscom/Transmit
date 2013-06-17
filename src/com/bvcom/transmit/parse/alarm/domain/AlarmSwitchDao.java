package com.bvcom.transmit.parse.alarm.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.AlarmSwitchMemory;
import com.bvcom.transmit.util.DaoException;

public class AlarmSwitchDao {
	private static Logger log = Logger.getLogger(AlarmSwitchDao.class
			.getCanonicalName());

	/**
	 * 
	 * @return alarmSwitchList报警开关状集合 JI LONG 2011-5-12
	 */
	public List<AlarmSwitch> list() {
		List<AlarmSwitch> alarmSwitchList = new ArrayList<AlarmSwitch>();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		// freq,serviceid,switchtype,switchvalue,alarmtype
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();

			String sqlStr = "SELECT * FROM alarmswitch a;";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				AlarmSwitch alarmSwitch = new AlarmSwitch();
				alarmSwitch = new AlarmSwitch();
				alarmSwitch.setFreq(rs.getString("freq"));
				alarmSwitch.setServiceID(rs.getString("serviceid"));
				alarmSwitch.setSwitchType(rs.getInt("switchtype"));
				alarmSwitch.setSwitchValue(rs.getInt("switchvalue"));
				alarmSwitch.setAlarmType(rs.getInt("alarmtype"));
				alarmSwitchList.add(alarmSwitch);
			}
		} catch (Exception e) {
			log.error("查找报警开关状态集合错误: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		return alarmSwitchList;
	}

	/**
	 * 
	 * @param alarmSwitchList
	 *            需要保存的报警开关集合 保存时候做判断如果已经有责更新，没有责插入 JI LONG 2011-5-12
	 */
	public void save(List<AlarmSwitch> alarmSwitchList) {
		Statement statement = null;
		Connection conn = null;
		// 数据库中的开关状态
		List<AlarmSwitch> list =AlarmSwitchMemory.alarmSwitchList;
		try {
			conn = DaoSupport.getJDBCConnection();
			conn.setAutoCommit(false);
			// freq,serviceid,switchtype,switchvalue,alarmtype
			String sqlStr = "";
			for (int i = 0; i < alarmSwitchList.size(); i++) {
				AlarmSwitch as=alarmSwitchList.get(i);
				//如果开关类型为节目级
				if(as.getSwitchType()==1){
					//如果freq和serviceid都为arr
					if(as.getFreq().equals("arr")&&as.getServiceID().equals("arr")){
						delete(as.getSwitchType(),as.getFreq());
					//如果freq不等于arr servicid为arr	
					}else if(as.getServiceID().equals("arr")&&(!as.getFreq().equals("arr"))){
						delete(as.getSwitchType(),as.getFreq());
					}
				//如果开关类型为频率级 并且频率为arr时	
				}else{
					if(as.getFreq().equals("arr")){
						delete(as.getSwitchType(),"");
					}
				}
				AlarmSwitchMemory.alarmSwitchList=list();
				if(elementIsList(list,as)){
					sqlStr="update alarmswitch set switchvalue = "+as.getSwitchValue()+" where " +
					"freq = '"+as.getFreq()+"' and serviceid = '"+as.getServiceID()+"' and " +
					"switchtype = "+as.getSwitchType()+" and alarmtype = "+as.getAlarmType()+";" ;
				}else{
					sqlStr="insert into alarmswitch (freq,serviceid,switchtype,switchvalue,alarmtype)" +
					"values('"+as.getFreq()+"','"+as.getServiceID()+"',"+as.getSwitchType()+"," +
					as.getSwitchValue()+","+as.getAlarmType()+");";
				}
				try {
					statement = conn.createStatement();
					statement.executeUpdate(sqlStr);

				} catch (Exception e) {
					log.error("报警开关信息更新数据库错误: " + e.getMessage());
					log.error("报警开关信息更新数据库错误 SQL：\n" + sqlStr);
				} finally {
					DaoSupport.close(statement);
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("保存更新报警开关状态集合错误: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		AlarmSwitchMemory.alarmSwitchList=list();
//		log.info("更新后的开关状态"+AlarmSwitchMemory.alarmSwitchList);
	}

	/**
	 * 判断改报警开关是否已经存在
	 * @param alarmSwitchList
	 * @param alarmSwitch
	 * @return true存在
	 */
	private boolean elementIsList(List<AlarmSwitch> alarmSwitchList,
			AlarmSwitch alarmSwitch) {
		boolean temp = false;
		for (AlarmSwitch as : alarmSwitchList) {
			if (as.getFreq().equals(alarmSwitch.getFreq())
					&& as.getServiceID().equals(alarmSwitch.getServiceID())
					&& as.getSwitchType() == alarmSwitch.getSwitchType()
					&& as.getAlarmType() == alarmSwitch.getAlarmType()) {
				temp=true;
				break;
			}
		}
		return temp;
	}
	/**
	 * switchValue 1表示节目级  2表示频率级
	 * 如果开关为频率级 则把频率开关相关的所有记录删除
	 * 如果开关为节目级 并且 freq为arr  则把节目相关的所有记录删除
	 * 如果开关为节目级 并且 freq不等于arr  则把对应 freq下的 节目相关开关记录删除 
	 * @param switchValue
	 * @param freq
	 */
	private void delete(int switchValue,String freq){
		Statement statement = null;
		Connection conn = null;
		String sqlStr = "";
		if(freq.equals("")&&switchValue==2){
			sqlStr="delete FROM alarmswitch where switchtype = 2;";
		}else if(freq.equals("arr")&&switchValue==1){
			sqlStr="delete FROM alarmswitch where switchtype = 1;";
		}else if((!freq.equals("arr"))&&(freq.equals(""))){
			sqlStr="delete FROM alarmswitch where switchtype = 1 and freq = '"+freq+"';";
		}
		try {
			conn = DaoSupport.getJDBCConnection();
			conn.setAutoCommit(false);
			try {
				statement = conn.createStatement();
				statement.execute(sqlStr);
			}  catch (Exception e) {
				log.error("删除报警开关信息数据库错误: " + e.getMessage());
				log.error("删除报警开关信息数据库错误 SQL：\n" + sqlStr);
			} finally {
				DaoSupport.close(statement);
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("删除报警开关状态错误: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
	}
}
