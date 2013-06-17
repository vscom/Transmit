package com.bvcom.transmit.vo.alarm;


import com.bvcom.transmit.vo.MSGHeadVO;

//报警门限（频率相关）
public class AlarmThresholdSetVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private Integer type;
    
    private String Desc;

    private int Num;
    
    private int TimeInterval;
    
    private int DownThreshold;
    
    private int UpThreshold;
    
    private int AlarmThreshold;
    
    private int ReturnValue;
    
    private String Comment;//失败原因

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public int getNum() {
		return Num;
	}

	public void setNum(int num) {
		Num = num;
	}

	public int getTimeInterval() {
		return TimeInterval;
	}

	public void setTimeInterval(int timeInterval) {
		TimeInterval = timeInterval;
	}

	public int getDownThreshold() {
		return DownThreshold;
	}

	public void setDownThreshold(int downThreshold) {
		DownThreshold = downThreshold;
	}

	public int getUpThreshold() {
		return UpThreshold;
	}

	public void setUpThreshold(int upThreshold) {
		UpThreshold = upThreshold;
	}

	public int getAlarmThreshold() {
		return AlarmThreshold;
	}

	public void setAlarmThreshold(int alarmThreshold) {
		AlarmThreshold = alarmThreshold;
	}

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}
}
