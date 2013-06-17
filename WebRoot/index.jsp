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
    <title>Welcome BVCOM 监管平台 <%=CommonUtility.VERSION%></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    Welcome BVCOM 监管平台 <%=CommonUtility.VERSION%>. <br>
    Java:	80/Work <br>
    IAS: 	8280/Work Check Stati<br>
	TSC: 	8089/Work <br>
	RTVM: 	6701/Work <br>
  </body>
</html>
