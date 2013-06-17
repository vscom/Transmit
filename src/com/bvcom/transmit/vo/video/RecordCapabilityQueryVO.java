package com.bvcom.transmit.vo.video;

import com.bvcom.transmit.vo.MSGHeadVO;

/**
 *  ¼��·����ѯ
 * @author Bian Jiang
 *
 */
public class RecordCapabilityQueryVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Freq;
	
	private int ServiceID;
	
	private int ProgramID;
	
	// IsRecordΪ0�ǿ���¼�ƣ�Ϊ1�ǲ�����¼��2 ͨ�����������˲���¼��
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
	 * IsRecordΪ0�ǿ���¼�ƣ�Ϊ1�ǲ�����¼��2 ͨ�����������˲���¼��
	 * @return
	 */
	public int getIsRecord() {
		return IsRecord;
	}

	/**
	 * IsRecordΪ0�ǿ���¼�ƣ�Ϊ1�ǲ�����¼��2 ͨ�����������˲���¼��
	 * @param isRecord
	 */
	public void setIsRecord(int isRecord) {
		IsRecord = isRecord;
	}
	
}
