package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.rec.NVRTaskRecordDownInquiryParse;
import com.bvcom.transmit.parse.rec.NVRTaskRecordInquiryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

/**
 * ����¼������
 * FIXME Ŀǰֻ����һ̨TSC
 * @author Bian Jiang
 *
 */
public class NVRTaskRecordDownInquiryHandle {

    private static Logger log = Logger.getLogger(NVRTaskRecordDownInquiryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private static MemCoreData coreData = MemCoreData.getInstance();
    
    List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRTaskRecordDownInquiryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·������е�TSC
     * 2. ����TSC������Ϣ
     * 3. �ϱ���Ϣ������
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("����¼��StringToXML Error: " + e.getMessage());
        };
        
        NVRTaskRecordDownInquiryParse nvrTaskRecordInquiry = new NVRTaskRecordDownInquiryParse();
        
        NVRTaskRecordInquiryHandle NVRTaskRecordInquiryHandle = new NVRTaskRecordInquiryHandle();
        List<ProvisionalRecordTaskSetVO> NVRTaskRecordInquiryList = nvrTaskRecordInquiry.getIndexByDownXml(document);
        
        // ȡ��TSC�����ļ���Ϣ
//        List TSCList = new ArrayList();
        
        // ȡ���·�TSC URL�б���Ϣ
        for(int i= 0; i<NVRTaskRecordInquiryList.size(); i++) {
            ProvisionalRecordTaskSetVO vo = NVRTaskRecordInquiryList.get(i);
            
            vo = NVRTaskRecordInquiryHandle.selectTaskIndex(vo);
//            CommonUtility.checkTSCChannelIndex(vo.getIndex(), TSCList);
        }
        
        // TSC �·�ָ��,  
        // FIXME ���ͬʱ����̨TSC���ͣ�ֻ�������һ�εķ�������
        String url = "";
        for (int i=0; i< TSCSendList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(i);
            try {
                if (!url.equals(tsc.getURL())) {
					// ����¼����Ϣ�·� timeout 1000*30 ��ʮ��
					upString = utilXML.SendDownXML(this.downString, tsc
							.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
					url = tsc.getURL();
                    if(upString.equals("")) {
                    	log.info("������ϢΪ��: " + tsc.getURL());
                    	continue;
                    } else {
                    	break;
                    }
				}
            } catch (CommonException e) {
                log.error("����¼�������� TSC �·�����¼�����" + tsc.getURL());
                upString = "";
                continue;
            }
        } // TSC �·�ָ�� END
        
        try {
          	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	} else {
//        		Thread.sleep(CommonUtility.HISTORY_DOWN_WAIT_TIMEOUT);	
        	}
          	
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("����¼��������Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
//        TSCList = null;
        utilXML = null;
    }
    
}
