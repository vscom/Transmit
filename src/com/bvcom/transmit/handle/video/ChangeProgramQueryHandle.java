package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.video.ChangeProgramQueryParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.ChangeProgramQueryVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class ChangeProgramQueryHandle {
    
    private static Logger log = Logger.getLogger(ChangeProgramQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public ChangeProgramQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. SMG 下发手动选台
     * 2. 成功后，下发指令入库(记录当前节目信息)
     * 3. 从指定地方(数据库或配置文件)取得实时视频URL
     * 4. URL写入XML并上报XML给中心
     *
     */
    public void downXML() {
        // 返回数据
        String upString = "";
        //List SMGSendList = new ArrayList();
        
        boolean isErr = false;
        
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
		} catch (DaoException e1) {
			log.error("取得实时视频URL错误: " + e1.getMessage());
			isErr = true;
		}
        
        ChangeProgramQueryParse ChangeProgram = new ChangeProgramQueryParse();
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("手动选台StringToXML Error: " + e.getMessage());
        }
        // 取得下发XML对象数据
        ChangeProgramQueryVO vo = ChangeProgram.getDownObject(document);
//        System.out.println(vo);
        // 查看实时视频是否在一对一监测的节目里面
        SetAutoRecordChannelVO SetAutoRecordChannelVO = new SetAutoRecordChannelVO();
        SetAutoRecordChannelVO.setFreq(vo.getFreq());
        SetAutoRecordChannelVO.setServiceID(vo.getServiceID());
        
        int isRemapping = 0;
        
        try {
        	isRemapping = SetAutoRecordChannelHandle.isHaveProgramInRemapping(SetAutoRecordChannelVO);
        } catch (Exception ex) {
        }
        
        if (isRemapping == 0) {
        	// 没有取得一对一监测节目
            try {
            	this.downString = this.downString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
            	this.downString = this.downString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
            	
                if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
                	this.downString = this.downString.replaceAll("Index=\"0\"", "Index=\"" + rtvsVO.getSmgIndex() + "\"");
                }
                // 选台信息下发 timeout 1000*3 三秒
                utilXML.SendDownNoneReturn(this.downString, rtvsVO.getSmgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                // 只有一个通道做手动选台
                //break;

            } catch (Exception e) {
                log.error("下发手动选台出错：" + rtvsVO.getSmgURL());
                isErr = true;
            }
        } else {
        	// 取得一对一监测节目
        	rtvsVO.setRtvsIP(SetAutoRecordChannelVO.getUdp());
        	rtvsVO.setRtvsPort(SetAutoRecordChannelVO.getPort());
        } 

        // RTVS修改输入流的IP和端口
        RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setRunTime(vo.getRunTime());
        rtvsVO.setFreq(vo.getFreq());
        rtvsVO.setServiceID(vo.getServiceID());
        rtvsVO.setIndex(0);
        
        
        //2012-07-16 第一通道：VPID:102  APID:103 第二通道新增加VPID  vpid:502 apid:503
        try
        {
	    	if(rtvsVO.getSmgURL().contains("Setup1")){
	    		rtvsVO.setVideoPID(102);
	    		rtvsVO.setAudioPID(103);
	    	}else if(rtvsVO.getSmgURL().contains("Setup2")){
	    		rtvsVO.setVideoPID(502);
	    		rtvsVO.setAudioPID(503);
	    	}
        }
        catch(Exception ex){
        	
        }
        
        //CodingFormat="cbr" Width="960"  Height="544" Fps="25" Bps="1500000" 
        try{
        	if(vo.getCodingFormat().equals(null)){
        		
        	}
        	else
        	{
        		rtvsVO.setCodingFormat(vo.getCodingFormat());
        		rtvsVO.setWidth(vo.getWidth());
        		rtvsVO.setHeight(vo.getHeight());
        		rtvsVO.setBps(vo.getBps());
        		rtvsVO.setFps(vo.getFps());
        	}
        	
        }catch(Exception ex){
        	System.out.println(ex.getMessage());
        }
    	
        String rtvsString = RTVSReset.createForDownXML(bsData, rtvsVO);
        
        
        try {
          
        	// 选台信息下发 timeout 1000*10十秒
        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);//
        } catch (CommonException e) {
            log.error("下发RTVS修改输入流的IP和端口出错：" + rtvsVO.getRTVSResetURL());
            isErr = true;
        }
        
		String url = "";
		
        try {
            document = utilXML.StringToXML(upString);
            url = RTVSReset.getReturnURL(document);
        } catch (CommonException e) {
        	isErr = true;
            log.error("视频URL StringToXML Error: " + e.getMessage());
        }
        
        if (isErr) {
            // 失败
            upString = utilXML.getReturnXML(bsData, 1);
        } else if(url.equals("")){
        	upString = utilXML.getReturnXML(bsData, 1);
        }else{
            // 成功
            // 手动选台入库
            try {
                upChangeProgramTable(vo, this.downString);
            } catch (DaoException e) {
                log.error("手动选台更新数据库错误: " + e.getMessage());
            }
            // 实时视频URL 从配置文件读取
            //MemCoreData coreData = MemCoreData.getInstance();
            upString = ChangeProgram.ReturnXMLByURL(bsData, url, 0, vo.getIndex());
        }
        
        try {
            // 等待一秒钟，让SMG打出流，防止RTVS在没有接到流就重启。
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("上发手动选台信息失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        ChangeProgram = null;
    }
    
    
    
    /**
     * 更新入库频道扫描表
     * @throws DaoException 
     */
    private static void upChangeProgramTable(ChangeProgramQueryVO vo, String downXML) throws DaoException {

        StringBuffer strBuff = new StringBuffer();
        
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = DaoSupport.getJDBCConnection();
        
        // update channelstatus c set freq = 6000000, qam = 'QAM128' where channelindex = 1
        
		strBuff.append("update monitorprogramquery c set ");
		// statusFlag: 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播
		strBuff.append("statusFlag = 3, ");
		strBuff.append(" xml = '" + downXML + "', ");
		// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
		strBuff.append(" RunType = 1, ");
		strBuff.append(" Freq = " + vo.getFreq() + ", ");
		strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
		strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where statusFlag = 3 ");
        
        //log.info("手动选台更新数据库：" + strBuff.toString());
        
        try {
            statement = conn.createStatement();
            
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("手动选台更新数据库错误: " + e.getMessage());
        } finally {
            DaoSupport.close(rs);
            DaoSupport.close(statement);
            DaoSupport.close(conn);
        }
        //log.info("手动选台更新数据库成功!");
    }
    
    public String getdownXML(ChangeProgramQueryVO vo) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"4\" MsgID=\"2\" Type=\"MonDown\" DateTime=\"2002-08-17 15:30:00\" SrcCode=\"110000X01\" DstCode=\"110000N01\" SrcURL=\"http://10.24.32.28:8089\"  Priority=\"1\">");
        strBuf.append("<ChangeProgramQuery>");
        strBuf.append("<ChangeProgram  Index=\" "+vo.getIndex() +" \" Freq=\""+vo.getFreq()+"\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\""+vo.getServiceID()+"\" VideoPID=\"1032\" AudioPID=\"1033\"/>");
        strBuf.append("</ChangeProgramQuery> </Msg>");
        return strBuf.toString();
    }
    
    
    
    
    
    
    
    
}
