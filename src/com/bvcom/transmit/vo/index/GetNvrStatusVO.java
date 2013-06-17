package com.bvcom.transmit.vo.index;

import com.bvcom.transmit.vo.MSGHeadVO;
//通道状态查询
public class GetNvrStatusVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

    private int Index;
    
    private int Status;
    
    private int Freq;
    
    private int ServiceID;
    
    private String Desc;
    
    private String ReturnDesc;
    
    private int ReturnValue;
    
    private String Comment;//失败原因

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int index) {
		Index = index;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
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

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getReturnDesc() {
		return ReturnDesc;
	}

	public void setReturnDesc(String returnDesc) {
		ReturnDesc = returnDesc;
	}

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}

}
