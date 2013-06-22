package com.bvcom.transmit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.config.ReadConfigFile;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.video.MosaicStreamRoundInfoStopThread;
import com.bvcom.transmit.handle.video.MosaicStreamRoundInfoStopTimerTask;
import com.bvcom.transmit.task.AutoAnalysisTimeQueryTask;
import com.bvcom.transmit.task.RecordTaskThread;
import com.bvcom.transmit.task.ReplyAlarmErrorTaskThread;
import com.bvcom.transmit.task.StreamRoundInfoQueryReboot;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;


public class Transmit extends HttpServlet {

    private static Logger log = Logger.getLogger(Transmit.class.getSimpleName());
    
    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

    private static void testJDBCConnection() throws Exception {
        
        StringBuffer strBuff = new StringBuffer();
        
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = DaoSupport.getJDBCConnection();
        
        strBuff.append("insert into channelstatus (channelindex, Freq, SymbolRate, qam, ServiceID) values (1, 500000, 6875, 'QAM64', 110)");
        
        log.info(strBuff.toString());
        
        try {
            statement = conn.createStatement();
            
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            throw new DaoException(e);
        } finally {
            DaoSupport.close(rs);
            DaoSupport.close(statement);
            DaoSupport.close(conn);
        }
        log.info("JDBC Test Successful!");
    }
    /**
     * The doGet method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.println("<title> BVCOM " + CommonUtility.VERSION + "   </title>");
        out.println("</HEAD>  <BODY>");
        out.print("Welcome Home, using the POST method. PowerBy: BVCOM <br />");
        out.print(" VERSION: " + CommonUtility.VERSION + "<br />");
        out.println("<br /> ------------------------------  ");
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
     * This method is called when a form has its tag value method equals to post.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.info("Transmit转发开始");
        InputStreamReader inReader;
        String getString = null;
        Document requestDoc = null;
        
        MSGHeadVO bsData = new MSGHeadVO();
        UtilXML xmlUtil = new UtilXML();
        
        try {
            try {
                inReader = new InputStreamReader(request.getInputStream(),
                        "GB2312");// 获得链接该类的流

                getString = CommonUtility.readStringFromURL(inReader);

                /**
                 * 读取请求信息
                 */
                inReader.close();
            } catch (IOException ex) {
                log.error("取得数据出错: " + ex.getMessage());
                ex.printStackTrace();
                return;
            }
            log.info("请求信息:\n" + getString);
    
            requestDoc = xmlUtil.StringToXML(getString);
            
            requestDoc.setXMLEncoding("GB2312");
            
            // 从下发的Xml数据中提取头部信息
            boolean retFlg = xmlUtil.getInfoFromDownXml(requestDoc, bsData);
            
            try {
                OutputStreamWriter ResponseWriter = null;
                response.setContentType("text/html");
                ResponseWriter = new OutputStreamWriter(response.getOutputStream(),
                        "GB2312"); //向客户端返回确认信息 
                if (!retFlg) {
                    ResponseWriter.write("Data Error !");
                    ResponseWriter.flush();
                    ResponseWriter.close();
                    return;
                } else {
                    ResponseWriter.write("OK");
                    ResponseWriter.flush();
                    ResponseWriter.close();
                }
            } catch (Exception ex) {
                //log.error("回复数据出错: " + ex.getMessage());
            }

            TransmitThread transmitThread = new TransmitThread(getString, bsData);
            transmitThread.start();
            
        } catch (Exception ex) {
            log.error("启动业务处理线程出错");
        }
        
    }

    /**
     * Returns information about the servlet, such as 
     * author, version, and copyright. 
     *
     * @return String information about this servlet
     */
    public String getServletInfo() {
        return "This is my default servlet created by Eclipse";
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occure
     */
    public void init() throws ServletException {
        // 初始化配置文件信息
        ReadConfigFile configFile = new ReadConfigFile();
        configFile.initConfig();
        log.info("配置文件信息初始化完成");
        
        // 任务录像处理, 删除已经过期的任务录像
        RecordTaskThread RecordTaskProcess = new RecordTaskThread();
        RecordTaskProcess.start();
        
        //当前端发送给平台的信息不通时，需要入库，然后启动补报机制
        ReplyAlarmErrorTaskThread replyAlarmErrorTaskThread = new ReplyAlarmErrorTaskThread();
        replyAlarmErrorTaskThread.start();
        
        //数据分析
        AutoAnalysisTimeQueryTask autoAnalysisTimeQueryTask = new AutoAnalysisTimeQueryTask();
        autoAnalysisTimeQueryTask.start();
        
        //TODO 轮播复位标记  Ji Long
        //StreamRoundInfoQueryReboot streamRoundInfoQueryReboot =new StreamRoundInfoQueryReboot();
        //streamRoundInfoQueryReboot.start();
        
        
        
        //启动马赛克轮播 停止线程
        AutoAnalysisTimeQueryConfigFile autoAnalysisTimeOueryConfigFile=new AutoAnalysisTimeQueryConfigFile();
        String StopTime = autoAnalysisTimeOueryConfigFile.getStreamRoundInfoQueryStopTime();
        
        try {
		String[] strStartDate = StopTime.split(" ");
		String[] sStartDate = strStartDate[0].split("-");
		String[] sEndDate = strStartDate[1].split(":");
		String[] strDate =  new String[6];
		
		//YYYY-MM-DD
		strDate[0]=sStartDate[0];
		strDate[1]=sStartDate[1];
		strDate[2]=sStartDate[2];
		
		//HH:MM:SS
		strDate[3]=sEndDate[0];
		strDate[4]=sEndDate[1];
		strDate[5]=sEndDate[2];
		
		Date stopDate = new Date(Integer.parseInt(strDate[0]) - 1900, Integer.parseInt(strDate[1]) - 1, Integer.parseInt(strDate[2]), Integer.parseInt(strDate[3]), Integer.parseInt(strDate[4]), Integer.parseInt(strDate[5]));
		//若时间过期，删除马赛克轮播，那么比对，启动新业务，否则不启动定时器
		if (stopDate.before(new Date())) {
			Timer stopTimer=new Timer();
	        //stopTimer.schedule(new MosaicStreamRoundInfoStopTimerTask(), stopDate);
		}
        } catch (Exception ex) {
        	// 在第一次初始化的时候可能取不到时间信息
        }
		
		
		
        //新频道报警
		//AlarmNewChannel alarmNewChannel = new AlarmNewChannel();
		//alarmNewChannel.start();
    }
    
    

}
