package com.bvcom.transmit.vo.index;

import com.bvcom.transmit.vo.MSGHeadVO;
//���л���ָ���ѯ
public class GetIndexESetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

	private int Type;
	
	private String Desc;//��Ӧtype������
	
	private int Value;//��Ӧtype��value
	
	private int ReturnValue;//�ɹ���ʧ��
	
	private String Comment;//ʧ��ԭ��

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
