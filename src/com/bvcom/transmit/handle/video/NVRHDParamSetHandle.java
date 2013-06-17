package com.bvcom.transmit.handle.video;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;

public class NVRHDParamSetHandle {

    private static Logger log = Logger.getLogger(NVRHDParamSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRHDParamSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. 下发给所有的TSC
     * 2. 上报信息给中心
     *
     */
    public void downXML() {
        // 返回数据
        String upString = "";
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("高清录像相关设置StringToXML Error: " + e.getMessage());
        };
        
        MemCoreData coreData = MemCoreData.getInstance();
        // 取得TSC配置文件信息
        List TSCList = coreData.getTSCList();
        List SMGList = coreData.getSMGCardList();
        
        // TSC 下发指令,  
        // FIXME 目前只考虑一套TSC的情况
        String url = "";
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            try {
                if(!url.equals(tsc.getURL())) {
                    // 任务录像信息下发 timeout 1000*30 三十秒
                    utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
                    url = tsc.getURL();
                }
            } catch (CommonException e) {
                log.error("高清录像相关设置向TSC下发任务录像出错：" + tsc.getURL());
                upString = "";
            }
        } // TSC 下发指令 END
        
        // 高清设置
        for (int i=0; i<SMGList.size(); i++) {
        	SMGCardInfoVO smg = (SMGCardInfoVO) SMGList.get(i);
            try {
                // 高清相关配置
                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
                	// 高清转码下发
                	utilXML.SendDownNoneReturn(this.downString, smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                
            } catch (CommonException e) {
                log.error("下发自动录像到SMG出错：" + smg.getURL());
            }
        }
        try {
    		upString = utilXML.getReturnXML(bsData, 0);
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("高清录像相关设置信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        TSCList = null;
        utilXML = null;
    }
    
}
