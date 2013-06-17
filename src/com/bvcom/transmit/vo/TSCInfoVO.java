/**
 * transmit (javaת��)
 * 
 * TSCInfoVO.java    2009.11.12
 * 
 * Copyright 2009 BVCOM. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.vo;

/**
 * 
 *  ת������Ҫ�������ļ���Ϣ
 * 
 * @version  V1.0
 * @author Bian Jiang
 * @Date 2009.11.12
 */
public class TSCInfoVO {
    
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
    
//    private String StreamRateURL;

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

	//    public String getStreamRateURL() {
//		return StreamRateURL;
//	}
//
//	public void setStreamRateURL(String streamRateURL) {
//		StreamRateURL = streamRateURL;
//	}
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

	/**
	 * ��ǰ�����ĸ����Ŀ��
	 * @return
	 */
	public int getHDNums() {
		return HDNums;
	}

	/**
	 * ��ǰ�����ĸ����Ŀ��
	 * @param nums
	 */
	public void setHDNums(int nums) {
		HDNums = nums;
	}
	/**
	 * ��ǰ�����ı����Ŀ��
	 * @param nums
	 */
	public int getTVNums() {
		return TVNums;
	}
	/**
	 * ��ǰ�����ı����Ŀ��
	 * @param nums
	 */
	public void setTVNums(int nums) {
		TVNums = nums;
	}

	public void printTSCInfoList() {
        System.out.println("--------------- TSCInfo List Start----------------");
        System.out.println("IndexMin: " + IndexMin);
        System.out.println("IndexMax: " + IndexMax);
        System.out.println("IP: " + IP);
        System.out.println("URL: " + URL);
//        System.out.println("URL: " + StreamRateURL);
        System.out.println("--------------- TSCInfo List End----------------");
    }
}
