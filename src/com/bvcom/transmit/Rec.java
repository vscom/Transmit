package com.bvcom.transmit;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.handle.si.EPGQueryHandle;
import com.bvcom.transmit.parse.si.EPGQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;

public class Rec extends HttpServlet {

	private static Logger log = Logger.getLogger(Rec.class.getSimpleName());
	
	/**
	 * Constructor of the object.
	 */
	public Rec() {
		super();
	}

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
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("Only For Test");
		out.println("  </BODY>");
		out.println("</HTML>");
		
//		// EPG信息入库测试
//		EPGInsertDataTest();
		
		out.flush();
		out.close();
	}

	/**
	 * EPG信息入库测试
	 */
	public void EPGInsertDataTest() {
		try {
			String file = "D:\\EPGInfo\\Epg_8M.xml";
			log.info("读取EPG信息 " + file);
			File readFilePath = new File(file);
			String returnStr = CommonUtility.readStringFormFile(readFilePath);
			
			UtilXML utilXML = new UtilXML();
			Document document = null;
			
			try {
				document = utilXML.StringToXML(returnStr);
				EPGQueryParse EPGQueryParse = new EPGQueryParse();
				List EPGList = EPGQueryParse.getReturnObject(document);
				EPGQueryHandle EPGQueryHandle = new EPGQueryHandle();
				EPGQueryHandle.upEPGTable(EPGList);
				log.info("EPG信息入库完成 ");
			} catch (CommonException e1) {
				log.error("EPG XML 解析出错: " + e1.getMessage());
			}
			
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			log.error("EPG XML 解析出错: " + e.getMessage());
		}
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

        response.setContentType("text/html");
        response.setStatus(200);

        InputStreamReader inReader = null;
        String getString = null;

        try {
            inReader = new InputStreamReader(request.getInputStream(),
                    "UTF-8");// 获得链接该类的流

            getString = CommonUtility.readStringFromURL(inReader);

            /**
             * 读取请求信息
             */
        } catch (IOException ex) {
            return;
        } finally {
        	if(inReader != null) {
        		inReader.close();
        	}
        	getString = null;
        }
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
