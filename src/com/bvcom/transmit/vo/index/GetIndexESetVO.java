package com.bvcom.transmit.vo.index;

import com.bvcom.transmit.vo.MSGHeadVO;
//运行环境指标查询
public class GetIndexESetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

	private int Type;
	
	private String Desc;//对应type的描述
	
	private int Value;//对应type的value
	
	private int ReturnValue;//成功或失败
	
	private String Comment;//失败原因

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}


	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
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
