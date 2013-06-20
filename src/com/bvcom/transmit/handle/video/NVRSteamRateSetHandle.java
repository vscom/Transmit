package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.video.NVRSteamRateSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class NVRSteamRateSetHandle {
	
	private static Logger log = Logger.getLogger(NVRSteamRateSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRSteamRateSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    /**
     * TSC 设置实时视频流率
     * 1.解析xml得到通道index列表
     * 2.下发xml给相应的tsc
     * 3.下发成功后返回成功。
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		 // 返回数据
		@SuppressWarnings("unused")
		String upString = "";
//        List TSCSendList = new ArrayList();//tsc的列表信息
//        List SMGSendList = new ArrayList();//SMG的列表信息
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("实时视频流率StringToXML Error: " + e.getMessage());
        };
        NVRSteamRateSetParse nvrStream = new NVRSteamRateSetParse();
        List<ProvisionalRecordTaskSetVO> nvrStreamlist = nvrStream.getIndexByDownXml(document);
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        String url = "";
//        for(int i=0; i< nvrStreamlist.size(); i++) 
//        {
//        	int index = nvrStreamlist.get(i).getIndex();
//        	CommonUtility.checkTSCChannelIndex(index, TSCSendList);

        List monitorProgramList = null;
		try {
			monitorProgramList = MonitorProgramQueryHandle.GetWatchAndSeeVOList();
		} catch (DaoException e1) {

		}
		
    	for(int j=0;j<monitorProgramList.size();j++)
    	{
    		MonitorProgramQueryVO tsc = (MonitorProgramQueryVO) monitorProgramList.get(j);
			try {
                if(!url.equals(tsc.getRTVSResetURL())) {
//                      历史视频查看下发 timeout 1000*30 三十秒
                    upString = utilXML.SendDownXML(this.downString, tsc.getRTVSResetURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                url = tsc.getRTVSResetURL();
            } catch (CommonException e) {
                log.error("下发实时视频流率TSC出错：" + tsc.getRTVSResetURL());
                upString = "";
            }
    	}
    	// TODO 高清转码板 Del By Bian Jiang 2010.9.23 广州
//        	CommonUtility.checkSMGChannelIndex(index, SMGSendList);
    	 List SMGSendList = coreData.getSMGCardList();
	    	for(int j=0;j<SMGSendList.size();j++) {
	    		SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(j);
	            try {
	                // 高清相关配置
	                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
	                	// 高清转码下发
	                	utilXML.SendDownNoneReturn(this.downString, smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	                }
	                
	            } catch (CommonException e) {
	                log.error("下发高清实时视频流率到SMG出错：" + smg.getURL());
	            }
	    	}
        	
//        }
      //上报回复的xml给中心
        try {
    		upString = utilXML.getReturnXML(bsData, 0);
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("历史视频查回复失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        nvrStream = null;
        
    }

	public MSGHeadVO getBsData() {
		return bsData;
	}

	public void setBsData(MSGHeadVO bsData) {
		this.bsData = bsData;
	}

	public String getDownString() {
		return downString;
	}

	public void setDownString(String downString) {
		this.downString = downString;
	}

	public UtilXML getUtilXML() {
		return utilXML;
	}

	public void setUtilXML(UtilXML utilXML) {
		this.utilXML = utilXML;
	}
}
