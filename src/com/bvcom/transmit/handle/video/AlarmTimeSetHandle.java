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
 * 运行图
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
     * 1. 下发给指定通道的IPM
     * 2. 上报信息给中心
     *
     */
    public void downXML() {
        // 返回数据
        String upString = "";
//        List IPMList = new ArrayList();
        List IPMList = coreData.getIPMList();//IPM的列表信息
        
        Document document = null;
        
        AlarmTimeSetParse AlarmTimeSet = new AlarmTimeSetParse();
        
        try {
            document = utilXML.StringToXML(this.downString);
            AlarmTimeSet.parse(document);
        } catch (CommonException e) {
            log.error("任务录像StringToXML Error: " + e.getMessage());
        };
        
        List<AlarmTimeSetVO> RecordTaskSetList = AlarmTimeSet.getIndexByDownXml(document);
        
        SetAutoRecordChannelVO recordVO = null;
        
        // 取得下发IPM URL列表信息
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
        
        // IPM 下发指令,  
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
                // 运行图信息下发 timeout 1000*30 三十秒
                utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("运行图向IPM下发任务录像出错：" + ipm.getURL());
            }
        } // IPM 下发指令 END
        

        upString = AlarmTimeSet.ReturnXMLByURL(this.bsData, 0);
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("任务录像选台信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
        AlarmTimeSet = null;
    }
    
}
