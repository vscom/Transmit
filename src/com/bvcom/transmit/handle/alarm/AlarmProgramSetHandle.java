package com.bvcom.transmit.handle.alarm;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.alarm.AlarmProgramSwitchSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;

public class AlarmProgramSetHandle {

    private static Logger log = Logger.getLogger(AlarmProgramSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public AlarmProgramSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·������е�IPM
     * 2. �ϱ��ɹ���Ϣ������
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
//        List IPMList = new ArrayList();
        MemCoreData coreData = MemCoreData.getInstance();
        // ȡ��IPM�����ļ���Ϣ
        List IPMList = coreData.getIPMList();
        
//        AlarmProgramSwitchSetParse AlarmProgramSwitch = new AlarmProgramSwitchSetParse();
//        AlarmProgramThresholdSetParse AlarmProgramThreshold = new AlarmProgramThresholdSetParse();
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("����¼��StringToXML Error: " + e.getMessage());
        };
        
        /**
         * �����ж� ����ǽ�Ŀ���أ��򱣴���� ��Ӧ��Ŀ����״̬
         * 
         * JI LONG 2011-5-12
         */
        if(bsData.getStatusQueryType().equals("AlarmProgramSwitchSet")) {
        	AlarmProgramSwitchSetParse alarmProgramSwitchSetParse=new AlarmProgramSwitchSetParse();
        	alarmProgramSwitchSetParse.parseDB(document);
        }
//        // ȡ���·�IPM URL�б���Ϣ
//        if(bsData.getStatusQueryType().equals("AlarmProgramSwitchSet")) {
//            // �������ؽ�Ŀ��
//            //List<AlarmProgramSwitchSetVO> AlarmProgramSwitchList = AlarmProgramSwitch.getIndexByDownXml(document);
//            
//
//            
//            // ȡ���·�IPM URL�б���Ϣ
//            for(int i= 0; i<AlarmProgramSwitchList.size(); i++) {
//                AlarmProgramSwitchSetVO vo = AlarmProgramSwitchList.get(i);
//                CommonUtility.checkIPMChannelIndex(vo.getIndex(), IPMList);
//            }
//            
//        } else if(bsData.getStatusQueryType().equals("AlarmProgramThresholdSet")) {
//            // �������޽�Ŀ��
//            List<AlarmProgramThresholdSetVO> AlarmProgramThresholdList = AlarmProgramThreshold.getIndexByDownXml(document);
//            // ȡ���·�IPM URL�б���Ϣ
//            for(int i= 0; i<AlarmProgramThresholdList.size(); i++) {
//                AlarmProgramThresholdSetVO vo = AlarmProgramThresholdList.get(i);
//                CommonUtility.checkIPMChannelIndex(vo.getIndex(), IPMList);
//            }
//        } // ȡ���·�IPM URL�б���Ϣ end
        
        // IPM �·�ָ��,  
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
                // ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
                utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("������Ŀ���޻򿪹���IPM�·�����¼�����" + ipm.getURL());
            }
        } // IPM �·�ָ�� END
        
        upString = utilXML.getReturnXML(this.bsData, 0);
        
//        if(bsData.getStatusQueryType().equals("AlarmProgramSwitchSet")) {
//            // �������ؽ�Ŀ��
//            upString = AlarmProgramSwitch.ReturnXMLByURL(this.bsData, 0);
//            
//            AlarmProgramSwitch = null;
//        } else if(bsData.getStatusQueryType().equals("AlarmProgramThresholdSet")) {
//            // �������޽�Ŀ��
//            upString = AlarmProgramThreshold.ReturnXMLByURL(this.bsData, 0);
//            
//            AlarmProgramThreshold = null;
//        }
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("������Ŀ���޻򿪹��ϱ���Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
        
    }
}
