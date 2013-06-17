package com.bvcom.transmit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;

import com.bvcom.transmit.core.MemCoreData;

public class TransmitConfig extends HttpServlet {
	MemCoreData coreDate=MemCoreData.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req,resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//HttpSession session=req.getSession(false);
		//session.setAttribute("coreDate", coreDate);
		//resp.setContentType("text/html");
		//resp.setCharacterEncoding("UTF-8");
		//System.out.println("===============SMG信息开始===================");
		StringBuffer transmitConfigBuffer=new StringBuffer();
		transmitConfigBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		transmitConfigBuffer.append("<ROOT>\n");
		transmitConfigBuffer.append("\t<SMG>\n");
		List<SMGCardInfoVO> smgList=new ArrayList<SMGCardInfoVO>(); 
		String[] SMGIndex=req.getParameterValues("SMGIndex");
		String[] SMGURL=req.getParameterValues("SMGURL");
		String[] SMGHDFlag=req.getParameterValues("SMGHDFlag");
		String[] SMGHDURL=req.getParameterValues("SMGHDURL");
		String[] SMGIndexType=req.getParameterValues("SMGIndexType");
		for (int i = 0; i < SMGURL.length; i++) {
			//System.out.println(SMGIndex[i].trim()+", "+SMGURL[i].trim()+", "+SMGHDFlag[i].trim()+", "+SMGHDURL[i].trim()+", "+SMGIndexType[i].trim());
			SMGCardInfoVO smg=new SMGCardInfoVO();
			smg.setIndex(Integer.parseInt(SMGIndex[i].trim()));
			smg.setURL(SMGURL[i].trim());
			smg.setHDFlag(Integer.parseInt(SMGHDFlag[i].trim()));
			smg.setHDURL(SMGHDURL[i].trim());
			smg.setIndexType(SMGIndexType[i].trim());
			smgList.add(smg);
			//<SMGCardInfo Index="1"  URL="http://192.168.0.60:8080/Setup1/" HDFlag="0" HDURL="http://192.168.100.101:8080/Setup/" IndexType="ChannelScanQuery" />
			transmitConfigBuffer.append("\t\t<SMGCardInfo Index=\""+SMGIndex[i].trim()+"\"  URL=\""+SMGURL[i].trim()+"\" HDFlag=\""+SMGHDFlag[i].trim()+"\" HDURL=\""+SMGHDURL[i].trim()+"\" IndexType=\""+SMGIndexType[i].trim()+"\" />\n");
		}
		transmitConfigBuffer.append("\t</SMG>\n");
		//System.out.println("===============SMG信息结束===================");
		
		//System.out.println("===============IPM信息开始===================");
		transmitConfigBuffer.append("\t<IPM>\n");
		List<IPMInfoVO> ipmList=new ArrayList<IPMInfoVO>();
		String[] IPMIndexMin=req.getParameterValues("IPMIndexMin");
		String[] IPMIndexMax=req.getParameterValues("IPMIndexMax");
		String[] IPMRecordType=req.getParameterValues("IPMRecordType");
		String[] IPMURL=req.getParameterValues("IPMURL");
		String[] IPMSysURL=req.getParameterValues("IPMSysURL");
		for (int i = 0; i < IPMURL.length; i++) {
			//System.out.println(IPMIndexMin[i].trim()+", "+IPMIndexMax[i].trim()+", "+IPMRecordType[i].trim()+", "+IPMURL[i].trim());
			IPMInfoVO ipm=new IPMInfoVO();
			ipm.setIndexMin(Integer.parseInt(IPMIndexMin[i].trim()));
			ipm.setIndexMax(Integer.parseInt(IPMIndexMax[i].trim()));
			ipm.setRecordType(Integer.parseInt(IPMRecordType[i].trim()));
			ipm.setURL(IPMURL[i].trim());
			ipm.setSysURL(IPMSysURL[i].trim());
			ipmList.add(ipm);
			//<IPMInfo IndexMin="1" IndexMax="20" RecordType="2"  URL="http://192.168.0.34:8280/Setup/ SysURL="http://192.168.0.11:8089/Setup/""/>
			transmitConfigBuffer.append("\t\t<IPMInfo IndexMin=\""+IPMIndexMin[i].trim()+"\" IndexMax=\""+IPMIndexMax[i].trim()+"\" RecordType=\""+IPMRecordType[i].trim()+"\"  URL=\""+IPMURL[i].trim()+"\"  SysURL=\""+IPMSysURL[i].trim()+"\"/>\n");
		}
		transmitConfigBuffer.append("\t</IPM>\n");
		//System.out.println("===============IPM信息结束===================");
		
