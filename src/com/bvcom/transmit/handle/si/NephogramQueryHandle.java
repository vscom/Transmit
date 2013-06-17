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
 * Web2.0 ����ͼ Add By Bian Jiang 2011.1.7
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

		// 3:GetIndexSet(����ָ�������ͼ)
		CommonUtility.checkSMGChannelType("GetIndexSet", SMGSendList);
		UtilXML xmlUtil = new UtilXML();

		String returnStr = "";
		
        SysInfoVO sysVO = coreData.getSysVO();

		for (int i = 0; i < SMGSendList.size(); i++) {
			SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
			try {
				// Ƶ��ɨ����Ϣ�·� timeout 1000*60*3 ������
				returnStr = utilXML.SendDownXML(downString, smg
						.getURL(), CommonUtility.CONN_WAIT_TIMEOUT,
						bsData);
				if(returnStr == null || returnStr.equals("")) {
					log.error("ȡ������ͼʧ��: " + smg.getURL());
					continue;
				}
				
				break;
			} catch (CommonException e) {
				log.error("��SMG�·�����ͼ������Ϣ��" + e.getMessage());
				log.error("��SMG�·�����ͼ����URL��" + smg.getURL());
			}
		}
		
		if (returnStr == null || returnStr.equals("")) {
			returnStr = utilXML.getReturnXML(bsData, 1);
		}
		try {
			utilXML.SendUpXML(returnStr, bsData);
		} catch (CommonException e) {
			log.error("�Ϸ�Ƶ��ɨ����Ϣʧ��: " + e.getMessage());
		}

		bsData = null;
		downString = null;
		SMGSendList = null;
		utilXML = null;
        
    }
    
}
