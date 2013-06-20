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
 * 循切报警设置
 * TODO
 * 本质上实时监测和轮巡监测的报警查询没有任何区别，所以无需从协议上加以区分，建议此协议不实现。
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
     * 1. 下发给轮询通道的SMG
     * 2. 上报成功信息给中心
     *
     */
    public void downXML() {
        
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
        try {
			rtvsVO = MonitorProgramQueryHandle.GetProgramInfoByDownIndex(rtvsVO, downString);
		} catch (DaoException e1) {
			log.error("取得实时视频URL错误: " + e1.getMessage());
		}        
        
        String upString = "";
        try {
            // 循切报警设置信息下发 timeout 1000*10 10秒
            utilXML.SendDownNoneReturn(this.downString, rtvsVO.getSmgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
        } catch (CommonException e) {
            log.error("向SMG下发循切报警设置出错：" + rtvsVO.getSmgURL());
        }
        
        LoopAlaInfParse LoopAlaInf = new LoopAlaInfParse();
        
        upString = LoopAlaInf.ReturnXMLByURL(this.bsData, 0);
        
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("报警节目门限或开关上报信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        utilXML = null;
        LoopAlaInf = null;
    }
    
    
}
