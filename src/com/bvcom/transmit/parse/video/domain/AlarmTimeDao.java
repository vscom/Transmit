package com.bvcom.transmit.parse.video.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.AlarmTimeMemory;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;

/**
 * 节目运行图 dao Ji Long 2011-6-15
 */
public class AlarmTimeDao {
	private static Logger log = Logger.getLogger(AlarmTimeDao.class
			.getCanonicalName());

	/**
	 * 保存运行图节目信息 到数据库
	 * 
	 * @param list
	 */
	public void save(List<AlarmTime> list) {
		Statement statement = null;
		Connection conn = null;
		try {
			conn = DaoSupport.getJDBCConnection();
			String delOrSaveSql = "";
			for (int i = 0; i < list.size(); i++) {
				// 根据频点删除之前设定的运行图信息
				delOrSaveSql = "delete FROM alarmtime where freq = "
						+ list.get(i).getFreq() + ";";
				try {
					statement = conn.createStatement();
					statement.execute(delOrSaveSql);
				} catch (Exception e) {
					// TODO: handle exception
					log.error("删除语句："+delOrSaveSql);
					log.error("删除运行图状态集合错误: " + e.getMessage());
				} finally {
					DaoSupport.close(statement);
				}
				// 保存新的运行图信息
				delOrSaveSql = "insert into alarmtime(Freq,ServiceID,Month,Day,Type,DayofWeek,TaskType,StartTime,EndTime,AlarmEndTime,StartDateTime,EndDateTime) values("
						+ list.get(i).getFreq()
						+ ","
						+ list.get(i).getServiceID()
						+ ","
						+ list.get(i).getMonth()
						+ ","
						+ list.get(i).getDay()
						+ ","
						+ list.get(i).getType()
						+ ","
						+ list.get(i).getDayofWeek()
						+ ",'"
						+ list.get(i).getTaskType()
						+ "','"
						+ list.get(i).getStartTime()
						+ "','"
						+ list.get(i).getEndTime()
						+ "','"
						+ list.get(i).getAlarmEndTime()
						+ "','"
						+ list.get(i).getStartDateTime()
						+ "','"
						+ list.get(i).getEndDateTime() + "')";
				try {
					statement = conn.createStatement();
					statement.executeUpdate(delOrSaveSql);
				} catch (Exception e) {
					// TODO: handle exception
					log.error("插入语句："+delOrSaveSql);
					log.error("保存运行图状态集合错误: " + e.getMessage());
				} finally {
					DaoSupport.close(statement);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("保存or删除运行图状态集合错误: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		//更新内存中运行图信息
		AlarmTimeMemory.alarmTimeList=list();
	}

	/**
	 * 取出数据库所有节目信息
	 * 
	 * @return
	 */
	public List<AlarmTime> list() {
		List<AlarmTime> list = new ArrayList<AlarmTime>();
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql="";
		sql="SELECT * FROM alarmtime;";
		try{
			conn=DaoSupport.getJDBCConnection();
			try{
				statement=conn.createStatement();
				rs=statement.executeQuery(sql);
				while(rs.next()){
					AlarmTime at=new AlarmTime();
					at.setFreq(rs.getInt("Freq"));
					at.setServiceID(rs.getInt("ServiceID"));
					at.setMonth(rs.getString("Month"));
					at.setDay(rs.getInt("Day"));
					at.setType(rs.getInt("Type"));
					at.setDayofWeek(rs.getInt("DayofWeek"));
					at.setTaskType(rs.getString("TaskType"));
					at.setStartTime(rs.getString("StartTime"));
					at.setEndTime(rs.getString("EndTime"));
					at.setAlarmEndTime(rs.getString("AlarmEndTime"));
					at.setStartDateTime(rs.getString("StartDateTime"));
					at.setEndDateTime(rs.getString("EndDateTime"));
					list.add(at);
				}
			}catch(Exception e){
				
			}finally{
				DaoSupport.close(statement);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询封装运行图状态集合错误: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		return list;
	}

	//根据运行图信息 过滤报警 Ji Long 2011-06-16
	public List select(List alarmList) {
		List<AlarmSearchPSetVO> newList=new ArrayList<AlarmSearchPSetVO>();
		
		for(int i=0;i<alarmList.size();i++){
			AlarmSearchPSetVO vo=(AlarmSearchPSetVO)alarmList.get(i);
			//如果不在运行图停播范围内则不过滤
			if(!isAlarmTime(vo)){
				newList.add(vo);
			}
		}
		return newList;
	}
	
	//判断该报警是否在 运行图停播中
	public boolean isAlarmTime(AlarmSearchPSetVO vo){
		boolean falg=false;
		List<AlarmTime> alarmTimeList=AlarmTimeMemory.alarmTimeList;
		for(int i=0;i<alarmTimeList.size();i++){
			AlarmTime at=alarmTimeList.get(i);
			if(at.getFreq()==vo.getFreq()&&vo.getServiceID()==0){
				if(at.getTaskType().equals("MonthTime")){
					Date alarmDate= CommonUtility.dateStrToDate(vo.getTime());
					int month=alarmDate.getMonth();
					int monthDay=alarmDate.getDate();
					if(at.getMonth().equals("all")||at.getMonth().equals("ALL")){
						if(at.getDay()==monthDay){
							Date startTime= CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getStartTime());
							Date endTime  = CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getEndTime());
							Date endDate  = CommonUtility.dateStrToDate(at.getAlarmEndTime());
							if(alarmDate.after(startTime)&&alarmDate.before(endDate)&&alarmDate.before(endTime)){
								falg=true;
								break;
							}
						}
					}else{
						month=month+1;
						if(at.getDay()==monthDay&&Integer.parseInt(at.getMonth())==month){
							Date startTime= CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getStartTime());
							Date endTime  = CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getEndTime());
							Date endDate  = CommonUtility.dateStrToDate(at.getAlarmEndTime());
							if(alarmDate.after(startTime)&&alarmDate.before(endDate)&&alarmDate.before(endTime)){
								falg=true;
								break;
							}
						}
					}
				}
				if(at.getTaskType().equals("WeeklyTime")){
					Date alarmDate= CommonUtility.dateStrToDate(vo.getTime());
					int  dayoWeek=alarmDate.getDate();
					//转换日期类型,因为java api默认获取的星期是从0 到6 其中0标识周末
					//转换如果为0时候赋值 为7
					if(dayoWeek==0){
						dayoWeek=7;
					}
					if(at.getDayofWeek()==dayoWeek){
						//转换日期  作为过滤报警 时间依据
						Date startTime= CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getStartTime());
						Date endTime  = CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getEndTime());
						Date endDate  = CommonUtility.dateStrToDate(at.getAlarmEndTime());
						if(alarmDate.after(startTime)&&alarmDate.before(endDate)&&alarmDate.before(endTime)){
							falg=true;
							break;
						}
					}
				}
				if(at.getTaskType().equals("DayTime")){
					Date alarmDate= CommonUtility.dateStrToDate(vo.getTime());
					Date startTime= CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getStartDateTime().split(" ")[1]);
					Date endTime  = CommonUtility.dateStrToDate(vo.getTime().split(" ")[0]+" "+at.getEndDateTime().split(" ")[1]);
					Date endDate  = CommonUtility.dateStrToDate(at.getEndDateTime());
					//如果报警时间在 运行图 停播时间内 则返回true 结束循环
					if(alarmDate.after(startTime)&&alarmDate.before(endDate)&&alarmDate.before(endTime)){
						falg=true;
						break;
					}
				}
			}
		}
		return falg;
	}
}





























