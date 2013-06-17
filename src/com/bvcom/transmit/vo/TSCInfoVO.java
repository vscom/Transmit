/**
 * transmit (java转发)
 * 
 * TSCInfoVO.java    2009.11.12
 * 
 * Copyright 2009 BVCOM. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.vo;

/**
 * 
 *  转码所需要的配置文件信息
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
     * 当前机器的高清节目数
     */
    private int HDNums = 0;
    
    /**
     * 当前机器的高清节目数
     */
    private int TVNums = 0;
    
    /**
     * RecordType: 0:不录像 1:异态触发录像 2:自动录像 3:多画面合成(马赛克)
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
     * RecordType: 0:不录像 1:异态触发录像 2:自动录像 3:多画面合成(马赛克)
     */
	public int getRecordType() {
		return RecordType;
	}
    /**
     * RecordType: 0:不录像 1:异态触发录像 2:自动录像 3:多画面合成(马赛克)
     */
	public void setRecordType(int recordType) {
		RecordType = recordType;
	}

	/**
	 * 当前机器的高清节目数
	 * @return
	 */
	public int getHDNums() {
		return HDNums;
	}

	/**
	 * 当前机器的高清节目数
	 * @param nums
	 */
	public void setHDNums(int nums) {
		HDNums = nums;
	}
	/**
	 * 当前机器的标清节目数
	 * @param nums
	 */
	public int getTVNums() {
		return TVNums;
	}
	/**
	 * 当前机器的标清节目数
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
