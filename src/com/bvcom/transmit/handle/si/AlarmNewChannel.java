package com.bvcom.transmit.handle.si;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.si.ChannelScanQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;

public class AlarmNewChannel extends Thread {

    private static Logger log = Logger.getLogger(AlarmNewChannel.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public void run() {
    	log.info("新频道报警开始");
    	while(true) {
    		
    		downXML();
    		try {
    			Thread.sleep(CommonUtility.ALARM_NEW_CHANNEL_SCAN_TASK_TIME);
    		} catch(Exception ex) {
    			
    		}
    	}
    }
    
    private void downXML() {
        
        List SMGSendList = new ArrayList();
        
        CommonUtility.checkSMGChannelType("AlarmNewChannel", SMGSendList);

        String upString = "";
        
        upString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n";
        upString += "<Msg Version=\"2.3\" MsgID=\"0\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"110004\" DstCode=\"110004\" SrcURL=\"http://192.168.0.34/transmit\" Priority=\"1\">\r\n";
        upString += " <ChannelScanQuery ScanTime=\"\" SymbolRate=\"6875\" QAM=\"QAM64\" />     \r\n";
        upString += "</Msg>";
        
        UtilXML utilXML = new UtilXML();
        
        String retXML = "";
        List<ChannelScanQueryVO> ChannelScanQueryVOList = null;
        Document document = null;
        
        String channelScanPath = "";
        
		try {
			channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_3;

		} catch (Exception e1) {
			// e1.printStackTrace();
			log.error("取得频道扫描信息失败: " + channelScanPath);
		}
		
        for (int i=0; i< SMGSendList.size(); i++) {
            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
            try {
            	
                // 频道扫描信息下发 timeout 1000*60*10 十分钟
                retXML = utilXML.SendDownXML(upString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
                
            	if(retXML.equals("")) {
            		log.error("频道扫描结果为空");
            		break;
            	}
            	
                try {
                    ChannelScanQueryParse ChannelScanQueryParse = new ChannelScanQueryParse();
                    
                	retXML = retXML.replaceAll("'", " ");
                	
                    document = utilXML.StringToXML(retXML);
                    ChannelScanQueryVOList = ChannelScanQueryParse.getReturnObject(document);
                    
                    ChannelScanQueryVOList = getAlarmNewChannelList(ChannelScanQueryVOList);
                    
                } catch (Exception e) {
                    log.error("取得频道扫描结果失败 Error: " + e.getMessage());
                    e.printStackTrace();
                }
                
                break;
            } catch (Exception e) {
                log.error("向SMG下发频道扫描出错：" + smg.getURL());
            }
        }
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        
        bsData.setVersion("2.3");
        bsData.setCenterMsgID(String.valueOf(CommonUtility.getMsgID()));
        bsData.setSrcCode(sysVO.getSrcCode());
        bsData.setDstCode(sysVO.getDstCode());
        bsData.setStatusQueryType("AlarmNewChannel");
        bsData.setReplyID("-1");
        bsData.setSrcURL(sysVO.getCenterAlarmURL());
        
        ChannelScanQueryParse channelScanParse = new ChannelScanQueryParse();
        upString = channelScanParse.createChannelScanReturnXML(ChannelScanQueryVOList, bsData, 0);
        
        log.info(upString);
        
        // 报警后入库操作
		// 只有一个
		CommonUtility.StoreIntoFile(retXML, channelScanPath);
        CommonUtility.StoreIntoFile(retXML, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
        
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("上发频道扫描信息失败: " + e.getMessage());
		}
        
        for(int i=0; i<ChannelScanQueryVOList.size(); i++) {
        	ChannelScanQueryVO vo = ChannelScanQueryVOList.get(i);
        	if(!vo.getIsNewProgram()) {
        		continue;
        	}
        	ChannelScanQueryHandle ChannelScanQueryHandle = new ChannelScanQueryHandle();
        	try {
        		ChannelScanQueryHandle.upChannelScanTable(vo);
        	}catch (Exception ex) {
        		
        	}
        }
        
        SMGSendList = null;
        ChannelScanQueryVOList = null;
    }
    
    private List<ChannelScanQueryVO> getAlarmNewChannelList(List<ChannelScanQueryVO> VOList) throws DaoException {

		StringBuffer strBuff = null;
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for(int i=0; i<VOList.size(); i++) {
			ChannelScanQueryVO vo = VOList.get(i);
			ResultSet rs = null;
			strBuff = new StringBuffer();
			// 取得相关节目频点信息
			strBuff.append("SELECT * FROM channelscanlist c where lastflag = 1 and Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());

			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					vo.setIsNewProgram(true);
				}
				
			} catch (Exception e) {
				log.error("新节目报警处理错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
		}
		
		DaoSupport.close(conn);
		return VOList;
	}
    
}
