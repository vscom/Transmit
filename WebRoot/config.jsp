<%@ page language="java" import="java.util.*,com.bvcom.transmit.util.*"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Transmit 视频相关配置</title>

	</head>

	<body>
		<FORM method="POST" name="form1" action="./servlet/VideoConfig">
		    Transmit 视频相关配置<br/>
			<table border="1">
				<tr>
					<td> ID </td>
					<td size="5"> IP </td>
					<td> Port </td>
					<td size="10"> SMGUrl </td>
					<td> SMG 通道号 </td>
					<td> 通道状态 </td>
					<td> RTVS视频管理地址 </td>
					<td> 分组轮询Index </td>
				</tr>
				<tr>
					<td>
						结束通道号:
					</td>
					<td>
						<input type="text" name="EndIndex" size="5" value="16">
					</td>
				</tr>
				<tr>
					<td>
						每通道节目数:
					</td>
					<td>
						<input type="text" name="ProgramNum" size="5" value="6">
					</td>
				</tr>
			</table>
			<input type="submit" name="submit" value="SUBMIT">
		</FORM>
		通道状态标记: 0:空闲 1:一对一监视多画面 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面(马赛克) 6: 一对一监测的手动选台 <br />
		分组轮询Index 只有在"通道状态标记"为2的时候才有效。<br />
	</body>
</html>
