package com.bvcom.transmit.vo.alarm;

public class ReplyAlarmErrorTableVO {

	private int id;
	
	private String replyXML;
	
	private String errorMsg;
	
	private String lastDateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReplyXML() {
		return replyXML;
	}

	public void setReplyXML(String replyXML) {
		this.replyXML = replyXML;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getLastDateTime() {
		return lastDateTime;
	}

	public void setLastDateTime(String lastDateTime) {
		this.lastDateTime = lastDateTime;
	}
	
}
