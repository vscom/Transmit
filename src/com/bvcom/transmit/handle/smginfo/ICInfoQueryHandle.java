package com.bvcom.transmit.handle.smginfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.handle.video.StopPlayingVideoHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class ICInfoQueryHandle {
	
    private static Logger log = Logger.getLogger(ICInfoQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    private List sMGCardInfoList = new ArrayList();
    
    public ICInfoQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. 读取小卡卡号等信息配置文件
     * 2. 打包xml返回给平台
     */
    public void downXML() {
    	boolean isErr = false;
    	String ICInfoQueryStr=null;
    	
    	/*
    	try {
    		String filePath = getConfigFilePath();
    		ICInfoQueryStr = utilXML.ReadFile(filePath);
    		
    		ICInfoQueryStr=ICInfoQueryStr.substring(38, ICInfoQueryStr.length());
    		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			isErr=true;
		}

		if(ICInfoQueryStr==null||ICInfoQueryStr.equals("")){
			isErr=true;
		}
		*/
    	//从SMG_CARD_INFO表中获取板卡相关信息
    	try
		{
			Statement statement = null;
			Connection conn = DaoSupport.getJDBCConnection();
			ResultSet rs = null;
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("select *  from smg_card_info  ");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				while(rs.next()){
					SMGCardInfoVO cardinfoVO= new SMGCardInfoVO();
					cardinfoVO.setSmgCamCard(rs.getString("smgCamCard").toString());
					cardinfoVO.setSmgCamPostion(rs.getString("smgCamPostion").toString());
					cardinfoVO.setSmgCamDesc(rs.getString("smgCamDesc").toString());
					sMGCardInfoList.add(cardinfoVO);
				}
			}
			catch (Exception e)
			{
				log.info("获取小卡卡号信息："+e.getMessage());
				isErr=true;
			}
			finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
			DaoSupport.close(conn);
		}
		catch(Exception ex)
		{
			isErr=true;
		}
    	
    	
        String returnstr="";
        if(isErr){
        	returnstr = getReturnXML(sMGCardInfoList,this.bsData, 1);
        	try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
            }
        }else{
        	returnstr = getReturnXML(sMGCardInfoList,this.bsData, 0);
        	try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
            }
        }
        
        bsData = null;
        this.downString = null;
        utilXML = null;
    }
    
    /**
     * 取得返回的XML信息
     * @param head XML数据对象 
     * @param value 0:成功 1:失败
     * @return XML文本信息
     */
    public String getReturnXML(List ICInfoQueryList, MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r");
            if(ICInfoQueryList.size()>0){
            	strBuf.append("<ReturnInfo>\r\n");
            	strBuf.append("<ICInfoQuery Total=\""+ICInfoQueryList.size()+"\">\r\n");
            }
            for(int i =0;i<ICInfoQueryList.size();i++)
            {
            	SMGCardInfoVO smg=(SMGCardInfoVO)ICInfoQueryList.get(i);
            	//<ICInfo  CardNO="XX" Position=”第几个机箱第几个卡槽”  Desc="此卡的描述信息"  />
            	strBuf.append("\t<ICInfo CardNO=\""+smg.getSmgCamCard()+"\"  Position=\""+smg.getSmgCamPostion()+"\" Desc=\""+smg.getSmgCamDesc()+"\" />\n");

            }
            if(ICInfoQueryList.size()>0){
            	strBuf.append("</ICInfoQuery>\r\n");
            	strBuf.append("</ReturnInfo>\r\n");
            }
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
        }
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
    private String getConfigFilePath() {
        
        Properties p = new Properties();
        
        String filePath = "";
        InputStream inStr = null;
        
        try {
            inStr = this.getClass().getResourceAsStream("/config.properties");
            p.load(inStr);
            filePath = p.getProperty("ICInfoQueryPath");
        } catch (FileNotFoundException e) {
            CommonUtility.printErrorTrace(e);
        } catch (IOException ioe){
            CommonUtility.printErrorTrace(ioe);
        } finally {
            try {
                if (inStr != null) {
                    inStr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.clear();
        }
        return filePath;
    }

}
