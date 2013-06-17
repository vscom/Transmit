package com.bvcom.transmit.parse.video.domain;


public class AlarmTime {
	private int Freq;
	private int ServiceID;
	private String Month;
	private int Day;
	private int Type;
	private int DayofWeek;
	private String TaskType;
	private String StartTime;
	private String EndTime;
	private String AlarmEndTime;
	private String StartDateTime;
	private String EndDateTime;
	
	public AlarmTime() {
		super();
	}
	public AlarmTime(int freq, int serviceID, String month, int day, int type,
			int dayofWeek, String taskType, String startTime, String endTime,
			String alarmEndTime, String startDateTime, String endDateTime) {
		super();
		Freq = freq;
		ServiceID = serviceID;
		Month = month;
		Day = day;
		Type = type;
		DayofWeek = dayofWeek;
		TaskType = taskType;
		StartTime = startTime;
		EndTime = endTime;
		AlarmEndTime = alarmEndTime;
		StartDateTime = startDateTime;
		EndDateTime = endDateTime;
	}
	public int getFreq() {
		return Freq;
	}
	public void setFreq(int freq) {
		Freq = freq;
	}
	public int getServiceID() {
		return ServiceID;
	}
	public void setServiceID(int serviceID) {
		ServiceID = serviceID;
	}
	public String getMonth() {
		return Month;
	}
	public void setMonth(String month) {
		Month = month;
	}
	public int getDay() {
		return Day;
	}
	public void setDay(int day) {
		Day = day;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	public int getDayofWeek() {
		return DayofWeek;
	}
	public void setDayofWeek(int dayofWeek) {
		DayofWeek = dayofWeek;
	}
	public String getTaskType() {
		return TaskType;
	}
	public void setTaskType(String taskType) {
		TaskType = taskType;
	}
	public String getStartTime() {
		return StartTime;
	}
	public void setStartTime(String startTime) {
		StartTime = startTime;
	}
	public String getEndTime() {
		return EndTime;
	}
	public void setEndTime(String endTime) {
		EndTime = endTime;
	}
	public String getAlarmEndTime() {
		return AlarmEndTime;
	}
	public void setAlarmEndTime(String alarmEndTime) {
		AlarmEndTime = alarmEndTime;
	}
	public String getStartDateTime() {
		return StartDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		StartDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return EndDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		EndDateTime = endDateTime;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Freq +","+ ServiceID +","+ Month +","+ Day +","+ Type +","+ DayofWeek +","+ TaskType +","+ StartTime +","+ EndTime +","+ AlarmEndTime +","+ StartDateTime +","+ EndDateTime;
	}
}
