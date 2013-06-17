package com.bvcom.transmit.vo.alarm;

import com.bvcom.transmit.vo.MSGHeadVO;

//报警上报（环境相关）
public class AlarmSearchESetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int type;
	    
    private String Desc;

    private int Value;

    private String Time;
    
    private int ReturnValue;
    
    private String Comment;//失败原因

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public int getValue() {
		return Value;
	}

	public void setValue(int value) {
		Value = value;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
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
