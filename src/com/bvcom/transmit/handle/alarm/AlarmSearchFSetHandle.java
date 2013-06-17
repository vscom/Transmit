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
	 * �����ϱ�Ƶ�ʲ�ѯ
	 */
	@SuppressWarnings("unchecked")
	public void downXML() {
		
		List MSGSendList = new ArrayList();
        //List IPMSendList = new ArrayList();
		// ��������
        @SuppressWarnings("unused")
		String upString = "";
        AlarmSearchFSetParse AlarmSearchFSet = new AlarmSearchFSetParse();
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("�����ϱ�Ƶ��StringToXML Error: " + e.getMessage());
        }
        
//        List<AlarmSearchFSetVO> volist = AlarmSearchFSet.getIndexByDownXml(document);
//        for(int i=0;i<volist.size();i++){
//        	CommonUtility.checkSMGChannelIndex(volist.get(i).getIndex() , MSGSendList);
//            //CommonUtility.checkIPMChannelIndex(volist.get(i).getIndex() , IPMSendList);
//        }
        
//        for(int j=0;j<IPMSendList.size();j++){
//            IPMInfoVO ipm = (IPMInfoVO) MSGSendList.get(j);
//            try {
//                // ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
//                upString = utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
//            } catch (CommonException e) {
//                log.error("�����ϱ�Ƶ��IPM�·�����¼�����" + ipm.getURL());
//            }
//        }
        
    	for(int j=0;j<MSGSendList.size();j++){
    		SMGCardInfoVO smg = (SMGCardInfoVO) MSGSendList.get(j);
    		try {
                // ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
    			upString = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("�����ϱ�Ƶ��SMG�·�����¼�����" + smg.getURL());
            }
        }
        try {
            utilXML.SendUpXML(document.asXML(), bsData);
        } catch (CommonException e) {
            log.error("�����ϱ�Ƶ�ʻظ�ʧ��: " + e.getMessage());
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
