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
		<title>Transmit ͨ��ӳ��������Ϣ</title>

	</head>

	<body>
		<FORM method="POST" name="form1" action="./servlet/DatabaseInit">
		    Transmit ͨ��ӳ��������Ϣ��ز������������á����ͨ��ӳ�䣬ɾ��ͨ��ӳ��<br/>
			<table border="1">
				<tr>
					<td>
						��Ŀӳ�����:
					</td>
					<td>
					<select id="cleardb" name ="cleardb">
						<option value="0">�ָ�����Ĭ��ֵ</option>
						<option value="1" selected >���ͨ����Ŀӳ��</option>
						<option value="2" >ɾ��ͨ����Ŀӳ��</option>
					</select>
						
					</td>
				</tr>
				
				<tr>
					<td>
						��ʼͨ����:
					</td>
					<td>
						<input type="text" name="StartIndex" size="5" value="1">
					</td>
				</tr>
				<tr>
					<td>
						����ͨ����:
					</td>
					<td>
						<input type="text" name="EndIndex" size="5" value="16">
					</td>
				</tr>
				<tr>
					<td>
						ÿͨ����Ŀ��:
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
