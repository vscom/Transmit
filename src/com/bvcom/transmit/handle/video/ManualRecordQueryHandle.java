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
 * 手动录制
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
     * 1. 向TSC下发指令
     * 2. 下发成功后返回成功。
     * 3. 等TSC录制完成后主动上报
     *
     */
    public void downXML() {
        // 返回数据
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
            log.error("手动录制StringToXML Error: " + e.getMessage());
        };
        
        List<ManualRecordQueryVO> ManualRecordlist = ManualRecord.getIndexByDownXml(document);
        
        for(int i=0; i<ManualRecordlist.size(); i++) {
        	ManualRecordQueryVO vo  = ManualRecordlist.get(i);
            try {
            	rtvsVO.setFreq(vo.getFreq());
            	rtvsVO.setServiceID(vo.getServiceID());
            	// 取得手动录制组播地址和端口号
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
    			log.error("取得手动录制出错: " + e1.getMessage());
    		}
    		break;
        }


		this.downString = ManualRecord.createForDownXML(bsData, ManualRecordlist, rtvsVO);
		
        // 取得下发URL列表信息
        TSCList = coreData.getTSCList();
        
        String url = "";
        // 下发指令
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            try {
                if(!url.equals(tsc.getURL())) {
                    // 选台信息下发 timeout 1000*30 三十秒
                    utilXML.SendDownNoneReturn(this.downString, tsc.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                url = tsc.getURL();
                // 只有一个通道做手动选台
                //break;
            } catch (CommonException e) {
                log.error("下发手动录制出错：" + tsc.getURL());
            }
            //去掉注释 手动录像给所有TSC下发
            //break;
        }
        
        upString = ManualRecord.ReturnXMLByURL(bsData, 0);
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("手动录制选台信息失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        TSCList = null;
        utilXML = null;
        ManualRecord = null;
    }
    
}
