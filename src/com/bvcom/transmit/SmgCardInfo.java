package com.bvcom.transmit;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

public class SmgCardInfo extends HttpServlet {

	   private static Logger log = Logger.getLogger(SmgCardInfo.class.getSimpleName());
	   
	/**
	 * Constructor of the object.
	 */
	public SmgCardInfo() {
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

		doPost(request, response);
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
	//待优化
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int smgIndex=1;
		String smgIp="";
		int smgInputtype=0;
		String smgURL="";
		int smgStatus=0;
		String smgSmartCard="";
		String smgCamCard="";
		String smgCamPostion="";
		String smgCamDesc="";
		String smgRemark="";
		
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html");
		request.setCharacterEncoding("GBK");
		
		String flag =request.getParameter("type");
		//String showparam = request.getParameter("showparam");
		
		if(flag==null)//页面刷新
		{
			//System.out.println("type="+flag);
		}
		else 
		{
		    //取参数信息入库
		    try {
			  // 开始通道号
		    	try
		    	{
			      //System.out.println(request.getParameter("smgIndex"));
				  smgIndex = Integer.parseInt(request.getParameter("smgIndex").trim());
				  smgIp	= request.getParameter("smgIp").toString().trim();
				  smgInputtype=Integer.parseInt(request.getParameter("smgInputtype").trim());
				  smgURL= request.getParameter("smgURL").toString().trim();
				  smgStatus = Integer.parseInt(request.getParameter("smgStatus").trim());
				  
				  smgSmartCard= request.getParameter("smgSmartCard").toString().trim();
				  smgCamCard= request.getParameter("smgCamCard").toString().trim();
				  smgCamPostion= request.getParameter("smgCamPostion").toString().trim();
				  smgCamDesc= request.getParameter("smgCamDesc").toString().trim();
				  smgRemark= request.getParameter("smgRemark").toString().trim();
				  System.out.println(smgRemark);
		    	}
		    	catch(Exception ex){
		    		
		    	}
			
				Statement statement = null;
				ResultSet rs = null;
				Connection conn = DaoSupport.getJDBCConnection();

				StringBuffer strBuff1 = new StringBuffer();
				//strBuff1.append("select count(*) from smg_card_info where smgIndex="+smgIndex+" and smgInputtype="+smgInputtype);
				strBuff1.append("select count(*) from smg_card_info where smgIndex="+smgIndex);
				int count =0;
				try {
					statement = conn.createStatement();
					rs = statement.executeQuery(strBuff1.toString());
					while(rs.next()){
						count =rs.getInt(1);
						}
				} catch (Exception e) {
				} finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
				}
				if(count>0){
					StringBuffer strBuff2 = new StringBuffer();
					strBuff2.append("update smg_card_info set smgIp='");
					strBuff2.append(smgIp+"',smgInputtype=");
					strBuff2.append(smgInputtype+",smgURL='");
					strBuff2.append(smgURL+"',updateTime='");
					strBuff2.append(CommonUtility.getDateTime()+"',smgStatus=");
					strBuff2.append(smgStatus+",smgSmartCard='");
					strBuff2.append(smgSmartCard+"',smgCamCard='");
					strBuff2.append(smgCamCard+"',smgCamPostion='");
					strBuff2.append(smgCamPostion+"',smgCamDesc='");
					strBuff2.append(smgCamDesc+"',smgRemark='");
					strBuff2.append(smgRemark+"' where smgIndex=");
					strBuff2.append(smgIndex);
					conn.setAutoCommit(false);
					System.out.println(strBuff2.toString());
					//="update smg_card_info set IsSuccess = 0 where id = "+alarmIdList.get(i)+";";
					try {
						statement=conn.createStatement();
						statement.executeUpdate(strBuff2.toString());
						
					} catch (Exception e) {
						//log.info("更新通道状态表失败："+e.getMessage());
					}finally{
						DaoSupport.close(statement);
					}
					conn.commit();
					
					
				}
				else
				{
					StringBuffer strBuff = new StringBuffer();
					//数据库中是否存在此通道，若存在、UPDATE,否则INSERT
					strBuff.append("insert into smg_card_info(smgIndex, smgIp, smgInputtype, smgURL, updateTime,smgStatus, smgSmartCard, smgCamCard," +
							" smgCamPostion, smgCamDesc,smgRemark)");
					strBuff.append(" values(");
					strBuff.append(smgIndex + ", ");
					strBuff.append("'" + smgIp + "', ");
					strBuff.append(smgInputtype + ", ");
					strBuff.append("'" + smgURL + "', ");
					strBuff.append("'" + CommonUtility.getDateTime() + "', ");
					strBuff.append(smgStatus + ", ");
					strBuff.append("'" + smgSmartCard + "', ");
					strBuff.append("'" + smgCamCard + "', ");
					strBuff.append("'" + smgCamPostion + "', ");
					strBuff.append("'" + smgCamDesc + "', ");
					strBuff.append("'" + smgRemark + "')");
					System.out.println(strBuff.toString());
					try {
						statement = conn.createStatement();
						statement.executeUpdate(strBuff.toString());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					} finally {
						DaoSupport.close(statement);
					}
					strBuff = null;
				}
				
				DaoSupport.close(conn);
	        }catch(Exception ex) {
	        	System.out.println(ex.getMessage());
	        	log.error("任务录像查询数据库错误: " + ex.getMessage());
	        }
	        //更新一对一视频表:手动选台、自动轮播（马赛克轮播）
	        SMGCardInfoVO smgCardInfo = new SMGCardInfoVO();
			smgCardInfo.setIndex(smgIndex);
			smgCardInfo.setURL(smgURL);
			if(smgInputtype==1){
				 try {
					upChangeProgramTable(smgCardInfo,3);
				} catch (DaoException e) {
					e.printStackTrace();
				}
			}else if(smgInputtype==2){
				 try {
						upChangeProgramTable(smgCardInfo,4);
						//upChangeProgramTable(smgCardInfo,5);
					} catch (DaoException e) {
						e.printStackTrace();
					}
			}else if(smgInputtype==0){
				try {
					delChannelFromChannelMapping(smgCardInfo.getIndex());
				} catch (DaoException e) {
					e.printStackTrace();
				}
			}else{
				try {
					recoverChannelFromChannelMapping(smgCardInfo.getIndex());
				} catch (DaoException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\" />");
	    out.println("  <HEAD><TITLE>System Status</TITLE>");
	    
	    
	    out.println("<script type=\"text/javascript\">");

	    out.println("function showInfo(index)");
		out.println("{");
		//out.println("alert(index);");
		//out.println("document.getElementById(\"smgIndex\").value=dfl;");
		out.println("document.getElementById(\"smgIndex\").value=document.getElementById(\"smgIndex_\"+index).value;");
		out.println("document.getElementById(\"smgIp\").value=document.getElementById(\"smgIp_\"+index).value;");
		
		//out.println("document.getElementById(\"smgInputtype\").value=document.getElementById(\"smgInputtype_hidden_\"+index).value;");
		//out.println("alert(document.getElementById(\"smgInputtype_hidden_\"+index).value);");
		out.println("document.getElementById(\"smgURL\").value=document.getElementById(\"smgURL_\"+index).value;");
		out.println("document.getElementById(\"smgSmartCard\").value=document.getElementById(\"smgSmartCard_\"+index).value;");
		out.println("document.getElementById(\"smgCamCard\").value=document.getElementById(\"smgCamCard_\"+index).value;");
		out.println("document.getElementById(\"smgCamPostion\").value=document.getElementById(\"smgCamPostion_\"+index).value;");
		out.println("document.getElementById(\"smgCamDesc\").value=document.getElementById(\"smgCamDesc_\"+index).value;");
		out.println("document.getElementById(\"smgRemark\").value=document.getElementById(\"smgRemark_\"+index).value;");

		//out.println("this.form1.action=\"./smginfo?type=\"+index;");
		//out.println("this.form1.submit();");
		//out.println("alert(index);");
		out.println("}");
		
	    out.println("function delInfo(index)");
		out.println("{");
		out.println("}");
	    out.println("</script>");
	   
	    
	    out.println("  </HEAD>");
		out.println("  <BODY>");
		out.println("<h3>设置前端板卡通道信息：</h3>");
		out.println("  <form id=\"form1\" method=\"POST\" name=\"form1\" action=\"./smginfo?type=500\">");
		out.println("<table border=1 cellspacing=1 >");
		out.println("<tr>");
		//out.println("<td width=3%> 序号 </td>");
		out.println("<td width=4%> 通道</td>");
		out.println("<td width=8%> 板卡IP </td>");
		out.println("<td width=4%> 业务类型 </td>");	
		out.println("<td width=20%> 板卡URL </td>");	
		out.println("<td width=3%> 当前状态 </td>");
		out.println("<td width=10%> CAM卡号 </td>");
		out.println("<td width=10%> 智能卡号 </td>");
		out.println("<td width=10%> 小卡位置</td>");
		out.println("<td width=10%>小卡描述</td>");
		out.println("<td width=10%> 备注信息 </td>");
		out.println("</tr>");
		out.println("<td width=4%> <input type =\"text\" size=\"5\" id=\"smgIndex\" name=\"smgIndex\" value =\"\" </td>");
		out.println("<td width=8%> <input type =\"text\" size=\"15\" id=\"smgIp\" name=\"smgIp\" value =\"\"</td>");
		out.println("<td width=4%> <select id=\"smgInputtype\" name=\"smgInputtype\" > " +
				"<option value=\"0\">停用</option>" +
				"<option value=\"5\">空闲</option>" +
				"<option value=\"4\">录像</option>" +
				"<option value=\"1\">实时视频</option>" +
				"<option value=\"2\">轮播辅助</option>" +
				"<option value=\"3\">轮循测量</option>" +
				"<option value=\"6\">频道扫描</option>" +
				"<option value=\"7\">指标查询</option>" +
				"</select></td>");
		out.println("<td width=20%> <input type =\"text\" size=\"30\" id=\"smgURL\" name=\"smgURL\" value =\"\"</td>");
		out.println("<td width=3%> <select id=\"smgStatus\" name=\"smgStatus\"> " +
				"<option value=\"0\">正常</option>" +
				"<option value=\"1\">故障</option>" +
				"</select></td>");
		
		out.println("<td width=10%> <input type =\"text\" size=\"20\" name=\"smgSmartCard\" value =\"\"</td>");
		out.println("<td width=10%> <input type =\"text\" size=\"20\" name=\"smgCamCard\" value =\"\"</td>");
		out.println("<td width=10%> <input type =\"text\" size=\"20\" name=\"smgCamPostion\" value =\"\"</td>");
		out.println("<td width=10%> <input type =\"text\" size=\"20\" name=\"smgCamDesc\" value =\"\"</td>");
		out.println("<td width=10%> <input type =\"text\" size=\"20\" name=\"smgRemark\" value =\"\"</td>");
		
		out.println("  </table>");
		out.println("<input type=\"submit\" name=\"smg_submit\" value=\"设置\">");
		out.println("<input type=\"reset\" name=\"smg_ret\" value=\"清除\">");
		//out.println("<input type=\"submit\" name=\"smg_submit\" value=\"删除\">");
	
		
		
		//刷新查看板卡状态信息
		out.println("<h3>查看前端板卡通道信息：</h3>");
		out.println("<table border=1 cellspacing=1 >");
		out.println("<tr>");
		//out.println("<td width=4%> 序号 </td>");
		out.println("<td width=3%> 通道</td>");
		out.println("<td width=6%> 板卡IP </td>");
		out.println("<td width=5%> 业务类型 </td>");	
		out.println("<td width=30%> 板卡URL </td>");	
		out.println("<td width=5%> 状态 </td>");
		out.println("<td width=10%> CAM卡号 </td>");
		out.println("<td width=10%> 智能卡号 </td>");
		out.println("<td width=10%> 小卡位置</td>");
		out.println("<td width=10%>小卡描述</td>");
		//out.println("<td width=10%> 备注信息 </td>");
		out.println("</tr>");
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
				int i =0;
				String str="";
				while(rs.next()){
					out.println("<tr onclick=\"showInfo("+(i+1)+");\">");
					//out.println("<td> " + (i+1)+ " </td>");
					str= "<td ><input type=\"text\" size=\"5\" readonly style=\"border:0px\" id =\"smgIndex_"
					+ (i+1)
					+ "\" value=\" "
					+ rs.getInt("smgIndex")
					+ "\" />"
					+ "</td>";
					
					out.println(str);
					//out.println("<td> " + rs.getInt("smgIndex")+ "</td>");
					
					str= "<td ><input type=\"text\" size=\"20\" readonly style=\"border:0px\" id =\"smgIp_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgIp")
						+ "\" />"
						+ "</td>";
					out.println(str);
					//out.println("<td> " + rs.getString("smgIp")+ "</td>");
					
					int inputtype=rs.getInt("smgInputtype");
					switch(inputtype)
					{
					case 0:
						str= "<td ><input type=\"text\" size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "停用["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "停用["+ inputtype+"]</td>");
						out.println(str);
						break;
					case 1:
						str= "<td ><input type=\"text\" size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "实时视频["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "实时视频["+inputtype+ "] </td>");
						
						out.println(str);
						break;
					case 2:
						str= "<td ><input type=\"text\" size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "轮播辅助["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "轮播辅助["+inputtype+ " ]</td>");
						out.println(str);
						
						break;
					case 3:
						str= "<td ><input type=\"text\" size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "轮循测量["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "轮循测量["+inputtype+ "] </td>");
						out.println(str);
						
						break;
					case 4:
						str= "<td ><input type=\"text\" size=\"15\" readonly  style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "录像["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "录像["+inputtype+ "] </td>");
						out.println(str);
						break;
					case 5:
						str= "<td ><input type=\"text\"  size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "空闲["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "空闲["+inputtype+ "] </td>");
						out.println(str);
						
						break;
					case 6:
						str= "<td ><input type=\"text\" size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "频道扫描["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "频道扫、指标查询["+inputtype+ "] </td>");
						out.println(str);
						
						break;	
					case 7:
						str= "<td ><input type=\"text\" size=\"15\" readonly style=\"border:0px\" id =\"smgInputtype_"
							+ (i+1)
							+ "\" value=\" "
							+ "指标查询["+ inputtype+"]"
							+ "\" />"
							+ "</td>";
						//out.println("<td> " + "频道扫、指标查询["+inputtype+ "] </td>");
						out.println(str);
						
						break;	
					}
				
					str= "<td ><input type=\"text\" size=\"40\" readonly style=\"border:0px\" id =\"smgURL_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgURL")
						+ "\" />"
						+ "</td>";
					out.println(str);
					//out.println("<td> " + rs.getString("smgURL")+ "</td>");
					
					int status =rs.getInt("smgStatus");
					if(status==0)
					{
						str= "<td ><input type=\"text\" size=\"5\" readonly  style=\"border:0px\" id =\"smgStatus_"
							+ (i+1)
							+ "\" value=\" "
							+ "正常"
							+ "\" />"
							+ "</td>";
						out.println(str);
						//out.println("<td> " + "正常"+ "</td>");
					}
					else
					{
						str= "<td ><input type=\"text\" size=\"5\" readonly style=\"border:0px\" id =\"smgStatus_"
							+ (i+1)
							+ "\" value=\" "
							+ "故障"
							+ "\" />"
							+ "</td>";
						out.println(str);
						//out.println("<td> " + "故障"+ "</td>");
					}
					
					str= "<td ><input type=\"text\" readonly size=\"15\" style=\"border:0px\" id =\"smgSmartCard_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgSmartCard")
						+ "\" />"
						+ "</td>";
					out.println(str);
					
					//out.println("<td> " + rs.getString("smgSmartCard")+ "</td>");
					
					str= "<td ><input type=\"text\" readonly size=\"20\" style=\"border:0px\" id =\"smgCamCard_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgCamCard")
						+ "\" />"
						+ "</td>";
					out.println(str);
					
					//out.println("<td> " + rs.getString("smgCamCard")+ "</td>");
					str= "<td ><input type=\"text\"  readonly size=\"20\" style=\"border:0px\" id =\"smgCamPostion_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgCamPostion")
						+ "\" />"
						+ "</td>";
					out.println(str);
					
					//out.println("<td> " + rs.getString("smgCamPostion")+ "</td>");
					str= "<td ><input type=\"text\" readonly size=\"20\" style=\"border:0px\" id =\"smgCamDesc_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgCamDesc")
						+ "\" />"
						+ "</td>";
					out.println(str);
					//out.println("<td> " + rs.getString("smgCamDesc")+ "</td>");
					
					str= "<td ><input type=\"hidden\" size=\"20\" readonly style=\"border:0px\" id =\"smgRemark_"
						+ (i+1)
						+ "\" value=\" "
						+ rs.getString("smgRemark")
						+ "\" />"
						+ "</td>";
					out.println(str);
					
					
					str= "<td ><input type=\"hidden\"  readonly style=\"border:0px\" id =\"smgInputtype_hidden_"
						+ (i+1)
						+ "\" value=\" "
						+  inputtype
						+ "\" />"
						+ "</td>";
					//out.println("<td> " + "录像["+inputtype+ "] </td>");
					out.println(str);
					
					//out.println("<td> " + rs.getString("smgRemark")+ "</td>");
					out.println("</tr>");	
					i = i+1;
				}
			}
			catch (Exception e)
			{
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
			
		}
		out.println("  </table>");
		out.println("  </form>");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
		
		
		//获取通道的状态信息
		doGetNvrStatus();
	}
	
	
	
	@SuppressWarnings("unused")
	private static void recoverChannelFromChannelMapping(int index) throws DaoException{
    	 StringBuffer strBuff = new StringBuffer();
         Statement statement = null;
         ResultSet rs = null;
         Connection conn = DaoSupport.getJDBCConnection();
         
 		strBuff.append("update channelremapping c set ");
 		strBuff.append("DelFlag = 0");
 		strBuff.append(" where DevIndex = "+index);
         
        try {
             statement = conn.createStatement();
             
             statement.executeUpdate(strBuff.toString());
             
         } catch (Exception e) {
             log.error("一对一节目表更新数据库错误: " + e.getMessage());
         } finally {
             DaoSupport.close(rs);
             DaoSupport.close(statement);
             DaoSupport.close(conn);
         }
    }
	
	@SuppressWarnings("unused")
	private static void delChannelFromChannelMapping(int index) throws DaoException{
    	 StringBuffer strBuff = new StringBuffer();
         Statement statement = null;
         ResultSet rs = null;
         Connection conn = DaoSupport.getJDBCConnection();
         
 		strBuff.append("update channelremapping c set ");
 		strBuff.append("DelFlag = 1");
 		strBuff.append(" where DevIndex = "+index);
         
        try {
             statement = conn.createStatement();
             
             statement.executeUpdate(strBuff.toString());
             
         } catch (Exception e) {
             log.error("一对一节目表更新数据库错误: " + e.getMessage());
         } finally {
             DaoSupport.close(rs);
             DaoSupport.close(statement);
             DaoSupport.close(conn);
         }
    }
	
	@SuppressWarnings("unchecked")
	public void doGetNvrStatus(){
		MemCoreData coreDate = MemCoreData.getInstance();
        List SMGCardList = coreDate.getSMGCardList();
        SMGCardList.clear();
        //程序重新启动后，要从数据库中读取数据、
        //判断SMG_CARD_INFO表中是否有数据，有则读取数据库信息，否则读取TransmitConfig.xml配置文件
        int count =0;
        try
        {
	    	Statement statement = null;
			ResultSet rs = null;
			Connection conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff1 = new StringBuffer();
			//strBuff1.append("select count(*) from smg_card_info where smgIndex="+smgIndex+" and smgInputtype="+smgInputtype);
			strBuff1.append("select count(*) from smg_card_info ");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff1.toString());
				while(rs.next()){
					count =rs.getInt(1);
					}
			} catch (Exception e) {
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
        }catch(Exception ex){
        	
        }
        
        if(count>0){
        	try
        	{
	        	Statement statement = null;
				Connection conn = DaoSupport.getJDBCConnection();
				ResultSet rs = null;
				StringBuffer strBuff = new StringBuffer();
				strBuff.append("select *  from smg_card_info order by smgIndex ");
				try {
					statement = conn.createStatement();
					rs = statement.executeQuery(strBuff.toString());
					while(rs.next()){
						 SMGCardInfoVO smgCardInfo = new SMGCardInfoVO();
						 //获取通道号
						 smgCardInfo.setIndex(rs.getInt("smgIndex"));
						 //SMGURL
						 smgCardInfo.setURL(rs.getString("smgURL"));
						 //HDFLAG
						 smgCardInfo.setHDFlag(0);
						 //HDURL
						 smgCardInfo.setHDURL("http://192.168.0.100/Setup1");
						 
						 //通道业务类型
						 @SuppressWarnings("unused")
						 int inputtype=rs.getInt("smgInputtype");
						 switch(inputtype){
						 case 0://停用
							   smgCardInfo.setIndexType("Stop");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 1://实时视频
							   smgCardInfo.setIndexType("ChangeProgramQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 2://轮播辅助
							   smgCardInfo.setIndexType("StreamRoundInfoQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 3://轮循测量
							   smgCardInfo.setIndexType("AutoAnalysisTimeQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 4://录像
							   smgCardInfo.setIndexType("AutoRecord");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 5://空闲
							   smgCardInfo.setIndexType("Free");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 6://频道扫、指标查询
							   smgCardInfo.setIndexType("ChannelScanQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 7://指标查询
							  smgCardInfo.setIndexType("GetIndexSet");
							  SMGCardList.add(smgCardInfo);
							  break;
						 }
						 
						 //=================频道扫描和指标查询占用一个通道=============
//						 if(inputtype==6){
//							 SMGCardInfoVO smgCardInfo1 = new SMGCardInfoVO();
//							 //获取通道号
//							 smgCardInfo1.setIndex(rs.getInt("smgIndex"));
//							 //SMGURL
//							 smgCardInfo1.setURL(rs.getString("smgURL"));
//							 //HDFLAG
//							 smgCardInfo1.setHDFlag(0);
//							 //HDURL
//							 smgCardInfo1.setHDURL("http://192.168.0.100/Setup1");
//							 smgCardInfo1.setIndexType("GetIndexSet");
//							 SMGCardList.add(smgCardInfo1);
//						 }
					}
				}
				catch(Exception ex){
					
				}
				finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
				}
				strBuff = null;
				DaoSupport.close(conn);
        	}catch(Exception e){
        		
        	}
        }
        coreDate.setSMGCardList(SMGCardList);
	}

	
	 /**
     * 更新入库一对一表
     * @throws DaoException 
     */
    @SuppressWarnings("unused")
	private static void upChangeProgramTable(SMGCardInfoVO vo,int type) throws DaoException {
        StringBuffer strBuff = new StringBuffer();
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = DaoSupport.getJDBCConnection();
        
		strBuff.append("update monitorprogramquery c set ");
		// statusFlag: 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播,5:马赛克轮播
		strBuff.append("statusFlag =");
		strBuff.append(type+",");
		// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
		strBuff.append(" RunType = 1, ");
		strBuff.append(" smgURL = '"+vo.getURL()+"'");
		//add smgindex to db 
		strBuff.append(",smgIndex= "+vo.getIndex());
		strBuff.append(" ,lastDatatime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where statusFlag =");
		strBuff.append(type);
        
        try {
            statement = conn.createStatement();
            
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("手动选台更新数据库错误: " + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            DaoSupport.close(rs);
            DaoSupport.close(statement);
            DaoSupport.close(conn);
        }
        //log.info("手动选台更新数据库成功!");
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
