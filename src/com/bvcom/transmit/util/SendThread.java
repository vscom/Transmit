package com.bvcom.transmit.util;

import org.apache.log4j.Logger;

import com.bvcom.transmit.TransmitThread;

public class SendThread extends Thread {
	

    private static Logger log = Logger.getLogger(TransmitThread.class.getSimpleName());
    
    private String downString = new String();
    
    private String sendURL = new String();
    
    /**
     * 
     * @param centerDownStr
     * @param bsData
     */
    public SendThread(String centerDownStr, String url) {
        this.downString = centerDownStr;
        this.sendURL = url;
    }

    public void run() {
    	
    }

}
