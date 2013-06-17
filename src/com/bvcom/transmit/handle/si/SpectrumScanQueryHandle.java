package com.bvcom.transmit.handle.si;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

/**
 * 频谱扫描
 * @author Bian Jiang
 *
 */
public class SpectrumScanQueryHandle {
    
    private static Logger log = Logger.getLogger(ChannelScanQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public SpectrumScanQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    public void downXML() {
        
        List SMGSendList = new ArrayList();
        
        CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);

        UtilXML utilXML = new UtilXML();
        
        String retXML = "";
        
        for (int i=0; i< SMGSendList.size(); i++) {
            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
            try {
                // 频谱扫描信息下发 timeout 1000*60*10 十分钟
                retXML = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
                
                retXML = CommonUtility.RegReplaceString(retXML, "ScanTime");
                break;
            } catch (Exception e) {
                log.error("向SMG下发 频谱扫描出错：" + smg.getURL());
            }
        }
        
        try {
            utilXML.SendUpXML(retXML, bsData);
        } catch (CommonException e) {
            log.error("上发频谱扫描信息失败: " + e.getMessage());
        }
    }
    
}
