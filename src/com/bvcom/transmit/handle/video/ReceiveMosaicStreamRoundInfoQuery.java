package com.bvcom.transmit.handle.video;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;

public class ReceiveMosaicStreamRoundInfoQuery {
	
    private static Logger log = Logger.getLogger(ReceiveMosaicStreamRoundInfoQuery.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public ReceiveMosaicStreamRoundInfoQuery(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    /**
     * 1. ���յ����������ֲ���Ŀ��Ϣ  �ϱ�������
     */
    public void downXML() {
    	String CenterURL=coreData.getSysVO().getCenterRoundChannelURL();
    	 log.error("�Ϸ� "+ bsData.getStatusQueryType()+"�ֲ��ϱ�URL:"+CenterURL);
    	 
    	utilXML.SendUpXML(downString, CenterURL);
    }
}
