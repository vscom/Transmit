package com.bvcom.transmit.vo.rec;

import com.bvcom.transmit.vo.MSGHeadVO;

//历史视频查看
public class NVRVideoHistoryInquiryVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Index; 

    private String StartDateTime;
    
    private String EndDateTime;
    
    private int ReuturnValue;
    
    private String ReturnURL;
    
    private String Comment;//失败原因

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

	public int getReuturnValue() {
		return ReuturnValue;
	}

	public void setReuturnValue(int reuturnValue) {
		ReuturnValue = reuturnValue;
	}

	public String getReturnURL() {
		return ReturnURL;
	}

	public void setReturnURL(String returnURL) {
		ReturnURL = returnURL;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}
}