		//System.out.println("===============TSC信息开始===================");
		transmitConfigBuffer.append("\t<TSC>\n");
		List<TSCInfoVO> tscList=new ArrayList<TSCInfoVO>();
		String[] TSCIndexMin=req.getParameterValues("TSCIndexMin");
		String[] TSCIndexMax=req.getParameterValues("TSCIndexMax");
		String[] TSCRecordType=req.getParameterValues("TSCRecordType");
		String[] TSCURL=req.getParameterValues("TSCURL");
		String[] TSCSysURL=req.getParameterValues("TSCSysURL");
		for (int i = 0; i < TSCURL.length; i++) {
			//System.out.println(TSCIndexMin[i].trim()+", "+TSCIndexMax[i].trim()+", "+TSCRecordType[i].trim()+", "+TSCURL[i].trim());
			TSCInfoVO tsc=new TSCInfoVO();
			tsc.setIndexMin(Integer.parseInt(TSCIndexMin[i].trim()));
			tsc.setIndexMax(Integer.parseInt(TSCIndexMax[i].trim()));
			tsc.setRecordType(Integer.parseInt(TSCRecordType[i].trim()));
			tsc.setURL(TSCURL[i].trim());
			tsc.setSysURL(TSCSysURL[i].trim());
			tscList.add(tsc);
			//<TSCInfo IndexMin="1" IndexMax="10" URL="http://192.168.0.22:8089/Setup/" RecordType="2"/>
			transmitConfigBuffer.append("\t\t<TSCInfo IndexMin=\""+TSCIndexMin[i].trim()+"\" IndexMax=\""+TSCIndexMax[i].trim()+"\" URL=\""+TSCURL[i].trim()+"\" RecordType=\""+TSCRecordType[i].trim()+"\" SysURL=\""+TSCSysURL[i].trim()+"\"/>\n");
		}	
		transmitConfigBuffer.append("\t</TSC>\n");
		//System.out.println("===============TSC信息结束===================");
		
		//System.out.println("===============SYS本地信息开始===================");
		SysInfoVO system=new SysInfoVO();
		String MaxRecordMbpsFlag=req.getParameter("MaxRecordMbpsFlag");
		String IsHasAlarmID=req.getParameter("IsHasAlarmID");
		String IsEPGZip=req.getParameter("IsEPGZip");
		String IsEPGFromDataBase=req.getParameter("IsEPGFromDataBase");
		String IsAutoAlarmReply=req.getParameter("IsAutoAlarmReply");
		String MaxAutoRecordNum=req.getParameter("MaxAutoRecordNum");
		String CenterAlarmURL=req.getParameter("CenterAlarmURL");
		String IsAlarmLogEnable=req.getParameter("IsAlarmLogEnable");
		String SrcCode=req.getParameter("SrcCode");
		String DstCode=req.getParameter("DstCode");
		String ReceFilePath=req.getParameter("ReceFilePath");
		String SendFilePath=req.getParameter("SendFilePath");
		String AlarmFilePath=req.getParameter("AlarmFilePath");
		String SendErrorFilePath=req.getParameter("SendErrorFilePath");
		String MHPInfoFilePath=req.getParameter("MHPInfoFilePath");
		String EPGInfoFilePath=req.getParameter("EPGInfoFilePath");
		String PSIInfoFilePath=req.getParameter("PSIInfoFilePath");
		String TomcatHome=req.getParameter("TomcatHome");
		String TomcatPort=req.getParameter("TomcatPort");
		String LocalRedirectIp=req.getParameter("LocalRedirectIp");
		//String AgentName=req.getParameter("AgentName");
		String AgentType=req.getParameter("AgentType");
		String CenterToAgentURL=req.getParameter("CenterToAgentURL");
		String AgentToCenterURL=req.getParameter("AgentToCenterURL");
		String TSGrabURL=req.getParameter("TSGrabURL");
		
