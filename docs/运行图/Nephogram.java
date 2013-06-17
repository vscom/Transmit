package com.bvcom.servflatinfo.action.Nephogram;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.bvcom.agentinfo.dao.SysAgentinfo;
import com.bvcom.agentinfo.dao.SysAgentinfoDAO;
import com.bvcom.common.Constants;
import com.bvcom.common.util.LocaleHostIPS;
import com.bvcom.servflatinfo.servelet.CmdCommunicationThread;
import com.bvcom.servflatinfo.servelet.RecvMainCtrlCmdTypeInfo;
import com.bvcom.servflatinfo.servelet.CmdPO.CmdInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.bvcom.adss.pubfun.PubFun;

public class Nephogram extends ActionSupport {
	private static int[] tempIdata ={
		-103,-121,97,110,98,-125,-117,114,-111,-106,124,-115,
		-128,-102,115,-128,-108,87,108,115,120,-117,-119,-123,
		-94,-127,114,-110,114,-123,127,85,103,-127,-121,-115,
		-126,-91,-100,100,125,-101,-92,-120,108,127,124,118,
		-115,76,127,-122,102,-110,111,125,-108,-123,127,114,
		-115,-92,-127,-124
	};
	private static int[] tempQdata = {
		119,103,112,123,-100,-124,111,-116,-127,116,112,-102,
		-128,114,-101,119,-100,123,-115,-79,122,-121,117,-123,
		120,-125,-114,102,-125,-121,114,119,-118,95,97,-90,
		-105,-126,-120,-128,108,81,-125,112,116,-128,122,124,
		-126,103,-114,113,-108,-128,115,-116,123,-100,-89,121,
		-124,122,-112,113
	};
	//日志记录者
	private static final Log loger = LogFactory.getLog(Nephogram.class);
	
	//几种查询的返回状态及描述
	//查询成功
	private static final String SUCCESS_FLAG = "0";
	private static final String SUCCESS_DESC = "查询成功";
	//查询失败
	private static final String PARSER_ERROR_FLAG = "1";
	private static final String PARSER_ERROR_DESC = "解析云图数据失败";
	//查询超时
	private static final String TIME_OUT_FLAG = "2";
	private static final String TIME_OUT_DESC = "查询返回超时";
	//传入参数不正确
	private static final String INVALID_PARAM_FLAG = "3";
	private static final String INVALID_PARAM_DESC = "传入参数不正确";
	
	//获取CMD方式通讯线程失败
	private static final String GET_THREAD_FAILED_FLAG = "4";
	private static final String GET_THREAD_FAILED_DESC = "获取CMD通讯线程失败";
	
	//前端处理异常
	private static final String AGENT_ERROR_FLAG = "5";
	private static final String AGENT_ERROR_DESC = "前端处理异常";
	
	//设备无数据
	private static final String NO_DATA_FLAG = "6";
	private static final String NO_DATA_DESC = "设备无数据";
	
	private static final Long OUT_TIME = 15000L;

	//通道
	private String index=null;
	//频点
	private String freq=null;
	//查询返回状态
	private String queryState=null;
	//查询返回状态描述
	private String queryDesc=null;
	
	//CMD返回数据信息
	private CmdInfo cmdInfo;
	
	int ILen = 64;
	
	//I
	private byte [] iData = new byte[ILen];
	
	//Q
	private byte [] qData = new byte[ILen];
	
	//前端参数
	private String servflatId;
	
	private String agentid;
 	  
	
	//等待消息返回的同步对象
	public Object synObj = new Object();
	
