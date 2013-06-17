package com.bvcom.transmit.handle.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.parse.alarm.LoopAlaInfParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * ѭ�б�������
 * TODO
 * ������ʵʱ������Ѳ���ı�����ѯû���κ��������������Э���ϼ������֣������Э�鲻ʵ�֡�
 * @author Bian Jiang
 *
 */
public class LoopAlaInfHandle {

    private static Logger log = Logger.getLogger(LoopAlaInfHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public LoopAlaInfHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·�����ѯͨ����SMG
     * 2. �ϱ��ɹ���Ϣ������
     *
     */
    public void downXML() {
        
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
        try {
			rtvsVO = MonitorProgramQueryHandle.GetProgramInfoByDownIndex(rtvsVO, downString);
		} catch (DaoException e1) {
			log.error("ȡ��ʵʱ��ƵURL����: " + e1.getMessage());
		}        
        
        String upString = "";
        try {
            // ѭ�б���������Ϣ�·� timeout 1000*10 10��
            utilXML.SendDownNoneReturn(this.downString, rtvsVO.getSmgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
        } catch (CommonException e) {
            log.error("��SMG�·�ѭ�б������ó���" + rtvsVO.getSmgURL());
        }
        
        LoopAlaInfParse LoopAlaInf = new LoopAlaInfParse();
        
        upString = LoopAlaInf.ReturnXMLByURL(this.bsData, 0);
        
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("������Ŀ���޻򿪹��ϱ���Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        utilXML = null;
        LoopAlaInf = null;
    }
    
    
}