		//system.setAgentName(AgentName.trim());
		system.setAgentType(AgentType.trim());
		system.setCenterToAgentURL(CenterToAgentURL.trim());
		system.setAgentToCenterURL(AgentToCenterURL.trim());
		system.setTSGrabURL(TSGrabURL.trim());
		system.setMaxRecordMbpsFlag(Integer.parseInt(MaxRecordMbpsFlag.trim()));
		system.setIsHasAlarmID(Integer.parseInt(IsHasAlarmID.trim()));
		system.setIsEPGZip(Integer.parseInt(IsEPGZip.trim()));
		system.setIsEPGFromDataBase(Integer.parseInt(IsEPGFromDataBase.trim()));
		system.setIsAutoAlarmReply(Integer.parseInt(IsAutoAlarmReply.trim()));
		system.setMaxAutoRecordNum(Integer.parseInt(MaxAutoRecordNum.trim()));
		system.setCenterAlarmURL(CenterAlarmURL.trim());
		system.setIsAlarmLogEnable(Integer.parseInt(IsAlarmLogEnable.trim()));
		system.setSrcCode(SrcCode.trim());
		system.setDstCode(DstCode.trim());
		system.setReceFilePath(ReceFilePath.trim());
		system.setSendFilePath(SendFilePath.trim());
		system.setAlarmFilePath(AlarmFilePath.trim());
		system.setSendErrorFilePath(SendErrorFilePath.trim());
		system.setMHPInfoFilePath(MHPInfoFilePath.trim());
		system.setEPGInfoFilePath(EPGInfoFilePath.trim());
		system.setPSIInfoFilePath(PSIInfoFilePath.trim());
		system.setTomcatHome(TomcatHome.trim());
		system.setTomcatPort(TomcatPort.trim());
		system.setLocalRedirectIp(LocalRedirectIp.trim());
		transmitConfigBuffer.append("\t<SYSTEM>\n");
		// <SysInfo MaxRecordMbpsFlag="5" IsHasAlarmID="1" IsEPGZip="1" IsEPGFromDataBase="0" IsAutoAlarmReply="1" MaxAutoRecordNum="40" CenterAlarmURL="http://10.134.121.4/interface/receive.asmx" SrcCode="440000M01" DstCode="440000G01"/>
		//transmitConfigBuffer.append("\t\t<SysInfo MaxRecordMbpsFlag=\""+MaxRecordMbpsFlag.trim()+"\" IsHasAlarmID=\""+IsHasAlarmID.trim()+"\" IsEPGZip=\""+IsEPGZip.trim()+"\" IsEPGFromDataBase=\""+IsEPGFromDataBase.trim()+"\" IsAutoAlarmReply=\""+IsAutoAlarmReply.trim()+"\" MaxAutoRecordNum=\""+MaxAutoRecordNum.trim()+"\" CenterAlarmURL=\""+CenterAlarmURL.trim()+"\" SrcCode=\""+SrcCode.trim()+"\" DstCode=\""+DstCode.trim()+"\"  AgentName=\""+AgentName.trim()+"\" AgentType=\""+AgentType.trim()+"\" CenterToAgentURL=\""+CenterToAgentURL.trim()+"\" AgentToCenterURL=\""+AgentToCenterURL.trim()+"\"/>\n");
		transmitConfigBuffer.append("\t\t<SysInfo MaxRecordMbpsFlag=\""+MaxRecordMbpsFlag.trim()+"\" IsHasAlarmID=\""+IsHasAlarmID.trim()+"\" IsEPGZip=\""+IsEPGZip.trim()+"\" IsEPGFromDataBase=\""+IsEPGFromDataBase.trim()+"\" IsAutoAlarmReply=\""+IsAutoAlarmReply.trim()+"\" MaxAutoRecordNum=\""+MaxAutoRecordNum.trim()+"\" CenterAlarmURL=\""+CenterAlarmURL.trim()+"\" SrcCode=\""+SrcCode.trim()+"\" DstCode=\""+DstCode.trim()+"\" AgentType=\""+AgentType.trim()+"\" CenterToAgentURL=\""+CenterToAgentURL.trim()+"\" AgentToCenterURL=\""+AgentToCenterURL.trim()+"\"/>\n");
		//<SysLog  IsAlarmLogEnable="0" receFilePath="D:\\Loging\\ReceCenterFile" sendFilePath="D:\\Loging\\SendUpFile" alarmFilePath="D:\\Loging\\AlarmUpFile" sendErrorFilePath="D:\\Loging\\ErrorUpFile"/>
		transmitConfigBuffer.append("\t\t<SysLog  IsAlarmLogEnable=\""+IsAlarmLogEnable.trim()+"\" receFilePath=\""+ReceFilePath.trim()+"\" sendFilePath=\""+SendFilePath.trim()+"\" alarmFilePath=\""+AlarmFilePath.trim()+"\" sendErrorFilePath=\""+SendErrorFilePath.trim()+"\"/>\n");
		//<PSIInfo MHPInfoFilePath="D:\MHPInfo" EPGInfoFilePath="D:\EPGInfo" PSIInfoFilePath="D:\PSI_SIInfo" />
		transmitConfigBuffer.append("\t\t<PSIInfo MHPInfoFilePath=\""+MHPInfoFilePath.trim()+"\" EPGInfoFilePath=\""+EPGInfoFilePath.trim()+"\" PSIInfoFilePath=\""+PSIInfoFilePath.trim()+"\" TSGrabURL=\""+TSGrabURL.trim()+"\"/>\n");
		//<Tomcat TomcatHomePath="C:\apache-tomcat-6.0.29" TomcatPort="8088" LocalRedirectIp="192.168.2.216"/>
		transmitConfigBuffer.append("\t\t<Tomcat TomcatHomePath=\""+TomcatHome.trim()+"\" TomcatPort=\""+TomcatPort.trim()+"\" LocalRedirectIp=\""+LocalRedirectIp.trim()+"\"/>\n");
		transmitConfigBuffer.append("\t</SYSTEM>\n");
		transmitConfigBuffer.append("</ROOT>");
		
