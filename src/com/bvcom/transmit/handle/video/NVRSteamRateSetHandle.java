package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.video.NVRSteamRateSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class NVRSteamRateSetHandle {
	
	private static Logger log = Logger.getLogger(NVRSteamRateSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRSteamRateSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    /**
     * TSC ����ʵʱ��Ƶ����
     * 1.����xml�õ�ͨ��index�б�
     * 2.�·�xml����Ӧ��tsc
     * 3.�·��ɹ��󷵻سɹ���
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		 // ��������
		@SuppressWarnings("unused")
		String upString = "";
//        List TSCSendList = new ArrayList();//tsc���б���Ϣ
//        List SMGSendList = new ArrayList();//SMG���б���Ϣ
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("ʵʱ��Ƶ����StringToXML Error: " + e.getMessage());
        };
        NVRSteamRateSetParse nvrStream = new NVRSteamRateSetParse();
        List<ProvisionalRecordTaskSetVO> nvrStreamlist = nvrStream.getIndexByDownXml(document);
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        String url = "";
//        for(int i=0; i< nvrStreamlist.size(); i++) 
//        {
//        	int index = nvrStreamlist.get(i).getIndex();
//        	CommonUtility.checkTSCChannelIndex(index, TSCSendList);

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
//                      ��ʷ��Ƶ�鿴�·� timeout 1000*30 ��ʮ��
                    upString = utilXML.SendDownXML(this.downString, tsc.getRTVSResetURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                url = tsc.getRTVSResetURL();
            } catch (CommonException e) {
                log.error("�·�ʵʱ��Ƶ����TSC����" + tsc.getRTVSResetURL());
                upString = "";
            }
    	}
    	// TODO ����ת��� Del By Bian Jiang 2010.9.23 ����
//        	CommonUtility.checkSMGChannelIndex(index, SMGSendList);
    	 List SMGSendList = coreData.getSMGCardList();
	    	for(int j=0;j<SMGSendList.size();j++) {
	    		SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(j);
	            try {
	                // �����������
	                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
	                	// ����ת���·�
	                	utilXML.SendDownNoneReturn(this.downString, smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	                }
	                
	            } catch (CommonException e) {
	                log.error("�·�����ʵʱ��Ƶ���ʵ�SMG����" + smg.getURL());
	            }
	    	}
        	
//        }
      //�ϱ��ظ���xml������
        try {
    		upString = utilXML.getReturnXML(bsData, 0);
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("��ʷ��Ƶ��ظ�ʧ��: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        nvrStream = null;
        
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
