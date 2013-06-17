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
     * 1. �·������е�TSC
     * 2. �ϱ���Ϣ������
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("����¼���������StringToXML Error: " + e.getMessage());
        };
        
        MemCoreData coreData = MemCoreData.getInstance();
        // ȡ��TSC�����ļ���Ϣ
        List TSCList = coreData.getTSCList();
        List SMGList = coreData.getSMGCardList();
        
        // TSC �·�ָ��,  
        // FIXME Ŀǰֻ����һ��TSC�����
        String url = "";
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            try {
                if(!url.equals(tsc.getURL())) {
                    // ����¼����Ϣ�·� timeout 1000*30 ��ʮ��
                    utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
                    url = tsc.getURL();
                }
            } catch (CommonException e) {
                log.error("����¼�����������TSC�·�����¼�����" + tsc.getURL());
                upString = "";
            }
        } // TSC �·�ָ�� END
        
        // ��������
        for (int i=0; i<SMGList.size(); i++) {
        	SMGCardInfoVO smg = (SMGCardInfoVO) SMGList.get(i);
            try {
                // �����������
                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
                	// ����ת���·�
                	utilXML.SendDownNoneReturn(this.downString, smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                
            } catch (CommonException e) {
                log.error("�·��Զ�¼��SMG����" + smg.getURL());
            }
        }
        try {
    		upString = utilXML.getReturnXML(bsData, 0);
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("����¼�����������Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        TSCList = null;
        utilXML = null;
    }
    
}