		//System.out.println(transmitConfigBuffer.toString());
		/*
		System.out.println(MaxRecordMbpsFlag+", "+IsHasAlarmID);
		System.out.println(IsEPGZip+", "+IsEPGFromDataBase);
		System.out.println(IsAutoAlarmReply+", "+MaxAutoRecordNum);
		System.out.println(CenterAlarmURL+", "+IsAlarmLogEnable);
		System.out.println(SrcCode+", "+DstCode);
		System.out.println(ReceFilePath+", "+SendFilePath);
		System.out.println(AlarmFilePath+", "+SendErrorFilePath);
		System.out.println(MHPInfoFilePath+", "+EPGInfoFilePath);
		System.out.println(PSIInfoFilePath+", "+TomcatHome);
		System.out.println(TomcatPort+", "+LocalRedirectIp);
		System.out.println("===============SYS本地信息结束===================");
		*/
		coreDate.setSMGCardList(smgList);
		coreDate.setIPMList(ipmList);
		coreDate.setTSCList(tscList);
		coreDate.setSysVO(system);

		String fileStr=getConfigFilePath();
		//System.out.println("获取配置文件中保存的位置："+fileStr);
		//D\:/config/TransmitConfig.xml
		//String[] files=fileStr.split("/");
		//String newFile=files[0]+"/"+files[1]+"/config.xml";
		//System.out.println("新保存地址："+newFile);
		CommonUtility.StoreIntoFile(transmitConfigBuffer.toString(), fileStr);
		
		RequestDispatcher dispatcher=req.getRequestDispatcher("../TransmitConfig.jsp");
		dispatcher.forward(req, resp);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
    private String getConfigFilePath() {
        
        Properties p = new Properties();
        
        String filePath = "";
        InputStream inStr = null;
        
        try {
            inStr = this.getClass().getResourceAsStream("/config.properties");
            p.load(inStr);
            filePath = p.getProperty("configFilePath");
        } catch (FileNotFoundException e) {
            CommonUtility.printErrorTrace(e);
        } catch (IOException ioe){
            CommonUtility.printErrorTrace(ioe);
        } finally {
            try {
                if (inStr != null) {
                    inStr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.clear();
        }
        return filePath;
    }
	
}
