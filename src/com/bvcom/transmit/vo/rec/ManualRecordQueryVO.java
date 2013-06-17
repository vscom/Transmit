package com.bvcom.transmit.vo.rec;

import com.bvcom.transmit.vo.MSGHeadVO;

//手动录像
public class ManualRecordQueryVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

	private int Index;
	
	private String Time;

	private String Path;
	
	private int ReutnValue;
	
	private String Comment;//失败原因
	
	private String IP;
	
	private String Port;
	
	private int Freq;
	
	private int ServiceID;
	
	//手动录制的文件保存时间
	private String fileSaveTime;

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

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getPath() {
		return Path;
	}

	public void setPath(String path) {
		Path = path;
	}

	public int getReutnValue() {
		return ReutnValue;
	}

	public void setReutnValue(int reutnValue) {
		ReutnValue = reutnValue;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public String getPort() {
		return Port;
	}

	public void setPort(String port) {
		Port = port;
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

	public String getFileSaveTime() {
		return fileSaveTime;
	}

	public void setFileSaveTime(String fileSaveTime) {
		this.fileSaveTime = fileSaveTime;
	}

}
