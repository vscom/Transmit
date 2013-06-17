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
 * ��Ŀ����ͼ dao Ji Long 2011-6-15
 */
public class AlarmTimeDao {
	private static Logger log = Logger.getLogger(AlarmTimeDao.class
			.getCanonicalName());

	/**
	 * ��������ͼ��Ŀ��Ϣ �����ݿ�
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
				// ����Ƶ��ɾ��֮ǰ�趨������ͼ��Ϣ
				delOrSaveSql = "delete FROM alarmtime where freq = "
						+ list.get(i).getFreq() + ";";
				try {
					statement = conn.createStatement();
					statement.execute(delOrSaveSql);
				} catch (Exception e) {
					// TODO: handle exception
					log.error("ɾ����䣺"+delOrSaveSql);
					log.error("ɾ������ͼ״̬���ϴ���: " + e.getMessage());
				} finally {
					DaoSupport.close(statement);
				}
				// �����µ�����ͼ��Ϣ
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
					log.error("������䣺"+delOrSaveSql);
					log.error("��������ͼ״̬���ϴ���: " + e.getMessage());
				} finally {
					DaoSupport.close(statement);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("����orɾ������ͼ״̬���ϴ���: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
		//�����ڴ�������ͼ��Ϣ
		AlarmTimeMemory.alarmTimeList=list();
	}

	/**
	 * ȡ�����ݿ����н�Ŀ��Ϣ
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
			log.error("��ѯ��װ����ͼ״̬���ϴ���: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
		return list;
	}

	//��������ͼ��Ϣ ���˱��� Ji Long 2011-06-16
	public List select(List alarmList) {
		List<AlarmSearchPSetVO> newList=new ArrayList<AlarmSearchPSetVO>();
		
		for(int i=0;i<alarmList.size();i++){
			AlarmSearchPSetVO vo=(AlarmSearchPSetVO)alarmList.get(i);
			//�����������ͼͣ����Χ���򲻹���
			if(!isAlarmTime(vo)){
				newList.add(vo);
			}
		}
		return newList;
	}
	
	//�жϸñ����Ƿ��� ����ͼͣ����
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
					//ת����������,��Ϊjava apiĬ�ϻ�ȡ�������Ǵ�0 ��6 ����0��ʶ��ĩ
					//ת�����Ϊ0ʱ��ֵ Ϊ7
					if(dayoWeek==0){
						dayoWeek=7;
					}
					if(at.getDayofWeek()==dayoWeek){
						//ת������  ��Ϊ���˱��� ʱ������
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
					//�������ʱ���� ����ͼ ͣ��ʱ���� �򷵻�true ����ѭ��
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





























