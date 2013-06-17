package com.bvcom.transmit.vo.si;

import com.bvcom.transmit.vo.MSGHeadVO;

public class MHPQueryVO {
	
    private MSGHeadVO MSGHead = new MSGHeadVO();

    private String ScanTime;
    
    private String Ftp;
    
    private String UserName;
    
    private String Pass;

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public String getScanTime() {
		return ScanTime;
	}

	public void setScanTime(String scanTime) {
		ScanTime = scanTime;
	}

	public String getFtp() {
		return Ftp;
	}

	public void setFtp(String ftp) {
		Ftp = ftp;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPass() {
		return Pass;
	}

	public void setPass(String pass) {
		Pass = pass;
	}
    
    
    
}
