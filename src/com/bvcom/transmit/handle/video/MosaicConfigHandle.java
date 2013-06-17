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
 * 多画面合成(马赛克)
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
     * 1. 取得IPM所有的地址
     * 2. 根据 RecordType: 0:不录像 1:异态触发录像 2:自动录像 3:多画面合成(马赛克), 下发给RecordType=3的IPM地址
     * 3. 向中心上报成功信息
     */
    public void downXML() {
        // 返回数据
        String upString = "";
       
        boolean isErr = false;
        
        List IPMList = coreData.getIPMList();//IPM的列表信息
        
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
		} catch (DaoException e1) {
			log.error("取得多画面合成(马赛克)URL错误: " + e1.getMessage());
			isErr = true;
		}
        
        // IPM 下发指令
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
            	// RecordType: 0:不录像 1:异态触发录像 2:自动录像 3:多画面合成(马赛克)
            	if (ipm.getRecordType() == 3) {
            		// 任务录像信息下发 timeout 1000*3 三秒
                    utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            	}
            } catch (CommonException e) {
                log.error("向IPM下发多画面合成(马赛克)出错：" + ipm.getURL());
                isErr = true;
            }
        } // IPM 下发指令 END
        
        // RTVS修改输入流的IP和端口
        RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setFreq(rtvsVO.getFreq());
        rtvsVO.setServiceID(rtvsVO.getServiceID());
        rtvsVO.setIndex(0);
        String rtvsString = RTVSReset.createForDownXML(bsData, rtvsVO);
        
        
        if (isErr) {
            // 失败
            upString = utilXML.getReturnXML(bsData, 1);
        } else {
	        try {
	        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
	        } catch (CommonException e) {
	            log.error("多画面合成(马赛克)下发RTVS修改输入流的IP和端口出错：" + rtvsVO.getRTVSResetURL());
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
            log.error("视频URL StringToXML Error: " + e.getMessage());
        }
        
        if (isErr) {
            // 失败
            upString = getReturnXML(url, bsData, 1);
        } else {
	        try {
	        	 upString = getReturnXML(url, bsData, 0);
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("多画面合成(马赛克)信息失败: " + e.getMessage());
	        }
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
    }
    
    /**
     * 取得返回的XML信息
     * @param head XML数据对象 
     * @param value 0:成功 1:失败
     * @return XML文本信息
     */
    public String getReturnXML(String url, MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
        }
        strBuf.append("<ReturnInfo> \r\n <MosaicUrl URL=\"" + url	+ "\" /> \r\n</ReturnInfo>\r\n");
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
}
