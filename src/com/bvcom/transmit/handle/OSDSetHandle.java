package com.bvcom.transmit.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.parse.OSDSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.OSDSetVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class OSDSetHandle {
	private static Logger log = Logger.getLogger(OSDSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public OSDSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * osd设置
     * 目前直接返回成功
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		 // 返回数据
		@SuppressWarnings("unused")
		String upString = "";
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("osd设置StringToXML Error: " + e.getMessage());
        }
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        OSDSetParse osdp = new OSDSetParse();
        List<OSDSetVO> osdlist = osdp.getIndexByDownXml(document);
        
        String url = "";

        List monitorProgramList = null;
		try {
			monitorProgramList = MonitorProgramQueryHandle.GetWatchAndSeeVOList();
		} catch (DaoException e1) {

		}
        
    	for(int j=0;j<monitorProgramList.size();j++)
    	{
    		MonitorProgramQueryVO tsc = (MonitorProgramQueryVO) monitorProgramList.get(j);
			try {
                if(!url.equals(tsc.getRTVSResetURL())) {
//                      历史视频查看下发 timeout 1000*30 三十秒
                    upString = utilXML.SendDownXML(this.downString, tsc.getRTVSResetURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                    //break;
                    url = tsc.getRTVSResetURL();
                }
                
                //break;
            } catch (CommonException e) {
                log.error("下发osd设置到TSC出错：" + tsc.getRTVSResetURL());
            }
    	}
        
        List TSCSendList = coreData.getTSCList();//tsc的列表信息
        
    	for(int j=0;j<TSCSendList.size();j++)
    	{
    		TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(j);
			try {
                if(!url.equals(tsc.getURL())) {
                    // 历史视频下载下发 timeout 1000*30 三十秒
                    utilXML.SendDownNoneReturn(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
                    //break;
                    url = tsc.getURL().trim();
                }
            } catch (CommonException e) {
                log.error("下发osd设置到TSC出错：" + tsc.getURL());
            }
    	}
        
      //上报回复的xml给中心,自己返回成功
        upString = osdp.ReturnXMLByURL(this.bsData,0);
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("osd设置回复失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        osdp=null;
	}

}
