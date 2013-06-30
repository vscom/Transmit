<%@ page language="java" import="java.util.*,com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle,com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
SetAutoRecordChannelHandle autoRecordHandel = new SetAutoRecordChannelHandle();
List<SetAutoRecordChannelVO> autoRecordList = autoRecordHandel.GetAllPrograms();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style>
		.bluebuttoncss {
		    font-family: "tahoma", "宋体"; /*www.52css.com*/
		    font-size: 10pt; color: #ffffff;
		    border: 0px #93bee2 solid;
		    border-bottom: #93bee2 1px solid;
		    border-left: #93bee2 1px solid;
		    border-right: #93bee2 1px solid;
		    border-top: #93bee2 1px solid;*/
		    background-image:url(./images/head.bmp);
		    background-color: #ffffff;
		    cursor: hand;
		    font-style: normal ;
		    width:100px;
		    height:25px;
		}
		bodsy {
		    scrollbar-face-color: #ededf3;
		    scrollbar-highlight-color: #ffffff;
		    scrollbar-shadow-color: #93949f;
		    scrollbar-3dlight-color: #ededf3;
		    scrollbar-arrow-color: #082468;
		    scrollbar-track-color: #f7f7f9;
		    scrollbar-darkshadow-color: #ededf3;
		    font-size: 4pt; /*www.52css.com*/
		    color: #003366;
		    overflow:auto;
		}
		.noneinput{
		    text-align:left;
		    width:100%;
		    height:100%;
		    border-top-style: none;
		    border-right-style: none;
		    border-left-style: none;
		    background-color: #e6c6e5;
		    border-bottom-style: none;
		} 
	</style>
  </head>
  <body style="background-color: #a2fcb7">
  	<form method="post" action="servlet/TransmitConfig">
  		<table width="100%">
	  		<tr align="center"><td><h4 ><font color="#4035e7">节目信息表</font></h4></td></tr>
	  	</table>
	    <table border=1 cellspacing=1 width="100%" id="SMG">
	    	<THEAD> 
		    	<tr>
				<td>设备通道号</td>
				<td>内部通道号</td>
				<td>频点</td>
				<td>QAM</td>
				<td>符号率</td>
				<td>ServiceID</td>
				<td>ProgramName</td>
				<td>VideoPID</td>
				<td>AudioPID</td>
				<td>RecordType</td>
				<td>HDFlg</td>
				<td>UDP</td>
				<td>Port</td>
				<td>SMGURL</td>
				<td>IPMIndex</td>
				<td>TscIndex</td>
		    	</tr>
	    	</THEAD>
	    	<tbody id="newsSMGTB"> 
		    	<%
 		    		if (autoRecordList != null) {
 		    			for (int i = 0; i < autoRecordList.size(); i++) {
 		    				SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO) autoRecordList.get(i);
 		    	%>
		    	<tr>
		    	<td><%= vo.getDevIndex() %></td>
		    	<td><%= vo.getIndex() %></td>
		    	<td><%= vo.getFreq() %></td>
		    	<td><%= vo.getQAM() %></td>
		    	<td><%= vo.getSymbolRate() %></td>
		    	<td><%= vo.getProgramName() %></td>
		    	<td><%= vo.getVideoPID() %></td>
		    	<td><%= vo.getAudioPID() %></td>
		    	<td><%= vo.getRecordType() %></td>
		    	<td><%= vo.getHDFlag() %></td>
		    	<td><%= vo.getUdp() %></td>
		    	<td><%= vo.getPort() %></td>
		    	<td><%= vo.getSmgURL() %></td>
		    	<td><%= vo.getIpmIndex() %></td>
		    	<td><%= vo.getTscIndex() %></td>
		    	</tr>
		    	<%}}%>
			</tbody> 
	    </table>
    </form>    
  </body>
</html>
