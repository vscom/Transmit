<%@ page language="java" import="java.util.*,com.bvcom.transmit.util.*"
	pageEncoding="gbk"%>
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
		<meta http-equiv="Content-Type" content="text/html; charset=gbk2312" />
		<title>Transmit 通道映射配置信息</title>

	</head>

	<body>
		<FORM method="POST" name="form1" action="./servlet/DatabaseInit">
		    Transmit 通道映射配置信息相关操作：出厂设置、添加通道映射，删除通道映射<br/>
			<table border="1">
				<tr>
					<td>
						节目映射操作:
					</td>
					<td>
					<select id="cleardb" name ="cleardb">
						<option value="0">恢复出厂默认值</option>
						<option value="1" selected >添加通道节目映射</option>
						<option value="2" >删除通道节目映射</option>
					</select>
						
					</td>
				</tr>
				
				<tr>
					<td>
						起始通道号:
					</td>
					<td>
						<input type="text" name="StartIndex" size="5" value="1">
					</td>
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
	</body>
</html>
