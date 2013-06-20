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
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <script type="text/javascript">
	  function a(v){
	  	var a=v+0;
	  	alert(a);
	  }
  </script>
<body style="background-color: #a2fcb7">
<div align="center" >
<br/><br/><br/><br/><br/><br/><br/><br/>
<font color="#FF0000">
<h1>欢迎使用BVCOM集中配置</h1>
<h2>请点击左侧导航进行配置</h2>
<h2>如有疑问及时联系研发人员</h2>
</font>
</div>
</body>
</html>
