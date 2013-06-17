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
     * 1. 下发给所有的IPM
     * 2. 上报成功信息给中心
     *
     */
    public void downXML() {
        // 返回数据
        String upString = "";
//        List IPMList = new ArrayList();
        MemCoreData coreData = MemCoreData.getInstance();
        // 取得IPM配置文件信息
        List IPMList = coreData.getIPMList();
        
//        AlarmProgramSwitchSetParse AlarmProgramSwitch = new AlarmProgramSwitchSetParse();
//        AlarmProgramThresholdSetParse AlarmProgramThreshold = new AlarmProgramThresholdSetParse();
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("任务录像StringToXML Error: " + e.getMessage());
        };
        
        /**
         * 新增判断 如果是节目开关，则保存入库 对应节目开关状态
         * 
         * JI LONG 2011-5-12
         */
        if(bsData.getStatusQueryType().equals("AlarmProgramSwitchSet")) {
        	AlarmProgramSwitchSetParse alarmProgramSwitchSetParse=new AlarmProgramSwitchSetParse();
        	alarmProgramSwitchSetParse.parseDB(document);
        }
//        // 取得下发IPM URL列表信息
//        if(bsData.getStatusQueryType().equals("AlarmProgramSwitchSet")) {
//            // 报警开关节目类
//            //List<AlarmProgramSwitchSetVO> AlarmProgramSwitchList = AlarmProgramSwitch.getIndexByDownXml(document);
//            
//
//            
//            // 取得下发IPM URL列表信息
//            for(int i= 0; i<AlarmProgramSwitchList.size(); i++) {
//                AlarmProgramSwitchSetVO vo = AlarmProgramSwitchList.get(i);
//                CommonUtility.checkIPMChannelIndex(vo.getIndex(), IPMList);
//            }
//            
//        } else if(bsData.getStatusQueryType().equals("AlarmProgramThresholdSet")) {
//            // 报警门限节目类
//            List<AlarmProgramThresholdSetVO> AlarmProgramThresholdList = AlarmProgramThreshold.getIndexByDownXml(document);
//            // 取得下发IPM URL列表信息
//            for(int i= 0; i<AlarmProgramThresholdList.size(); i++) {
//                AlarmProgramThresholdSetVO vo = AlarmProgramThresholdList.get(i);
//                CommonUtility.checkIPMChannelIndex(vo.getIndex(), IPMList);
//            }
//        } // 取得下发IPM URL列表信息 end
        
        // IPM 下发指令,  
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
                // 运行图信息下发 timeout 1000*30 三十秒
                utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("报警节目门限或开关向IPM下发任务录像出错：" + ipm.getURL());
            }
        } // IPM 下发指令 END
        
        upString = utilXML.getReturnXML(this.bsData, 0);
        
//        if(bsData.getStatusQueryType().equals("AlarmProgramSwitchSet")) {
//            // 报警开关节目类
//            upString = AlarmProgramSwitch.ReturnXMLByURL(this.bsData, 0);
//            
//            AlarmProgramSwitch = null;
//        } else if(bsData.getStatusQueryType().equals("AlarmProgramThresholdSet")) {
//            // 报警门限节目类
//            upString = AlarmProgramThreshold.ReturnXMLByURL(this.bsData, 0);
//            
//            AlarmProgramThreshold = null;
//        }
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("报警节目门限或开关上报信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
        
    }
}
