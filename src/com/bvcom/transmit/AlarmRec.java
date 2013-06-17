package com.bvcom.transmit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.handle.alarm.AlarmSearchPSetHandle;
import com.bvcom.transmit.parse.alarm.AlarmSearchFSetParse;
import com.bvcom.transmit.parse.alarm.AlarmSearchPSetParse;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;

public class AlarmRec extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 133665542165182051L;
	private static Logger log = Logger
            .getLogger(AlarmRec.class.getSimpleName());

    MemCoreData coreData = MemCoreData.getInstance();
    
    SysInfoVO sysVO = coreData.getSysVO();
    
    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out
                .println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.println("<title> BVCOM " + CommonUtility.VERSION + "   </title>");
        out.println("</HEAD>  <BODY>");
        out.print("Welcome Home, using the POST method. PowerBy: BVCOM <br />");
        out.print(" VERSION: " + CommonUtility.VERSION + "<br />");
        out.println("<br /> ------------报警接收地址------------------  ");
        out.println("<br />" + request.getContextPath());
        out.println("<br /> RemoteHost: " + request.getRemoteHost());
        out.println("<br /> LocalAddr: " + request.getLocalAddr());
        out.println("<br /> ------------------------------  ");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    /**
     * The doPost method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to
     * post.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(200);

        String clientIp=request.getRemoteAddr();
        InputStreamReader inReader = null;
        String getString = null;
        Document requestDoc = null;

        MSGHeadVO bsData = new MSGHeadVO();
        UtilXML xmlUtil = new UtilXML();
        
        int isManualRecord = 0;

        try {
            try {
                inReader = new InputStreamReader(request.getInputStream(),
                        "UTF-8");// 获得链接该类的流
                getString = CommonUtility.readStringFromURL(inReader);
                if(getString.equals("")){
                	return;
                }
                //System.out.println(getString);
                //log.info("接收到报警数据为："+getString);
                try {
                    OutputStreamWriter ResponseWriter = null;
                    response.setContentType("text/html");
                    ResponseWriter = new OutputStreamWriter(response.getOutputStream(),
                            "UTF-8"); //向客户端返回确认信息 

                    ResponseWriter.write("OK");
                    ResponseWriter.flush();
                    ResponseWriter.close();
                } catch (Exception ex) {
                    //log.error("回复数据出错: " + ex.getMessage());
                }
                /**
                 * 读取请求信息
                 */
            } catch (IOException ex) {
                log.error("接收报警数据出错: " + ex.getMessage());
                log.error("发送请求端IP："+clientIp);
                /**输出错误信息到文件*/
        		PrintWriter pw;
    			try {
    				pw = new PrintWriter(new File("d:/ExceptionAlarm.log"));
    				ex.printStackTrace(pw);
    				pw.flush();
    				pw.close();
    			} catch (Exception e1) {
    			}
    			/******/
                return;
            } finally {
            	if(inReader != null) {
            		inReader.close();
            	}
            }
            
            if (sysVO.getIsAlarmLogEnable() == 1) {
	            log.info("上报IP: ----- " + request.getRemoteAddr() + " ---   Port: " + request.getRemotePort() + " ---- ");
	            log.info("接收报警上报信息:\n" + getString);
            }

            requestDoc = xmlUtil.StringToXML(getString);
            if(requestDoc==null){
            	return;
            }
            requestDoc.setXMLEncoding("GB2312");

            // 从下发的Xml数据中提取头部信息
            boolean retFlg = xmlUtil.getInfoFromDownXml(requestDoc, bsData);
            
