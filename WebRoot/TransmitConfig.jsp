<%@ page language="java" import="java.util.*,com.bvcom.transmit.core.MemCoreData,com.bvcom.transmit.vo.SMGCardInfoVO,com.bvcom.transmit.vo.SysInfoVO,com.bvcom.transmit.vo.IPMInfoVO,com.bvcom.transmit.vo.TSCInfoVO" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//MemCoreData coreDate=(MemCoreData)request.getSession(false).getAttribute("coreDate");
MemCoreData coreDate=MemCoreData.getInstance();
SysInfoVO sysVO = coreDate.getSysVO();
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
	<script language="JavaScript">    
		//var cGetRow=-99999; 
		function insertrow(obj){
			//alert(document.all.SMG.rows.length);
			if(obj=='SMG'){
				var newrow1 = document.all.SMG.rows[1].cloneNode(true); 
				document.all("news"+obj+"TB").appendChild(newrow1);
				var newrow2 = document.all.SMG.rows[2].cloneNode(true);
				document.all("news"+obj+"TB").appendChild(newrow2);
			}
			if(obj=='IPM'){
				var newrow1 = document.all.IPM.rows[1].cloneNode(true); 
				document.all("news"+obj+"TB").appendChild(newrow1);
			}
			if(obj=='TSC'){
				var newrow1 = document.all.TSC.rows[1].cloneNode(true); 
				document.all("news"+obj+"TB").appendChild(newrow1);
			}			
		}
		/* 
		function GetRow(){ 
			//获得行索引 
			//两个parentElement分别是TD和TR，rowIndex是TR的属性 
			//this.parentElement.parentElement.rowIndex 
		    cGetRow=window.event.srcElement.parentElement.parentElement.rowIndex; 
		    DelRow(cGetRow);//点击checkbox时，直接删除行。 
		}
		*/ 
		function DelRow(obj){ 
			//删除一行 
			//if(iIndex==-99999){ 
			//   alert("系统提示：没有选中行号!"); 
			//}else{
			var index;
			if(obj=='SMG'){
				if(document.all.SMG.rows.length-3!=0){
					index=document.all.SMG.rows.length-2;
					newsSMGTB.deleteRow(index);
					index=document.all.SMG.rows.length-2;
					newsSMGTB.deleteRow(index);  
				}else{
					alert('不能删除所有SMG配置！');
				}
			}
			if(obj=='IPM'){
				if(document.all.IPM.rows.length-2!=0){
					index=document.all.IPM.rows.length-2;
					newsIPMTB.deleteRow(index);
				}else{
					alert('不能删除所有IPM配置！');
				} 				
			}
			if(obj=='TSC'){
				if(document.all.TSC.rows.length-2!=0){
					index=document.all.TSC.rows.length-2;
					newsTSCTB.deleteRow(index); 				
				}else{
					alert('不能删除所有TSC配置！');
				}
			}
			//   iIndex==-99999;//将rowIndex恢复默认值。 
			//} 
		} 
	</script>
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
	  		<tr align="center"><td><h4 ><font color="#4035e7">SMG 配置信息</font></h4></td></tr>
	  	</table>
	    <table border=1 cellspacing=1 width="100%" id="SMG">
	    	<THEAD> 
		    	<tr>
		    	<td>通道号</td>
		    	<td>数据接收URL</td>
		    	<td>高清标记</td>
		    	<td>高清硬转码接收URL</td>
		    	<td>通道类型</td>
		    	</tr>
	    	</THEAD>
	    	<tbody id="newsSMGTB"> 
		    	<%
		    	if(coreDate!=null){
		    	for(int i=0;i<coreDate.getSMGCardList().size();i++){
		    	SMGCardInfoVO smg=(SMGCardInfoVO)coreDate.getSMGCardList().get(i);
		    	%>
		    	<tr>
		    	<td><input class="noneinput" name="SMGIndex" type="text" size="3" value="<%=smg.getIndex()%>"/></td>
		    	<td><input class="noneinput" name="SMGURL" type="text" size="27" value="<%=smg.getURL()%>"/></td>
		    	<td><input class="noneinput" name="SMGHDFlag" type="text" size="3" value="<%=smg.getHDFlag()%>"/></td>
		    	<td><input class="noneinput" name="SMGHDURL" type="text" size="30" value="<%=smg.getHDURL()%>"/></td>
		    	<td><input class="noneinput" name="SMGIndexType" type="text" size="15" value="<%=smg.getIndexType()%>"/></td>
		    	</tr>
		    	<%}}%>
			</tbody> 
	    </table>
	    <table width="100%">
	  		<tr align="right"><td>
		    	<input name="AddSMG" class="bluebuttoncss" type="button" value="添加SMG" onClick="insertrow('SMG');"/>
		    	<input name="DelSMG" class="bluebuttoncss" type="button" value="删除SMG" onClick="DelRow('SMG');"/></td>
	  		</tr>
	    	<tr align="center"><td><h4><font color="#4035e7">多画软件配置信息</font></h4></td></tr>
	    </table>
	    <table border=1 cellspacing=1 width="100%" id="IPM">
		   	<tr>
		   	<td>序号</td>
		   	<td>最小通道</td>
		   	<td>最大通道</td>
		   	<td>录像类型</td>
		   	<td>设备接收URL</td>
		   	<td>参数配置URL</td>
		   	</tr>
	    	<tbody id="newsIPMTB"> 
		    	<%
		    	if(coreDate!=null){
		    	for(int i=0;i<coreDate.getIPMList().size();i++){
		    	IPMInfoVO ipm=(IPMInfoVO)coreDate.getIPMList().get(i);
		    	%>
		    	<tr>
		    	<td><input class="noneinput" name="IPMindex" type="text" size="1" value="<%=i+1%>"/></td>
		    	<td><input class="noneinput" name="IPMIndexMin" type="text" size="5" value="<%=ipm.getIndexMin()%>"/></td>
		    	<td><input class="noneinput" name="IPMIndexMax" type="text" size="5" value="<%=ipm.getIndexMax()%>"/></td>
		    	<td><input class="noneinput" name="IPMRecordType" type="text" size="5" value="<%=ipm.getRecordType()%>"/></td>
		    	<td><input class="noneinput" name="IPMURL" type="text" size="30" value="<%=ipm.getURL()%>"/></td>
		    	<td><input class="noneinput" name="IPMSysURL" type="text" size="30" value="<%=ipm.getSysURL()%>"/></td>
		    	</tr>
		    	<%}}%>
			</tbody>	    	
	    </table>
	    <table width="100%">
	  		<tr align="right"><td>
			    <input name="AddMSG" class="bluebuttoncss" type="button" value="添加 IPM" onClick="insertrow('IPM');"/>
			    <input name="DelMSG" class="bluebuttoncss" type="button" value="删除 IPM" onClick="DelRow('IPM');"/></td>
	    	</tr>
	    	<tr align="center"><td><h4><font color="#4035e7">转码软件配置信息</font></h4></td></tr>
	    </table>
	    <table border=1 cellspacing=1 width="100%" id="TSC">
		   	<tr>
		   	<td>序号</td>
		   	<td>最小通道</td>
		   	<td>最大通道</td>
		   	<td>录像类型</td>
		   	<td>设备接收URL</td>
		   	<td>参数配置URL</td>
		   	</tr>
	    	<tbody id="newsTSCTB"> 
		    	<%
		    	if(coreDate!=null){
		    	for(int i=0;i<coreDate.getTSCList().size();i++){
		    	TSCInfoVO tsc=(TSCInfoVO)coreDate.getTSCList().get(i);
		    	%>
		    	<tr>
		    	<td><input class="noneinput" name="TSCindex" type="text" size="1" value="<%=i+1%>"/></td>
		    	<td><input class="noneinput" name="TSCIndexMin" type="text" size="5" value="<%=tsc.getIndexMin()%>"/></td>
		    	<td><input class="noneinput" name="TSCIndexMax" type="text" size="5" value="<%=tsc.getIndexMax()%>"/></td>
		    	<td><input class="noneinput" name="TSCRecordType" type="text" size="5" value="<%=tsc.getRecordType()%>"/></td>
		    	<td><input class="noneinput" name="TSCURL" type="text" size="30" value="<%=tsc.getURL()%>"/></td>
		    	<td><input class="noneinput" name="TSCSysURL" type="text" size="30" value="<%=tsc.getSysURL()%>"/></td>
		    	</tr>
		    	<%}}%>		   	
			</tbody>	    	
	    </table>
	    <table width="100%">
	    	<tr align="right"><td>    
			    <input name="AddMSG" class="bluebuttoncss" type="button" value="添加TSC" onClick="insertrow('TSC');"/>
			    <input name="DelMSG" class="bluebuttoncss" type="button" value="删除TSC" onClick="DelRow('TSC');"/></td>
			</tr>	
	    	<tr align="center"><td><h4><font color="#4035e7">本地系统配置信息</font></h4></td></tr>
	    </table>
	    <table border=1 cellspacing=1 width="100%" id="system">
		   	<tr>
		   	<!-- <td>前端名称</td><td><input class="noneinput" name="AgentName" type="text" size="30" value="//if(sysVO!=null)//sysVO.getAgentName()//"/></td> -->
		   	<td>前端类型</td><td><input class="noneinput" name="AgentType" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getAgentType()%><%}%>"/></td>
		   	<td>TSGrab的地址</td><td><input class="noneinput" name="TSGrabURL" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getTSGrabURL()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>码率加大录像个数</td><td><input class="noneinput" name="MaxRecordMbpsFlag" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getMaxRecordMbpsFlag()%><%}%>"/></td>
		   	<td>报警是否入库</td><td><input class="noneinput" name="IsHasAlarmID" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getIsHasAlarmID()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>EPG数据是否打包</td><td><input class="noneinput" name="IsEPGZip" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getIsEPGZip()%><%}%>"/></td>
		   	<td>EPG是否从数据库</td><td><input class="noneinput" name="IsEPGFromDataBase" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getIsEPGFromDataBase()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>报警是否主动补报</td><td><input class="noneinput" name="IsAutoAlarmReply" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getIsAutoAlarmReply()%><%}%>"/></td>
		   	<td>最大录像个数</td><td><input class="noneinput" name="MaxAutoRecordNum" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getMaxAutoRecordNum()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>报警上报地址</td><td><input class="noneinput" name="CenterAlarmURL" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getCenterAlarmURL()%><%}%>"/></td>
		   	<td>报警日志开关</td><td><input class="noneinput" name="IsAlarmLogEnable" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getIsAlarmLogEnable()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>中心到前端的地址</td><td><input class="noneinput" name="CenterToAgentURL" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getCenterToAgentURL()%><%}%>"/></td>
		   	<td>前端到中心的地址</td><td><input class="noneinput" name="AgentToCenterURL" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getAgentToCenterURL()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>本地code</td><td><input class="noneinput" name="SrcCode" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getSrcCode()%><%}%>"/></td>
		   	<td>中心code</td><td><input class="noneinput" name="DstCode" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getDstCode()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>下发文件保存地址</td><td><input class="noneinput" name="ReceFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getReceFilePath()%><%}%>"/></td>
		   	<td>上报文件保存地址</td><td><input class="noneinput" name="SendFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getSendFilePath()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>报警上报保存地址</td><td><input class="noneinput" name="AlarmFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getAlarmFilePath()%><%}%>"/></td>
		   	<td>错误文件保存地址</td><td><input class="noneinput" name="SendErrorFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getSendErrorFilePath()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>MHP采集保存地址</td><td><input class="noneinput" name="MHPInfoFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getMHPInfoFilePath()%><%}%>"/></td>
		   	<td>EPG采集保存地址</td><td><input class="noneinput" name="EPGInfoFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getEPGInfoFilePath()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>PSI采集保存地址</td><td><input class="noneinput" name="PSIInfoFilePath" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getPSIInfoFilePath()%><%}%>"/></td>
		   	<td>Tomcat的物理地址</td><td><input class="noneinput" name="TomcatHome" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getTomcatHome()%><%}%>"/></td>
		   	</tr>
		   	<tr>
		   	<td>Tomcat的端口号</td><td><input class="noneinput" name="TomcatPort" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getTomcatPort()%><%}%>"/></td>
		   	<td>本地对外的IP地址</td><td><input class="noneinput" name="LocalRedirectIp" type="text" size="30" value="<%if(sysVO!=null){%><%=sysVO.getLocalRedirectIp()%><%}%>"/></td>
		   	</tr>
	    </table>
	    <table width="100%">
	    <tr align="center"><td><input class="bluebuttoncss" type="submit" value="提交"/><input class="bluebuttoncss" type="reset" value="重置"/></td></tr>
	    </table>
    </form>    
  </body>
</html>
