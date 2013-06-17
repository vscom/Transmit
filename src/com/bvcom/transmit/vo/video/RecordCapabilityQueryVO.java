package com.bvcom.transmit.vo.video;

import com.bvcom.transmit.vo.MSGHeadVO;

/**
 *  录像路数查询
 * @author Bian Jiang
 *
 */
public class RecordCapabilityQueryVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Freq;
	
	private int ServiceID;
	
	private int ProgramID;
	
	// IsRecord为0是可以录制，为1是不可以录像，2 通道个数不够了不能录像
	private int IsRecord = 0;
	
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

	public int getServiceID() {
		return ServiceID;
	}

	public void setServiceID(int serviceID) {
		ServiceID = serviceID;
	}

	public int getProgramID() {
		return ProgramID;
	}

	public void setProgramID(int programID) {
		ProgramID = programID;
	}

	/**
	 * IsRecord为0是可以录制，为1是不可以录像，2 通道个数不够了不能录像
	 * @return
	 */
	public int getIsRecord() {
		return IsRecord;
	}

	/**
	 * IsRecord为0是可以录制，为1是不可以录像，2 通道个数不够了不能录像
	 * @param isRecord
	 */
	public void setIsRecord(int isRecord) {
		IsRecord = isRecord;
	}
	
}