//            if (bsData.getDateTime() == null || bsData.getDateTime().startsWith("2000")) {
//            	requestDoc = null;
//            	//System.out.println("=== Please Checking SMG DateTime: " +bsData.getDateTime() + " @ IP: " + request.getRemoteAddr() + " ===");
//            }
            
            MemCoreData coreData = MemCoreData.getInstance();
            SysInfoVO sysVO = coreData.getSysVO();
            // 或者从Java转发取得上报地址
            
            if (getString.indexOf("ManualRecordQuery") <= 0) {
                // 非手动录制主动上报采用本地配置
                bsData.setSrcURL(sysVO.getCenterAlarmURL());
                bsData.setReplyID("-1");
                bsData.setCenterMsgID(String.valueOf(CommonUtility.getMsgID()));
            } else {
            	log.info("手动录制上报地址:\n" + bsData.getSrcURL());
            	log.info("手动录制上报信息:\n" + getString);
            	isManualRecord = 1;
            }

            if(getString.indexOf("AlarmSearchPSet") > 0) {
                // 节目报警主动上报
                AlarmSearchPSetHandle AlarmSearchPSetHandle = new AlarmSearchPSetHandle(getString, bsData);
                getString = AlarmSearchPSetHandle.upXML();
                
                AlarmSearchPSetHandle = null;
            } else {
            	getString = CommonUtility.RegReplaceString(getString, "Time");
            }
            
            if (getString == null || getString.trim().equals("")) {
            	return;
            }
            Document sendDoc = xmlUtil.StringToXML(getString);

            sendDoc.setXMLEncoding("GB2312");
            
            getString = sendDoc.asXML();
            
            // 判断报警信息的频点是否在channelremapping表里面存在,如果不存在就放弃相关报警 Add By: Bian Jiang 2011.03.01
        	List alarmList = null;
        	String type = xmlUtil.getReturnType(getString);
        	bsData.setStatusQueryType(type);
        	AlarmSearchPSetParse alarmSearchPSetParse = null;
        	AlarmSearchFSetParse alarmSearchFSetParse = null;
        	List newAlarmList = null;
        	
        	if (type.equals("AlarmSearchPSet") || type.equals("AlarmSearchFSet")) {
	    		alarmSearchPSetParse = new AlarmSearchPSetParse();
	    		alarmSearchFSetParse = new AlarmSearchFSetParse();
	    		
	        	if (type.equals("AlarmSearchPSet")) {
	        		alarmList = alarmSearchPSetParse.getUpList(sendDoc);
	        	} else if (type.equals("AlarmSearchFSet")) {
	        		alarmList = alarmSearchFSetParse.getUpList(sendDoc);
	        	}
	            
	    		newAlarmList = AlarmSearchPSetHandle.getFreqFromDB(alarmList, type);
	            
	    		if (newAlarmList.size() <= 0) {
	    			log.info("数据库中alarmsearchtable表中存在此条报警信息:\n"+getString+"\n====此报警信息没有恢复,不是新报警信息=======");
	    			return;
	    		}
        	}
            //xmlUtil.replaceXMLMsgHeader(sendDoc, bsData);
            
            if (sysVO.getIsHasAlarmID() != 0 && isManualRecord != 1) {
	            AlarmThread AlarmThread = new AlarmThread(newAlarmList, bsData);
	            AlarmThread.start();
            } else {
            	if (bsData.getStatusQueryType().equals("AlarmSearchPSet")) {
            		// 节目相关报警
            		
            		getString = alarmSearchPSetParse.createForUpXML(bsData, newAlarmList, 0);
            	} else if (bsData.getStatusQueryType().equals("AlarmSearchFSet")) {
            		// 频率相关报警
            		getString = alarmSearchFSetParse.createForUpXML(bsData, newAlarmList, 0);
            	}
                try {
                	// 关闭报警日志信息
                	//log.info("报警上报信息:\n" + xmlUtil.replaceAlarmXMLMsgHeader(getString, bsData));
                	//log.info("上报信息数据为："+getString);
                    xmlUtil.SendUpXML(xmlUtil.replaceAlarmXMLMsgHeader(getString, bsData, isManualRecord), bsData);
                } catch (Exception e) {
                    log.error("报警节目门限或开关上报信息失败: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
        	//ex.printStackTrace();
            log.error("处理报警数据出错: " + ex.getMessage());
            log.error("报警错误原始XML: " + getString);
        }
    }
}
