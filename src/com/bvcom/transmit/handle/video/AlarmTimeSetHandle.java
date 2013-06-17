package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.video.AlarmTimeSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.AlarmTimeSetVO;

/**
 * ����ͼ
 * @author Bian Jiang
 *
 */
public class AlarmTimeSetHandle {

    private static Logger log = Logger.getLogger(AlarmTimeSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public AlarmTimeSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·���ָ��ͨ����IPM
     * 2. �ϱ���Ϣ������
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
//        List IPMList = new ArrayList();
        List IPMList = coreData.getIPMList();//IPM���б���Ϣ
        
        Document document = null;
        
        AlarmTimeSetParse AlarmTimeSet = new AlarmTimeSetParse();
        
        try {
            document = utilXML.StringToXML(this.downString);
            AlarmTimeSet.parse(document);
        } catch (CommonException e) {
            log.error("����¼��StringToXML Error: " + e.getMessage());
        };
        
        List<AlarmTimeSetVO> RecordTaskSetList = AlarmTimeSet.getIndexByDownXml(document);
        
        SetAutoRecordChannelVO recordVO = null;
        
        // ȡ���·�IPM URL�б���Ϣ
        for(int i= 0; i<RecordTaskSetList.size(); i++) {
            AlarmTimeSetVO vo = RecordTaskSetList.get(i);
            
            recordVO = new SetAutoRecordChannelVO();
            recordVO.setFreq(vo.getFreq());
            recordVO.setServiceID(vo.getServiceID());
            
            try {
				SetAutoRecordChannelHandle.GetIndexByProgram(recordVO, true);
				this.downString = this.downString.replaceAll("Index=\"" + vo.getIndex() + "\"", "Index=\"" + recordVO.getIndex() + "\"");
            } catch (DaoException e) {
			}
            
            recordVO = null;
        }
        
        // IPM �·�ָ��,  
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
                // ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
                utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("����ͼ��IPM�·�����¼�����" + ipm.getURL());
            }
        } // IPM �·�ָ�� END
        

        upString = AlarmTimeSet.ReturnXMLByURL(this.bsData, 0);
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("����¼��ѡ̨��Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
        AlarmTimeSet = null;
    }
    
}