	@Override
	public String execute() throws Exception {
		if(freq==null){
			queryState = INVALID_PARAM_FLAG;
			queryDesc = INVALID_PARAM_DESC;
			return SUCCESS;
		}
		if(index==null){
			index = "1";
		}
		
		//从Session获取当前服务平台ID
		String servflatId = (String) ActionContext.getContext().getSession().get("servflatid");
		
		//前端信息获取者
		List agentInfoList;
		SysAgentinfo agentInfo;
		SysAgentinfoDAO agentDao = new SysAgentinfoDAO();
		
		agentInfoList = agentDao.findByServflatId(Integer.parseInt(servflatId));
		
		//根据服务平台下的前端数，取得各个前端的ip列表和前端ID列表
		String [] ipList;
		String [] agentIdList;
		String [] agentCode;
		String [] agentType;
		
		int agentCnt = agentInfoList.size();
		if(agentCnt>0){
			ipList = new String[agentCnt];
			agentIdList = new String[agentCnt];
			agentCode = new String[agentCnt];
			agentType = new String[agentCnt];
		}else{
			return SUCCESS;
		}
		for(int i=0; i<agentCnt; i++){
			//获取服务平台信息
			agentInfo = (SysAgentinfo)agentInfoList.get(i);
			//获取目标ip
			ipList[i] = agentInfo.getCip();
			//获取前端ID
			agentIdList[i] = String.valueOf(agentInfo.getAgentid());
			//获取前端编码
			agentCode[i] = agentInfo.getCcode();
			//
			agentType[i] =agentInfo.getCagenttype();
		}
		
		///**************新增加前端信息处理********by teng 2009-07-26***********
		
		if((this.getAgentid()=="")||(this.getAgentid()==null))
		{
			
			return SUCCESS;
		}
		
		agentInfo 		= agentDao.findById(Long.parseLong(this.getAgentid()));
		agentCode[0]	= agentInfo.getCcode();
		agentType[0]	= agentInfo.getCagenttype();
		agentIdList[0] 	= this.getAgentid();
		///**************新增加前端信息处理*******************
		
		/*优化代码将IP和端口都写成静态常量
		 
		//获取服务器IP
		String localip;
		localip = LocaleHostIPS.getLocalIP();
		
		//获取当前PORT
		HttpServletRequest request = ServletActionContext.getRequest();
		int port = request.getLocalPort();
		
		//获取Web根路径
		String webpath = request.getContextPath();
		
		//生成接收返回信息的URL
		String srcURL = "http://"+localip+":"+port+webpath+"/servlet/MainCtrlCMDRecvServlet";
		
		*/
		String srcURL = "http://" + Constants.Local_IP + ":" + Constants.Tomcat_Port+"/Web2.0/servlet/MainCtrlCMDRecvServlet";
		
		//同步资源锁
		Object synObject = new Object();
		
		//发送的命令
		CmdInfo cmd = new CmdInfo();
		
		//设备类型
		if(Integer.valueOf(agentType[0])!=4)
		{
			cmd.setIndex(index);
			cmd.setRwFlag("Read");
			cmd.setOid("12321");
			cmd.setCmdType("26");
			cmd.setCmdLen("0");
			cmd.setCmdData("NULL");
		}
		else{
			cmd.setIndex(index);
			cmd.setRwFlag("Read");
			cmd.setOid("12321");
			cmd.setCmdType("1");			//SMG的云图信息
			cmd.setCmdLen("0");
			cmd.setCmdData(this.getFreq());
		}
		

		CmdCommunicationThread queryThread = RecvMainCtrlCmdTypeInfo.getQueryThread(synObject, 
																					agentIdList[0], 
																					agentCode[0], 
																					srcURL, 
																					cmd);
		
		
		if(null == queryThread){
			queryState = GET_THREAD_FAILED_FLAG;
			queryDesc = GET_THREAD_FAILED_DESC;
			return SUCCESS;
		}else{
			long start = System.currentTimeMillis();
			synchronized (synObject){synObject.wait(OUT_TIME);}
			long now = System.currentTimeMillis();
			long timeSpan = now - start;
			if(timeSpan >= OUT_TIME){
				queryState = TIME_OUT_FLAG;
				queryDesc = TIME_OUT_DESC;
				return SUCCESS;
			}else{
				cmdInfo = queryThread.getCmdInfo();
				String ret="";
				if(cmdInfo!=null){
					ret = cmdInfo.getRet();
				}	

				if("1".equals(ret)){
					queryState = AGENT_ERROR_FLAG;
					queryDesc = AGENT_ERROR_DESC;
				}
				
				if("2".equals(ret)){
					queryState = NO_DATA_FLAG;
					queryDesc = NO_DATA_DESC;
				}
				
				if("0".equals(ret)){

					//设备类型
					if(Integer.valueOf(agentType[0])!=4)
					{
						byte[] data = PubFun.StringAscToBytes(cmdInfo.getCmdData());
						int IIndex = data.length-128;
						int QIndex = data.length-64;
						
						System.arraycopy(data, IIndex, iData, 0, 64);
						System.arraycopy(data, QIndex, qData, 0, 64);
					}
					else
					{
						//int Len = Integer.valueOf(cmdInfo.getCmdLen());
						
						int ILen = cmdInfo.getIData().length;
						if(cmdInfo.getMer().equals("") || cmdInfo.getMer() == null)
						{
							for(int i=0;i<ILen;i++)
							{
								iData[i]=Byte.valueOf(cmdInfo.getIData()[i]);
								qData[i]=Byte.valueOf(cmdInfo.getIData()[i]);
							}
						}
						else//MER有值，适用于AD988HP双通道
						{
							int flag = 64;	
							int mer = Integer.valueOf(cmdInfo.getMer());
							
							if(mer >= 36)
							{
								for(int i=0;i<ILen;i++){
									int temp = 0;
									String strIDate = "";
									String strQDate = "";
									
									//I值
									if(cmdInfo.getIData()[i].charAt(0) != '-')
										temp = Integer.valueOf(cmdInfo.getIData()[i]) - flag - ((i%4)*16);
									else
										temp = Integer.valueOf(cmdInfo.getIData()[i]) + flag + ((i%4)*16);						
									
									if((temp >= 0 && temp < 8) || (temp > 10 && temp < 18))
									{
										temp = 9;
									}
									else if((temp >= 18 && temp < 26) || (temp > 28 && temp < 36))
									{
										temp = 27;
									}
									else if((temp >= 36 && temp < 44) || (temp > 46 && temp < 54))
									{
										temp = 45;
									}
									else if((temp >= 54 && temp < 62) || (temp > 64 && temp <= 72))
									{
										temp = 63;
									}
									else if((temp > -8 && temp < 0) || (temp > -18 && temp < -10))
									{
										temp = -9;
									}
									else if((temp > -26  && temp <= -18) || (temp > -36 && temp < -28))
									{
										temp = -27;
									}
									else if((temp > -44 && temp <= -36) || (temp > -54 && temp < -46))
									{
										temp = -45;
									}
									else if((temp >= -62 && temp <= -54) || (temp >= -72 && temp < -64))
									{
										temp = -63;
									}
									strIDate = String.valueOf(temp);
									
									//Q值
									if(cmdInfo.getQData()[i].charAt(0) != '-')
										temp = Integer.valueOf(cmdInfo.getQData()[i]) - flag - (((i/4)-(i/16)*4 )*16);
									else
										temp = Integer.valueOf(cmdInfo.getQData()[i]) + flag +(((i/4)-(i/16)*4 )*16);
									if((temp >= 0 && temp < 8) || (temp > 10 && temp < 18))
									{
										temp = 9;
									}
									else if((temp >= 18 && temp < 26) || (temp > 28 && temp < 36))
									{
										temp = 27;
									}
									else if((temp >= 36 && temp < 44) || (temp > 46 && temp < 54))
									{
										temp = 45;
									}
									else if((temp >= 54 && temp < 62) || (temp > 64 && temp <= 72))
									{
										temp = 63;
									}
									else if((temp > -8 && temp < 0) || (temp > -18 && temp < -10))
									{
										temp = -9;
									}
									else if((temp > -26  && temp <= -18) || (temp > -36 && temp < -28))
									{
										temp = -27;
									}
									else if((temp > -44 && temp <= -36) || (temp > -54 && temp < -46))
									{
										temp = -45;
									}
									else if((temp >= -62 && temp <= -54) || (temp >= -72 && temp < -64))
									{
										temp = -63;
									}
									strQDate = String.valueOf(temp);
									
									iData[i]=Byte.valueOf(strIDate);
									qData[i]=Byte.valueOf(strQDate);
									
//									loger.info("********iData["+i+"] = "+iData[i]+"***********\n");
//									loger.info("********qData["+i+"] = "+qData[i]+"***********\n");
		
								}
							}
							else if(mer > 20 && mer < 36)
							{
								for(int i=0;i<ILen;i++){
									int temp = 0;
									String strIDate = "";
									String strQDate = "";
									
									//I值
									if(cmdInfo.getIData()[i].charAt(0) != '-')
										temp = Integer.valueOf(cmdInfo.getIData()[i]) - flag - ((i%4)*16);
									else
										temp = Integer.valueOf(cmdInfo.getIData()[i]) + flag + ((i%4)*16);						
									
									if((temp >= 0 && temp < 5) || (temp >= 18 && temp < 23) || (temp >= 36 && temp < 41) || (temp >= 54 && temp < 59))
									{
										temp = temp + 5;
									}
									else if((temp > 13 && temp <= 18) || (temp > 31 && temp <= 36) || (temp > 49 && temp <= 54) || (temp > 67 && temp <= 72))
									{
										temp = temp - 5;
									}
									else if((temp < 0 && temp > -5) || (temp <= -18 && temp > -23) || (temp <= -36 && temp > -41) || (temp <= -54 && temp > -59))
									{
										temp = temp - 5;
									}
									else if((temp < -13 && temp > -18) || (temp < -31 && temp > -36) || (temp < -49 && temp > -54) || (temp < -67 && temp >= -72))
									{
										temp = temp + 5;
									}
									strIDate = String.valueOf(temp);
									
									//Q值
									if(cmdInfo.getQData()[i].charAt(0) != '-')
										temp = Integer.valueOf(cmdInfo.getQData()[i]) - flag - (((i/4)-(i/16)*4 )*16);
									else
										temp = Integer.valueOf(cmdInfo.getQData()[i]) + flag +(((i/4)-(i/16)*4 )*16);
									
									if((temp >= 0 && temp < 5) || (temp >= 18 && temp < 23) || (temp >= 36 && temp < 41) || (temp >= 54 && temp < 59))
									{
										temp = temp + 5;
									}
									else if((temp > 13 && temp <= 18) || (temp > 31 && temp <= 36) || (temp > 49 && temp <= 54) || (temp > 67 && temp <= 72))
									{
										temp = temp - 5;
									}
									else if((temp < 0 && temp > -5) || (temp <= -18 && temp > -23) || (temp <= -36 && temp > -41) || (temp <= -54 && temp > -59))
									{
										temp = temp - 5;
									}
									else if((temp < -13 && temp > -18) || (temp < -31 && temp > -36) || (temp < -49 && temp > -54) || (temp < -67 && temp >= -72))
									{
										temp = temp + 5;
									}
									strQDate = String.valueOf(temp);
									
									iData[i]=Byte.valueOf(strIDate);
									qData[i]=Byte.valueOf(strQDate);
									
//									loger.info("********iData["+i+"] = "+iData[i]+"***********\n");
//									loger.info("********qData["+i+"] = "+qData[i]+"***********\n");
		
								}
							}
							else if(mer <= 20)
							{
								for(int i=0;i<ILen;i++){
									int temp = 0;
									String strIDate = "";
									String strQDate = "";
									
																		
									if(cmdInfo.getIData()[i].charAt(0) != '-')
										temp = Integer.valueOf(cmdInfo.getIData()[i]);
									else
										temp = Integer.valueOf(cmdInfo.getIData()[i]);
									
									if(temp >= 128)
										temp = temp - 128;
									else if(temp <= -128)
										temp = temp + 128;
									else if(temp > 36 && temp < 128) 
										temp = temp - 72;
									else if(temp < -36 && temp > -128)
										temp = temp + 72;
									else if(temp > 27 && temp <= 36)
										temp = temp - 36;
									else if(temp >= -36 && temp < -27)
										temp = temp + 36;
									else if(temp == 0)
									{
										if(tempIdata[i] > 0)
											temp = tempIdata[i] - 100;
										else 
											temp = tempIdata[i] + 100;
									}									
									strIDate = String.valueOf(temp);
									
									if(cmdInfo.getQData()[i].charAt(0) != '-')
										temp = Integer.valueOf(cmdInfo.getQData()[i]);
									else
										temp = Integer.valueOf(cmdInfo.getQData()[i]);
									
									if(temp >= 128)
										temp = temp - 128;
									else if(temp <= -128)
										temp = temp + 128;
									else if(temp > 36 && temp < 128) 
										temp = temp - 72;
									else if(temp < -36 && temp > -128)
										temp = temp + 72;
									else if(temp > 27 && temp <= 36)
										temp = temp - 36;
									else if(temp >= -36 && temp < -27)
										temp = temp + 36;
									else if(temp == 0)
									{
										if(tempQdata[i] > 0)
											temp = tempQdata[i] - 100;
										else 
											temp = tempQdata[i] + 100;
									}	
									
									strQDate = String.valueOf(temp);
									
									iData[i]=Byte.valueOf(strIDate);
									qData[i]=Byte.valueOf(strQDate);
									
									//loger.info("********iData["+i+"] = "+iData[i]+"***********\n");
									//loger.info("********qData["+i+"] = "+qData[i]+"***********\n");
		
								}								
							}
						}


					}
					
					
					queryState = SUCCESS_FLAG;
					queryDesc = SUCCESS_DESC;
				}
				return SUCCESS;
			}
		}
	}

	//getter & setter
	public String getFreq() {
		return freq;
	}
	
	public void setFreq(String freq) {
		this.freq = freq;
	}
	
	public String getIndex() {
		return index;
	}
	
	public void setIndex(String index) {
		this.index = index;
	}

	public String getQueryState() {
		return queryState;
	}

	public void setQueryState(String queryState) {
		this.queryState = queryState;
	}

	public String getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	public byte[] getIData() {
		return iData;
	}

	public void setIData(byte[] data) {
		iData = data;
	}

	public byte[] getQData() {
		return qData;
	}

	public void setQData(byte[] data) {
		qData = data;
	}

	public String getServflatId() {
		return servflatId;
	}

	public void setServflatId(String servflatId) {
		this.servflatId = servflatId;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}
	
}
