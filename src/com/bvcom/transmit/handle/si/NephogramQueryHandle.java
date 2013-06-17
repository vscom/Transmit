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
import com.bvcom.transmit.vo.SysInfoVO;
/**
 * Web2.0 星座图 Add By Bian Jiang 2011.1.7
 * @author Bian Jiang
 *
 */
public class NephogramQueryHandle {

    private static Logger log = Logger.getLogger(NephogramQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public NephogramQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    public void downXML() {
		List SMGSendList = new ArrayList();

		// 3:GetIndexSet(性能指标和星座图)
		CommonUtility.checkSMGChannelType("GetIndexSet", SMGSendList);
		UtilXML xmlUtil = new UtilXML();

		String returnStr = "";
		
        SysInfoVO sysVO = coreData.getSysVO();

		for (int i = 0; i < SMGSendList.size(); i++) {
			SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
			try {
				// 频道扫描信息下发 timeout 1000*60*3 三分钟
				returnStr = utilXML.SendDownXML(downString, smg
						.getURL(), CommonUtility.CONN_WAIT_TIMEOUT,
						bsData);
				if(returnStr == null || returnStr.equals("")) {
					log.error("取得星座图失败: " + smg.getURL());
					continue;
				}
				
				break;
			} catch (CommonException e) {
				log.error("向SMG下发星座图出错信息：" + e.getMessage());
				log.error("向SMG下发星座图出错URL：" + smg.getURL());
			}
		}
		
		if (returnStr == null || returnStr.equals("")) {
			returnStr = utilXML.getReturnXML(bsData, 1);
		}
		try {
			utilXML.SendUpXML(returnStr, bsData);
		} catch (CommonException e) {
			log.error("上发频道扫描信息失败: " + e.getMessage());
		}

		bsData = null;
		downString = null;
		SMGSendList = null;
		utilXML = null;
        
    }
    
}
