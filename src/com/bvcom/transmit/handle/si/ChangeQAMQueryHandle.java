package com.bvcom.transmit.handle.si;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.parse.si.ChangeQAMQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.si.ChangeQAMQueryVO;

public class ChangeQAMQueryHandle {
	
	private static Logger log = Logger.getLogger(ChangeQAMQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public ChangeQAMQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * qam设置
     * 目前直接返回成功
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		 // 返回数据
		@SuppressWarnings("unused")
		String upString = "";
        List SMGSendList = new ArrayList();//smg的列表信息
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("qam设置StringToXML Error: " + e.getMessage());
        }
        
        ChangeQAMQueryParse qamp = new ChangeQAMQueryParse();
        List<ChangeQAMQueryVO> qamlist = qamp.getIndexByDownXml(document);
        
        //上报回复的xml给中心,自己返回成功
        upString = qamp.ReturnXMLByURL(this.bsData,0);
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("qam设置回复失败: " + e.getMessage());
        }
        
        for(int i=0;i<qamlist.size();i++){
        	int index = qamlist.get(i).getIndex();
        	CommonUtility.checkSMGChannelIndex(index, SMGSendList);
        }

    	for(int j=0;j<SMGSendList.size();j++)
    	{
    		SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(j);
			try {
                // qam设置下发 timeout 1000*30 三十秒
                utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                //break;
            } catch (CommonException e) {
                log.error("下发qam设置到TSC出错：" + smg.getURL());
            }
    	}
        	

        
        bsData = null;
        downString = null;
        utilXML = null;
        SMGSendList = null;
        qamp  = null;
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
