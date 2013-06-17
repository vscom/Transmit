package com.bvcom.transmit.task;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;

public class StreamRoundInfoQueryReboot extends Thread{
	private AutoAnalysisTimeQueryConfigFile autoAnalysisTimeQueryConfigFile=new AutoAnalysisTimeQueryConfigFile();
	@Override
	public void run() {
		if(autoAnalysisTimeQueryConfigFile.getStreamRoundInfoQueryRebootSwitch().equals("1")){
			String time=autoAnalysisTimeQueryConfigFile.getStreamRoundInfoQueryTime();
			String url=autoAnalysisTimeQueryConfigFile.getStreamRoundInfoQueryURL();
			Date date=new Date();
			date.setHours(Integer.parseInt(time.trim().split(",")[0].trim()));
			date.setMinutes(Integer.parseInt(time.trim().split(",")[1].trim()));
			date.setSeconds(Integer.parseInt(time.trim().split(",")[2].trim()));
			//判断执行开关 并执行
			Timer timer=new Timer();
			timer.scheduleAtFixedRate(new StreamRoundInfoQueryRebootTask(url.trim()) , date, 24*3600*1000);
		}
	}
}
/**
 * 内部类 负责给轮播办卡发送 定时复位协议
 * @author Ji Long  
 *
 */
class StreamRoundInfoQueryRebootTask extends TimerTask{
	private String url="";
	private UtilXML utilXML=new UtilXML();
	public StreamRoundInfoQueryRebootTask(String url){
		this.url=url;
	}
	@Override
	public void run() {
		/*
		<?xml version="1.0" encoding="UTF-8" standalone="yes" ?> 
		<Msg  Version="2.4" MsgID="1000_ID" Type="MonDown" DateTime="2009-08-17 15:30:00" SrcCode="110000G01" DstCode="110000M01" SrcURL="http://10.24.32.28:8089/servlet/receiver" Priority="1" >
		<RebootSet  Type="6"/>     
		</Msg>
		*/
		StringBuffer strBuffer=new StringBuffer();
		strBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
		strBuffer.append("<Msg  Version=\"2.4\" MsgID=\"1000_ID\" Type=\"MonDown\" DateTime=\""+CommonUtility.getDateTime()+"\" SrcCode=\"110000G01\" DstCode=\"110000M01\" SrcURL=\"http://10.24.32.28:8089/servlet/receiver\" Priority=\"1\" >");
		strBuffer.append("<RebootSet  Type=\"6\"/>");
		strBuffer.append("</Msg>");
		if(!url.equals("")){
			utilXML.SendUpXML(strBuffer.toString(), url);
		}
	}
} 















