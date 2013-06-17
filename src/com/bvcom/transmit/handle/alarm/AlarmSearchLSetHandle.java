package com.bvcom.transmit.handle.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

public class AlarmSearchLSetHandle {
	private static Logger log = Logger.getLogger(AlarmSearchLSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public AlarmSearchLSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 循切报警查询
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		// 返回数据
        String upString = "";
        List SMGSendList = new ArrayList();
        
        boolean isErr = false;
        
        SMGSendList = CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);
        
        @SuppressWarnings("unused")
		Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("循切报警查询StringToXML Error: " + e.getMessage());
        };
    	for(int j=0;j<SMGSendList.size();j++)
    	{
    		 SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(j);
             try {
                 // 循切报警查询下发 timeout 1000*30 三十秒
                 upString = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
             } catch (CommonException e) {
                 log.error("下发循切报警查询出错：" + smg.getURL());
                 isErr = true;
             }
    	}
    	
        if(isErr || "".equals(upString)) {
            upString = utilXML.getReturnXML(bsData, 1);
        }
        
    	try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("上发循切报警信息失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        SMGSendList = null;
        utilXML = null;
        
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
