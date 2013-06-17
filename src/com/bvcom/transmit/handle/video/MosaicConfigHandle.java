package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.rec.ProvisionalRecordTaskSetParse;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * �໭��ϳ�(������)
 * @author Bian Jiang
 * @date 2010.12.9
 */
public class MosaicConfigHandle {
	
    private static Logger log = Logger.getLogger(MosaicConfigHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public MosaicConfigHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. ȡ��IPM���еĵ�ַ
     * 2. ���� RecordType: 0:��¼�� 1:��̬����¼�� 2:�Զ�¼�� 3:�໭��ϳ�(������), �·���RecordType=3��IPM��ַ
     * 3. �������ϱ��ɹ���Ϣ
     */
    public void downXML() {
        // ��������
        String upString = "";
       
        boolean isErr = false;
        
        List IPMList = coreData.getIPMList();//IPM���б���Ϣ
        
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
		} catch (DaoException e1) {
			log.error("ȡ�ö໭��ϳ�(������)URL����: " + e1.getMessage());
			isErr = true;
		}
        
        // IPM �·�ָ��
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
            	// RecordType: 0:��¼�� 1:��̬����¼�� 2:�Զ�¼�� 3:�໭��ϳ�(������)
            	if (ipm.getRecordType() == 3) {
            		// ����¼����Ϣ�·� timeout 1000*3 ����
                    utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            	}
            } catch (CommonException e) {
                log.error("��IPM�·��໭��ϳ�(������)����" + ipm.getURL());
                isErr = true;
            }
        } // IPM �·�ָ�� END
        
        // RTVS�޸���������IP�Ͷ˿�
        RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setFreq(rtvsVO.getFreq());
        rtvsVO.setServiceID(rtvsVO.getServiceID());
        rtvsVO.setIndex(0);
        String rtvsString = RTVSReset.createForDownXML(bsData, rtvsVO);
        
        
        if (isErr) {
            // ʧ��
            upString = utilXML.getReturnXML(bsData, 1);
        } else {
	        try {
	        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
	        } catch (CommonException e) {
	            log.error("�໭��ϳ�(������)�·�RTVS�޸���������IP�Ͷ˿ڳ���" + rtvsVO.getRTVSResetURL());
	            isErr = true;
	        }
        }
        
		String url = "";
        Document document = null;
        try {
            document = utilXML.StringToXML(upString);
            url = RTVSReset.getReturnURL(document);
        } catch (CommonException e) {
        	isErr = true;
            log.error("��ƵURL StringToXML Error: " + e.getMessage());
        }
        
        if (isErr) {
            // ʧ��
            upString = getReturnXML(url, bsData, 1);
        } else {
	        try {
	        	 upString = getReturnXML(url, bsData, 0);
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("�໭��ϳ�(������)��Ϣʧ��: " + e.getMessage());
	        }
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
    }
    
    /**
     * ȡ�÷��ص�XML��Ϣ
     * @param head XML���ݶ��� 
     * @param value 0:�ɹ� 1:ʧ��
     * @return XML�ı���Ϣ
     */
    public String getReturnXML(String url, MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
        }
        strBuf.append("<ReturnInfo> \r\n <MosaicUrl URL=\"" + url	+ "\" /> \r\n</ReturnInfo>\r\n");
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
}
