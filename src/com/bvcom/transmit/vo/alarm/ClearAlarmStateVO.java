package com.bvcom.transmit.vo.alarm;

import com.bvcom.transmit.vo.MSGHeadVO;

//����״̬���
public class ClearAlarmStateVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Freq;
	
	private int ReturnValue;
    
    private String Comment;//ʧ��ԭ��

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
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
