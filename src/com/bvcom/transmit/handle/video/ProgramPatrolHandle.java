package com.bvcom.transmit.handle.video;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.video.ProgramPatrolParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * ��Ѳ�������
 * @author Bian Jiang
 *
 */
public class ProgramPatrolHandle {

    private static Logger log = Logger.getLogger(ProgramPatrolHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public ProgramPatrolHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·������е�IPM
     * 2. �ϱ��ɹ���Ϣ������
     */
    public void downXML() {
        // ��������
        String upString = "";
        MemCoreData coreData = MemCoreData.getInstance();
        // ȡ��IPM�����ļ���Ϣ
        List IPMList = coreData.getIPMList();
        
        ProgramPatrolParse programPatrolParse = new ProgramPatrolParse();
        
        // IPM �·�ָ��
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
                // ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
            	upString = utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("��Ѳ�������IPM�·�����" + ipm.getURL());
            }
            
            if(upString != null && !upString.trim().equals("")) {
            	break;
            }
            
        } // IPM �·�ָ�� END
        
        if(upString.equals("")) {
        	upString = utilXML.getReturnXML(this.bsData, 1);
        } else {
            Document document = null;
            try {
                document = utilXML.StringToXML(upString);
            } catch (CommonException e) {
                log.error("��Ѳ�������StringToXML Error: " + e.getMessage());
                log.error("��Ѳ�������: " + upString);
            }
            List<MonitorProgramQueryVO> programPatrolList = programPatrolParse.parseReturnXml(document);
            
            try {
            	document.setXMLEncoding("GB2312");
				MonitorProgramQueryHandle.updataProgramPatrolGroup(programPatrolList, document.asXML());
			} catch (DaoException e) {
				log.error("��Ѳ���Ⱥ�Ÿ������ݿ�ʧ��: " + e.getMessage());
				log.error("��Ѳ�������: " + upString);
			}
            
        }
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("��Ѳ��������ϱ�ʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
    }
}
