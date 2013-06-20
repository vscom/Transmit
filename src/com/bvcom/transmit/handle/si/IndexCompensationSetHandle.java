package com.bvcom.transmit.handle.si;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.parse.si.IndexCompensationSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

public class IndexCompensationSetHandle {
	
    private static Logger log = Logger.getLogger(ChannelScanQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public IndexCompensationSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     *指标补偿
     */
    @SuppressWarnings("unchecked")
	public void downXML() {
    	 List SMGSendList = new ArrayList();
    	 
    	 SMGSendList = CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);
    	 String upString = "";
         for (int i=0; i< SMGSendList.size(); i++) {
             SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
             try {
                 // 指标补偿信息下发 timeout 1000*60*3 三分钟
                 utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
                 break;
             } catch (CommonException e) {
                 log.error("向SMG下发频道扫描出错：" + smg.getURL());
             }
         }
         
         IndexCompensationSetParse IndexCompensationSetParse = new IndexCompensationSetParse();
         upString = IndexCompensationSetParse.ReturnXMLByURL(bsData, 0);
         
         try {
             utilXML.SendUpXML(upString, bsData);
         } catch (CommonException e) {
             log.error("上发频道扫描信息失败: " + e.getMessage());
         }
         
         bsData = null;
         downString = null;
         SMGSendList = null;
         utilXML = null;
         IndexCompensationSetParse = null;
    }

	public MSGHeadVO getBsData() {
		return bsData;
	}

	public void setBsData(MSGHeadVO bsData) {
		this.bsData = bsData;
	}

	public String getDownString() {
		return downString;
	}

	public void setDownString(String downString) {
		this.downString = downString;
	}

	public UtilXML getUtilXML() {
		return utilXML;
	}

	public void setUtilXML(UtilXML utilXML) {
		this.utilXML = utilXML;
	}

}
