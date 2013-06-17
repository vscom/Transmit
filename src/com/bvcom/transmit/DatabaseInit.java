package com.bvcom.transmit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

public class DatabaseInit extends HttpServlet {

	private static Logger log = Logger.getLogger(DatabaseInit.class.getSimpleName());
	
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

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out
                .println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>Database Init</TITLE></HEAD>");
        out.println("  <BODY>");

        //request.setCharacterEncoding("UTF-8");
        //response.setCharacterEncoding("GBK");
        //request.setCharacterEncoding("GBK");
        
        response.setCharacterEncoding("GBK");
		response.setContentType("text/html");
		request.setCharacterEncoding("GBK");
		
        int startIndex = 0;
        int endIndex = 0;
        int programNum = 0;
        
        int clearflag =1;//�������Ŀӳ�����Ϣ
        
        try{
        	clearflag = Integer.parseInt(request.getParameter("cleardb"));
        }catch(Exception ex)
        {
        	
        }
        
//        int indexNum = Integer.parseInt(request.getParameter("IndexNum"));
        try {
        // ��ʼͨ����
        	startIndex = Integer.parseInt(request.getParameter("StartIndex"));
        }catch(Exception ex) {
        	
        }
        try {
        // ����ͨ����
        	endIndex = Integer.parseInt(request.getParameter("EndIndex"));
        }catch(Exception ex) {
        	
        }
        try {
        	programNum = Integer.parseInt(request.getParameter("ProgramNum"));
		}catch(Exception ex) {
			
		}
        out.println("<br> StartIndex: " + startIndex);
        out.println("<br> EndIndex: " + endIndex);
        out.println("<br> ProgramNum: " + programNum);
        
        try {
        	//���ԭʼ��Ŀӳ���
        	if(clearflag ==0)
        	{
        		out.println("<br>recover channel from ["+startIndex+"-----"+endIndex+" ]from channelremapping");
        		delChannelIndex();
        		//�����Ŀӳ���
				initChannelRemappingIndex(startIndex, endIndex + 1, programNum);
        	}
        	if(clearflag ==1)
        	{
        		out.println("<br>insert into channelremapping from ["+startIndex+"-----"+endIndex+"] program info");
        		//�����Ŀӳ���
				initChannelRemappingIndex(startIndex, endIndex + 1, programNum);
        	}
        	//ɾ��ĳͨ����Ŀӳ��
        	if(clearflag==2 && startIndex==endIndex){
        		out.println("<br>delete channel-["+startIndex+"] from channelremapping");
        		delChannelIndex(startIndex);
        	}
        	else{
        		if(clearflag==2){
        			out.println("<br>Attention!!! first channel must equal  last channel");
        		}
        	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
        
		
		try {
			List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = SystemStatus.selectChannelRemappingInfo(1);
			
			out.println("<FORM method=\"POST\" name=\"form1\" action=\"./servlet/controlAlarm\">");
			
			out.println("<table border=1 cellspacing=1 >");
			
			out.println("<tr>");
			out.println("<td>DevIndex</td>");
			out.println("<td>ChannelIndex</td>");	
			out.println("<td>Freq</td>");
			out.println("<td>ServiceID</td>");
			out.println("<td>HDFlag</td>");
			out.println("</tr>");	
			for(int i=0; i<AutoRecordlistSMGNew.size(); i++) {
				SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlistSMGNew.get(i);
				
				out.println("<tr>");
				out.println("<td> " + vo.getDevIndex()+ " </td>");
				out.println("<td> " + vo.getIndex()+ " </td>");
				out.println("<td> " + vo.getFreq()+ "</td>");
				out.println("<td> " + vo.getServiceID()+ " </td>");
				out.println("<td> " + vo.getHDFlag()+ "</td>");
				out.println("</tr>");	
			}

			out.println("  </table>");
			out.println("  </FORM>");
		} catch (DaoException e) {
			e.printStackTrace();
		}
		
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

	
    private static void initChannelRemappingIndex(int startIndex, int endIndex, int programNum) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime, LastFlag)
		
		// SELECT max(channelindex) FROM channelremapping c;
		
		int channelNum = getMaxChannelIndex() + 1;
		
		if(startIndex == 2 && channelNum == 1) {
			channelNum = 2;
		}
		
		StringBuffer strBuff = null;
		try {
			for(int i=startIndex; i<endIndex; i++) {
				
				for(int j=0; j<programNum; j++) {
					
					strBuff = new StringBuffer();
					strBuff.append("insert into transmit.channelremapping(DevIndex, channelindex) values (");
					strBuff.append(i + ", " + (channelNum) + ")");
					
					log.info("indexNum: " + (channelNum) + "\t devNum: " + (i));
					
					try {
						statement = conn.createStatement();
						statement.execute(strBuff.toString());
					} catch (Exception ex) {
						log.error("�Զ�¼�� ����ͨ��ӳ������: " + ex.getMessage());
						log.error("����SQL: " + strBuff.toString());
					}
					channelNum++;
					strBuff = null;
				}
			}
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
			log.error("����SQL: " + strBuff.toString());
			strBuff = null;
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
	}
    
    /*
     * ��ʼ�����ݿ�ǰҪ��������ݿ�ԭʼ��Ϣ
     * AUTHOR:TQY
     * DATE:2012-04-05
     */
    private static boolean delChannelIndex() throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		boolean ret = false;
		StringBuffer strBuff = new StringBuffer();
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("delete from channelremapping ");

		try {
			statement = conn.createStatement();
			ret =statement.execute(strBuff.toString());
		} catch (Exception e) {
			log.error("ȡ�ý�Ŀ�Ŵ���: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		return ret;
	}
    
  
    private static boolean delChannelIndex(int devIndex) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		boolean ret = false;
		StringBuffer strBuff = new StringBuffer();
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("delete from channelremapping where DevIndex= "+devIndex);

		try {
			statement = conn.createStatement();
			ret =statement.execute(strBuff.toString());
		} catch (Exception e) {
			log.error("ȡ�ý�Ŀ�Ŵ���: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		return ret;
	}
    
    private static int getMaxChannelIndex() throws DaoException {
		int maxChannel = 0;

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;

		StringBuffer strBuff = new StringBuffer();
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("SELECT max(channelindex) FROM channelremapping c");

		try {
			statement = conn.createStatement();

			rs = statement.executeQuery(strBuff.toString());

			while (rs.next()) {
				try {
					maxChannel = Integer.parseInt(rs
							.getString("max(channelindex)"));
				} catch (Exception ex) {
				}
			}

		} catch (Exception e) {
			log.error("ȡ�ý�Ŀ�Ŵ���: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);

		}
		strBuff = null;

		DaoSupport.close(conn);

		return maxChannel;
	}
    
    
    
}
