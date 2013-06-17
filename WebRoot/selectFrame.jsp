<%@ page language="java" import="java.util.*, com.bvcom.transmit.util.*,com.bvcom.transmit.core.MemCoreData,com.bvcom.transmit.vo.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
MemCoreData coreDate=MemCoreData.getInstance();
//List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreDate.getTSCList().size();
	SysInfoVO system=(SysInfoVO)coreDate.getSysVO();
//IPMInfoVO ipm=(IPMInfoVO)coreDate.getIPMList().get(i);
//SMGCardInfoVO smg=(SMGCardInfoVO)coreDate.getSMGCardList().get(i);
//http://ip/tscconfig.aspx
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
  	<style>
		.bluebuttoncss {
		    font-family: "tahoma", "宋体"; /*www.52css.com*/
		    font-size: 10pt; color: #ffffff;
		    border: 0px #93bee2 solid;
		    border-bottom: #93bee2 1px solid;
		    border-left: #93bee2 1px solid;
		    border-right: #93bee2 1px solid;
		    border-top: #93bee2 1px solid;*/
		    background-image:url(./images/select.jpg);
		    background-color: #ffffff;
		    cursor: hand;
		    font-style: normal ;
		    width:150px;
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
		    text-align:center;
		    width:99%;height:99%;
		    border-top-style: none;
		    border-right-style: none;
		    border-left-style: none;
		    background-color: #f6f6f6;
		    border-bottom-style: none;
		}
	</style>
	<script type="text/javascript">
		function openURL(url){
			//alert(window.parent.frames.length);
			//window.parent.frames.length.location.=url;
			window.parent.frames[1].location=url;
			//mainFrame.location=url;
			//$("#Box").attr("src","http://www.alixixi.cn/");
			//$("#selectFrame").attr("src",url);
		}
	</script>
<body style="background-color: #b2ece1">
	<input class="bluebuttoncss" type="button" value="转发(<%=system.getLocalRedirectIp().trim()%>)" onclick="openURL('./TransmitConfig.jsp');"/><br/><br/>
	<!-- <a href="./TransmitConfig.jsp" target="mainFrame" >转发配置</a> -->
	<% 
		for(int i=0;i<coreDate.getIPMList().size();i++){
		IPMInfoVO ipm=(IPMInfoVO)coreDate.getIPMList().get(i);
		String ipmurl=ipm.getSysURL();
		String ipmip=ipmurl.split(":")[1].substring(2,ipmurl.split(":")[1].length());
	%>
	<input class="bluebuttoncss" type="button" value="IPM(<%=ipmip%>)" onclick="openURL('<%=ipm.getSysURL()%>');"/><br/>
	<%
		}
	%>
<br/>
	<% 
		for(int i=0;i<coreDate.getTSCList().size();i++){
		TSCInfoVO tsc=(TSCInfoVO)coreDate.getTSCList().get(i);
		String tscurl=tsc.getSysURL();
		String tscip=tscurl.split(":")[1].substring(2,tscurl.split(":")[1].length());
	%>
	<input class="bluebuttoncss" type="button" value="TSC(<%=tscip%>)" onclick="openURL('<%=tsc.getSysURL()%>');"/><br/>
	<%
		}
	%>
<br/>
	<% 
		for(int i=0;i<coreDate.getSMGCardList().size();i+=2){
		SMGCardInfoVO smg=(SMGCardInfoVO)coreDate.getSMGCardList().get(i);
		String smgurl=smg.getURL();
		String smgip=smgurl.split(":")[1].substring(2,smgurl.split(":")[1].length());
		//http://ip/tscconfig.aspx
		String smgConfigUrl="http://"+smgip+":8080/mac_ci.html";
	%>
	<input class="bluebuttoncss" type="button" value="SMG(<%=smgip%>)" onclick="openURL('<%=smgConfigUrl%>');"/><br/>
	<%
		}
	%>
<br/>
<input class="bluebuttoncss" type="button" value="百度知道" onclick="openURL('http://www.baidu.com/');"/><br/><br/>
<!-- <a href="http://www.baidu.com/" target="mainFrame">百度</a> -->
</body>
</html>
