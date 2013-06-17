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
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class SystemStatus extends HttpServlet {

	private static Logger log = Logger.getLogger(SystemStatus.class.getSimpleName());
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("GBK");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\" />");
        out.println("  <HEAD><TITLE>System Status</TITLE></HEAD>");
        out.println("<STYLE type=\"text/css\">");
        out.println("TABLE  { border: solid black;}");
		out.println("TR.top { background: red; height: 20px; }");
		out.println("</STYLE>");
        out.println("  <BODY>");
        out.println("    Java:	80/Work <br>");
        out.println("    IAS: 	8280/Work Check Stati<br>");
        out.println("    TSC: 	8089/Work <br>");
        out.println("	 RTVM: 	6701/Work <br>");
		try {
			List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = selectChannelRemappingInfo(0);
			out.println("<h3>¼��״̬</h3>");
			out.println("<table border=1 cellspacing=1 >");
			
			out.println("<tr>");
			out.println("<td width=3%> ��� </td>");
			out.println("<td width=9%> �忨ͨ����</td>");
			out.println("<td width=9%> ��Ŀ��� </td>");
			out.println("<td width=6%> TscIndex </td>");	
			out.println("<td> ��Ŀ���� </td>");	
			out.println("<td width=5%> Freq </td>");
			out.println("<td width=7%> ServiceID </td>");
			out.println("<td width=7%> ¼������ </td>");
			out.println("<td width=5%> HDFlag </td>");
			out.println("<td> UDP </td>");
			out.println("<td> �忨��ַ </td>");
			out.println("</tr>");
			int devIndex = 0;
			for(int i=0; i<AutoRecordlistSMGNew.size(); i++) {
				SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlistSMGNew.get(i);
				
				if(devIndex != 0 && vo.getDevIndex() != devIndex) {
					out.println("<tr style=\"height: 10px;\"><td></td></tr>");
				}
				
				out.println("<tr>");
				out.println("<td> " + (i+1)+ " </td>");
				out.println("<td> " + vo.getDevIndex()+ " </td>");
				out.println("<td> " + vo.getIndex()+ " </td>");
				out.println("<td> " + vo.getTscIndex()+ " </td>");
				out.println("<td> " + vo.getProgramName()+ "</td>");
				out.println("<td> " + vo.getFreq()+ "</td>");
				out.println("<td> " + vo.getServiceID()+ " </td>");
				
				out.println("<td> ");
				if (vo.getRecordType() == 1) {
					out.println("��̬¼��");
				} else if(vo.getRecordType() == 2) {
					out.println("�Զ�¼��");
				}else if(vo.getRecordType() == 3) {
					out.println("����¼��");
				}else if(vo.getRecordType() == 4) {
					out.println("�������ֲ�");
				}
				
				out.println(" </td>");
				
				if(vo.getHDFlag() == 1) {
					out.println("<td> ���� </td>");
				} else {
					out.println("<td> ���� </td>");
				}
				
				out.println("<td> " + vo.getUdp()+ ":" + vo.getPort() + "</td>");
				out.println("<td> " + vo.getSmgURL()+ "</td>");
				out.println("</tr>");
				
				devIndex = vo.getDevIndex();
			}
			out.println("  </table>");
			List<MonitorProgramQueryVO>  monitorProgramQueryList = new ArrayList<MonitorProgramQueryVO>();
			
			monitorProgramQueryList=MonitorProgramQueryHandle.GetChangeProgramInfoList(monitorProgramQueryList);
			
			
			//TODO 1.������Ƶ���״̬
			out.println("<h3>��Ƶ״̬</h3>");
			out.println("<table border=1 cellspacing=1 >");
			out.println("<tr>");
			out.println("<td width=3%> ��� </td>");
			out.println("<td width=9%> �忨ͨ���� </td>");
			out.println("<td width=12%> ��Ƶ���� </td>");
			out.println("<td> UDP </td>");
			out.println("<td> RtvsURL </td>");	
			out.println("<td> smgURL </td>");
			out.println("</tr>");
			//0:���� 1:һ��һ���Ӷ໭�� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��(������) 6: һ��һ�����ֶ�ѡ̨
			
			for(int i=0;i<monitorProgramQueryList.size();i++){
				MonitorProgramQueryVO mo=monitorProgramQueryList.get(i);
				if(mo.getStatusFlag()==2||mo.getStatusFlag()==3||mo.getStatusFlag()==4||mo.getStatusFlag()==5){
					out.println("<tr>");
					out.println("<td>"+(i+1)+"</td>");
					out.println("<td>"+mo.getSmgIndex()+"</td>");
					if(mo.getStatusFlag()==2){
						out.println("<td>�ֲ����</td>");
					}else if(mo.getStatusFlag()==3){
						out.println("<td>�ֶ�ѡ̨</td>");
					}else if(mo.getStatusFlag()==4){
						out.println("<td>�Զ��ֲ�</td>");
					}else if(mo.getStatusFlag()==5){
						out.println("<td>�����˺ϳ�</td>");
					}
					out.println("<td>"+mo.getRtvsIP()+":"+mo.getRtvsPort()+"</td>");
					out.println("<td>"+mo.getRTVSResetURL()+"</td>");
					out.println("<td>"+mo.getSmgURL()+"</td>");
					out.println("</tr>");
				}
			}
			
			out.println("  </table>");
		} catch (DaoException e) {
			e.printStackTrace();
		}
		
        out.println("  </BODY>");
        out.println("</HTML>");
		
		out.flush();
		out.close();
	}
	
	public static List<SetAutoRecordChannelVO> selectChannelRemappingInfo(int isDataInit) throws DaoException {


		List<SetAutoRecordChannelVO> voList = new ArrayList();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
			StringBuffer strBuff = new StringBuffer();
			// ȡ����ؽ�ĿƵ����Ϣ
			strBuff.append("select *  from channelremapping  ");
			
			if(isDataInit == 0) {
				strBuff.append(" where StatusFlag != 0 ");
			}
			
			strBuff.append(" order by channelindex");

			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				//System.out.println(strBuff.toString());
				
				while(rs.next()){
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					//if(freq!=634000)
					//{
						if (isDataInit == 0) {
							if (freq == 0 || serverID == 0) {
								continue;
							}
						}
						
						SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
						// channelprogramstatus(channelindex, freq, symbolrate, qam, serviceID, videoPID, audioPID, lasttime, HDFlag) 
						vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
						vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
						vo.setFreq(freq);
						try {
							vo.setSymbolRate(Integer.parseInt(rs
									.getString("symbolrate")));
						} catch (Exception ex) {
						}
						try {
							vo.setQAM(Integer.parseInt(rs.getString("qam")));
						} catch (Exception ex) {
						}
						vo.setServiceID(serverID);
						try {
							vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
						} catch (Exception ex) {
						}
						try {
							vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
						} catch (Exception ex) {
						}
						try {
							vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
						} catch (Exception ex) {
						}
						vo.setUdp(rs.getString("udp"));
		
						try {
							vo.setHDFlag(Integer.parseInt(rs.getString("HDFlag")));
						} catch (Exception ex) {
						}
						try {
							vo.setPort(Integer.parseInt(rs.getString("port")));
						} catch (Exception ex) {
						}
						vo.setSmgURL(rs.getString("smgURL"));
		
						try {
							vo.setRecordType(Integer.parseInt(rs
									.getString("RecordType")));
						} catch (Exception ex) {
						}
	
						vo.setProgramName(rs.getString("ProgramName"));
						voList.add(vo);
					}
				//}
				
			} catch (Exception e) {
				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
				log.error("������ϢSQL: " + strBuff.toString());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
		
			}
			strBuff = null;

		DaoSupport.close(conn);
		
		return voList;
	}

}
