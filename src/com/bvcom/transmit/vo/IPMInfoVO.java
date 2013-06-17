/**
 * transmit (javaת��)
 * 
 * IPMInfoVO.java    2009.11.12
 * 
 * Copyright 2009 BVCOM. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.vo;

/**
 * 
 *  IP �໭����Ҫ�������ļ���Ϣ
 * 
 * @version  V1.0
 * @author Bian Jiang
 * @Date 2009.11.12
 */
public class IPMInfoVO {
    
    private int IndexMin;
    
    private int IndexMax;
    
    private String IP;
    
    private String URL;
    
    private String SysURL;
    
    
    /**
     * ��ǰ�����ĸ����Ŀ��
     */
    private int HDNums = 0;
    
    /**
     * ��ǰ�����ĸ����Ŀ��
     */
    private int TVNums = 0;
    
    /**
     * RecordType: 0:��¼�� 1:��̬����¼�� 2:�Զ�¼�� 3:�໭��ϳ�(������)
     */
    private int RecordType;

    public int getIndexMax() {
        return IndexMax;
    }

    public void setIndexMax(int indexMax) {
        IndexMax = indexMax;
    }

    public int getIndexMin() {
        return IndexMin;
    }

    public void setIndexMin(int indexMin) {
        IndexMin = indexMin;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String ip) {
        IP = ip;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        URL = url;
    }
    
    public String getSysURL() {
		return SysURL;
	}

	public void setSysURL(String sysURL) {
		SysURL = sysURL;
	}

	/**
     * RecordType: 0:��¼�� 1:��̬����¼�� 2:�Զ�¼�� 3:�໭��ϳ�(������)
     */
    public int getRecordType() {
		return RecordType;
	}
    
    /**
     * RecordType: 0:��¼�� 1:��̬����¼�� 2:�Զ�¼�� 3:�໭��ϳ�(������)
     */
	public void setRecordType(int recordType) {
		RecordType = recordType;
	}

	public int getHDNums() {
		return HDNums;
	}

	public void setHDNums(int nums) {
		HDNums = nums;
	}

	public int getTVNums() {
		return TVNums;
	}

	public void setTVNums(int nums) {
		TVNums = nums;
	}

	public void printIPMInfoList() {
        System.out.println("--------------- IPMInfo List Start----------------");
        System.out.println("IndexMin: " + IndexMin);
        System.out.println("IndexMax: " + IndexMax);
        System.out.println("IP: " + IP);
        System.out.println("URL: " + URL);
        System.out.println("--------------- IPMInfo List End----------------");
    }
    
    
}
