package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.rec.ManualRecordQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ManualRecordQueryVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * �ֶ�¼��
 * @author Bian Jiang
 *
 */
public class ManualRecordQueryHandle {
    
    private static Logger log = Logger.getLogger(ManualRecordQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    SetAutoRecordChannelHandle setAutoRecordHandle = new SetAutoRecordChannelHandle();
    
    public ManualRecordQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. ��TSC�·�ָ��
     * 2. �·��ɹ��󷵻سɹ���
     * 3. ��TSC¼����ɺ������ϱ�
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
        
        List TSCList = new ArrayList();
        //List SMGList = new ArrayList();
        
        ManualRecordQueryParse ManualRecord = new ManualRecordQueryParse();
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("�ֶ�¼��StringToXML Error: " + e.getMessage());
        };
        
        List<ManualRecordQueryVO> ManualRecordlist = ManualRecord.getIndexByDownXml(document);
        
        for(int i=0; i<ManualRecordlist.size(); i++) {
        	ManualRecordQueryVO vo  = ManualRecordlist.get(i);
            try {
            	rtvsVO.setFreq(vo.getFreq());
            	rtvsVO.setServiceID(vo.getServiceID());
            	// ȡ���ֶ�¼���鲥��ַ�Ͷ˿ں�
    			rtvsVO = MonitorProgramQueryHandle.GetManualRecordProgramInfo(rtvsVO);
    			
    			SetAutoRecordChannelVO SetAutoRecordChannelVO = new SetAutoRecordChannelVO();
    			SetAutoRecordChannelVO.setFreq(vo.getFreq());
    			SetAutoRecordChannelVO.setServiceID(vo.getServiceID());
    			
    			int isHav = setAutoRecordHandle.isHaveProgramInRemapping(SetAutoRecordChannelVO);
    			
    			if(isHav == 1) {
    				rtvsVO.setRtvsIP(SetAutoRecordChannelVO.getUdp());
    				rtvsVO.setRtvsPort(SetAutoRecordChannelVO.getPort());
    			} else {
                    continue;
                }
    			
    			
    		} catch (DaoException e1) {
    			log.error("ȡ���ֶ�¼�Ƴ���: " + e1.getMessage());
    		}
    		break;
        }


		this.downString = ManualRecord.createForDownXML(bsData, ManualRecordlist, rtvsVO);
		
        // ȡ���·�URL�б���Ϣ
        TSCList = coreData.getTSCList();
        
        String url = "";
        // �·�ָ��
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            try {
                if(!url.equals(tsc.getURL())) {
                    // ѡ̨��Ϣ�·� timeout 1000*30 ��ʮ��
                    utilXML.SendDownNoneReturn(this.downString, tsc.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                url = tsc.getURL();
                // ֻ��һ��ͨ�����ֶ�ѡ̨
                //break;
            } catch (CommonException e) {
                log.error("�·��ֶ�¼�Ƴ���" + tsc.getURL());
            }
            //ȥ��ע�� �ֶ�¼�������TSC�·�
            //break;
        }
        
        upString = ManualRecord.ReturnXMLByURL(bsData, 0);
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("�ֶ�¼��ѡ̨��Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        TSCList = null;
        utilXML = null;
        ManualRecord = null;
    }
    
}
