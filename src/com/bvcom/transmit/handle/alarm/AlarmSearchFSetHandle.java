package com.bvcom.transmit.handle.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.parse.alarm.AlarmSearchFSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

public class AlarmSearchFSetHandle {
	
	private static Logger log = Logger.getLogger(AlarmSearchFSetHandle.class
			.getSimpleName());

	private MSGHeadVO bsData = new MSGHeadVO();

	private String downString = new String();

	private UtilXML utilXML = new UtilXML();
	
	public AlarmSearchFSetHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}

	/**
	 * 报警上报频率查询
	 */
	@SuppressWarnings("unchecked")
	public void downXML() {
		
		List MSGSendList = new ArrayList();
        //List IPMSendList = new ArrayList();
		// 返回数据
        @SuppressWarnings("unused")
		String upString = "";
        AlarmSearchFSetParse AlarmSearchFSet = new AlarmSearchFSetParse();
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("报警上报频率StringToXML Error: " + e.getMessage());
        }
        
//        List<AlarmSearchFSetVO> volist = AlarmSearchFSet.getIndexByDownXml(document);
//        for(int i=0;i<volist.size();i++){
//        	CommonUtility.checkSMGChannelIndex(volist.get(i).getIndex() , MSGSendList);
//            //CommonUtility.checkIPMChannelIndex(volist.get(i).getIndex() , IPMSendList);
//        }
        
//        for(int j=0;j<IPMSendList.size();j++){
//            IPMInfoVO ipm = (IPMInfoVO) MSGSendList.get(j);
//            try {
//                // 运行图信息下发 timeout 1000*30 三十秒
//                upString = utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
//            } catch (CommonException e) {
//                log.error("报警上报频率IPM下发任务录像出错：" + ipm.getURL());
//            }
//        }
        
    	for(int j=0;j<MSGSendList.size();j++){
    		SMGCardInfoVO smg = (SMGCardInfoVO) MSGSendList.get(j);
    		try {
                // 运行图信息下发 timeout 1000*30 三十秒
    			upString = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("报警上报频率SMG下发任务录像出错：" + smg.getURL());
            }
        }
        try {
            utilXML.SendUpXML(document.asXML(), bsData);
        } catch (CommonException e) {
            log.error("报警上报频率回复失败: " + e.getMessage());
        }
        
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
