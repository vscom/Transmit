<%@ page language="java" import="java.util.*, com.bvcom.transmit.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> 
    <title>监管平台集中配置向导<%=CommonUtility.VERSION%></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
<frameset cols="18%,82%">
   <frame src="./selectFrame.jsp" name="selectFrame" id="selectFrame">
   <frame src="./mainFrame.jsp" name="mainFrame" id="mainFrame">
</frameset>
</html>
