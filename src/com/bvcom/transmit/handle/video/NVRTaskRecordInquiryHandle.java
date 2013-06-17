package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.rec.NVRTaskRecordInquiryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

/**
 * 任务录像查看
 * FIXME 目前只考虑一台TSC
 * @author Bian Jiang
 *
 */
public class NVRTaskRecordInquiryHandle {

    private static Logger log = Logger.getLogger(NVRTaskRecordInquiryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private static MemCoreData coreData = MemCoreData.getInstance();
    
    List TSCSendList = coreData.getTSCList();//tsc的列表信息
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRTaskRecordInquiryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    public NVRTaskRecordInquiryHandle() {
    }
    
    /**
     * 1. 下发给TSC
     * 2. 接收TSC返回信息
     * 3. 上报信息给中心
     *
     */
    public void downXML() {
        
        // 返回数据
        String upString = "";
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("任务录像查看StringToXML Error: " + e.getMessage());
        };
        
        NVRTaskRecordInquiryParse nvrTaskRecordInquiry = new NVRTaskRecordInquiryParse();
        
        List<ProvisionalRecordTaskSetVO> NVRTaskRecordInquiryList = nvrTaskRecordInquiry.getIndexByDownXml(document);
        
        // 取得TSC配置文件信息
//        List TSCList = new ArrayList();
        
        // 取得下发TSC URL列表信息
        for(int i= 0; i<NVRTaskRecordInquiryList.size(); i++) {
            ProvisionalRecordTaskSetVO vo = NVRTaskRecordInquiryList.get(i);
            
            vo = this.selectTaskIndex(vo);
//            CommonUtility.checkTSCChannelIndex(vo.getIndex(), TSCList);
        }
        
        // TSC 下发指令,  
        // FIXME 如果同时给多台TSC发送，只接收最后一次的返回数据
        String url = "";
        for (int i=0; i< TSCSendList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(i);
            try {
                if (!url.equals(tsc.getURL())) {
					// 任务录像信息下发 timeout 1000*30 三十秒
					upString = utilXML.SendDownXML(this.downString, tsc
							.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
					url = tsc.getURL();
                    if(upString.equals("")) {
                    	log.info("返回信息为空: " + tsc.getURL());
                    	continue;
                    } else {
                    	break;
                    }
				}
            } catch (CommonException e) {
                log.error("任务录像查看向 TSC 下发任务录像出错：" + tsc.getURL());
                upString = "";
                continue;
            }
        } // TSC 下发指令 END
        
        try {

        	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	} else {
//        		Thread.sleep(CommonUtility.VIDEO_RETURN_WAIT_TIME);	
        	}
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("任务录像查看信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
//        TSCList = null;
        utilXML = null;
    }
    
    public ProvisionalRecordTaskSetVO selectTaskIndex(ProvisionalRecordTaskSetVO vo) {
		StringBuffer strBuff = new StringBuffer();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn;
		
		try {
			conn = DaoSupport.getJDBCConnection();
		
			try {
				strBuff.append("select tr_index from taskrecord where Taskid = '" + vo.getTaskID() + "'");
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				int channelindex = 1;;
				while(rs.next()){
					channelindex = Integer.parseInt(rs.getString("tr_index"));
				}
				
				vo.setIndex(channelindex);
			} catch (Exception e) {
				log.error("任务录像查询数据库错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		} catch (DaoException e1) {
			log.error("任务录像查询数据库错误: " + e1.getMessage());
		}
		return vo;
    }
}
