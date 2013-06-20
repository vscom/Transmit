package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class MosaicStreamRoundInfoQuery {
	
    private static Logger log = Logger.getLogger(MosaicStreamRoundInfoQuery.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    private String Index="";
    
    private String RunTime="";
    
    private String WindowNumber="";
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    
    
    public MosaicStreamRoundInfoQuery(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. 解析xml获取节目信息
     * 2. 判断该节目是否已经设置为一对一
     * 3. 把不是的下发自动录像给板卡
     * 4. 更新节目映射表recordType = 4
     * 4. 下发多画
     * 5. 下发RTVM
     * 6. 返回平台
     * TODO 马赛克过期删除相关的节目信息 
     * 目前只给给板卡下发Del命令， 
     * 需要删除recordType = 4的节目
     * 还需要更具这些信息下发Set命令
     * By: Bian Jiang 2012.3.21
     * @throws DaoException 
     */
    @SuppressWarnings({ "deprecation", "unchecked", "static-access" })
	public void downXML() throws DaoException {
    	// 返回数据
        String upString = "";
       
        boolean isErr = false;
        
    	Document document=null;
    	@SuppressWarnings("unused")
		Document iasdoc =null;
    	
    	try {
			document=utilXML.StringToXML(downString);
			iasdoc = utilXML.StringToXML(downString);
			
		} catch (CommonException e) {
			log.error("String转换xml错误："+e.getMessage());
			isErr = true;
		}
		
		//获取平台下发的轮播窗体数
		int windowNum = getParseWindowNum(document);
		if(windowNum==1){
			StreamRoundInfoQueryHandle streamRoundInfoQuery = new StreamRoundInfoQueryHandle(this.downString, this.bsData);
        	streamRoundInfoQuery.downXML();
        	streamRoundInfoQuery = null;
			return;
		}
		
		 if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_5)) {
         	this.downString = this.downString.replaceAll("Version=\"2.5\"", "Version=\"2.4\"");
         }
		
		//先将轮播的节目删除：（当平台没有点击停止按钮时候，需要先清除轮播的节目信息）
		//更改一对一实时视频表的业务类型状态recordtype=4
    	// 取得马赛克相关的节目信息
		StopPlayingVideoHandle stopPlayingVideoHandle =new StopPlayingVideoHandle();
		@SuppressWarnings("unused")
		List<SetAutoRecordChannelVO> voList = stopPlayingVideoHandle.getProgramInfoByIndex();
		//同时更新一对一节目表，将recordtype=4 修改为初始状态
		stopPlayingVideoHandle.updateMosaicChannelMapping(voList);
		
		
    	List<String> channlLists=new ArrayList<String>();
        SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
        List SMGSendList = new ArrayList();//SMG的列表信息
		List channelSendList = new ArrayList();//Channel Index List
		List<SetAutoRecordChannelVO> autoRecordList = new ArrayList();
		
    	//解析出节目信息
    	channlLists=parse(document);
    	List<String> channlList=new ArrayList<String>();
    	//存放非一对一的节目信息、节目分（一对一、非一对一）
    	channlList = isChannlExist(channlLists);
    	//如果channlList.size() 不等于0
    	//那么把集合中的节目信息封装为自动录像下发到板卡
    	if(channlList.size()!=0){
    		//把节目封装成自动录像下发到板卡
    		 autoRecordList = setAutoRecord(channlList);
    	     int channel = 0;
    	     for(int i=0;i<autoRecordList.size();i++){   
    	    	 SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)autoRecordList.get(i);
	    		 //将板卡号存入channelSendList中，且保证channelSendList无重复的通道号
    	    	 int channelIndex = vo.getDevIndex();
    	    	 if (channel != channelIndex && channelIndex != 0) {
					boolean isHasIndex = false;
					for(int j=0; j<channelSendList.size(); j++) {
						int no = Integer.valueOf((Integer)channelSendList.get(j)) ;
						if(channelIndex == no) {
							isHasIndex = true;
							break;
						}
					}
					if(!isHasIndex) {
						channelSendList.add(channelIndex);
					}
    	    	 }
    	    	channel = channelIndex;
    	    	// *************下发smg(需要给哪些板卡发信息)**************
	    		CommonUtility.checkSMGChannelIndex(channelIndex, SMGSendList);
    	    }
    	
    	     
    	     String msgStr ="";
    	     
    	     if (channelSendList.size() != 0) {
    	    		@SuppressWarnings("unused")
					List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = handle.GetProgramInfoByIndex(channelSendList, true);
    	    		msgStr = createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set");
    	     }
    	    
    	     /*
    		
    		//获取需要给smg板卡 下发的 url
    		List<SMGCardInfoVO> smgList=new ArrayList<SMGCardInfoVO>();
    		//重新分配资源：无法找到一对一的节目信息，有一对一的节目信息
    		getSmgUrl(channlList,smgList);
    		String url="";
    		for(int i=0;i<smgList.size();i++){
    			SMGCardInfoVO smg = (SMGCardInfoVO) smgList.get(i);
    			if(smg.getIndexType().equals("AutoRecord")){
    				//此地方处理为第一个自动录制通道
	    			if(!url.equals(smg.getURL().trim())){
	    				try {
							utilXML.SendDownNoneReturn(msgStr, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
							url = smg.getURL().trim();
							try {
								Thread.sleep(1000 * 1);
							} catch (InterruptedException e) {
		
							}
						} catch (CommonException e) {
							log.error("下发自动录像到SMG出错：" + smg.getURL());
							isErr = true;
						}
	    			}
    			}
    		}
    		*/
    	     //马赛克轮播设置的时候，不需要给TSC下发自动录制，只需要给IAS和SMG
    	     
    	     //1：没有给TSC和IPM发送成功时不在给板卡发信息
 	        if(autoRecordList.size() > 0) {
 	        
 	        	String url = "";
 	        	SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
 	        	
 		        for(int l=0;l<SMGSendList.size();l++)
 		        {
 		            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(l);
 		            try {
 		                if (!url.equals(smg.getURL().trim())) {
 		                msgStr = msgStr.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
 		                msgStr = msgStr.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
 		                // 自动录像下发 timeout 1000*30 三十秒
 		                utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(msgStr), smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
 		                url = smg.getURL().trim();
 		                }
 		                try {
 							Thread.sleep(1000 * 1);
 						} catch (InterruptedException e) {
 	
 						}
 		            } catch (CommonException e) {
 		                log.error("下发自动录像到SMG出错：" + smg.getURL());
 		            }
 		            
 		           //更新板卡的URL地址
		            try {
		            	handle.updateSMGURLByDevIndex(smg.getIndex(), smg.getURL());
		            } catch (Exception ex) {
		            	
		            }
 		        }
 	        }
 	            
    		/*
    		// 更新节目映射表 
    		SetAutoRecordChannelHandle setAutoRecordChannel = new SetAutoRecordChannelHandle();
    		// RecordType 0：不录像，1:代表故障触发录制   2：24小时录像(默认)	3: 任务录像  4: 马赛克合成轮播
            for(int i=0; i< autoRecordList.size(); i++) {
            	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)autoRecordList.get(i);
    			try {
    				// 4: 马赛克合成轮播 Add By: Bian Jiang 2012.3.21
        			vo.setRecordType(4);
					setAutoRecordChannel.upChannelRemappingIndex(vo);
				} catch (DaoException e) {
					log.error("更新马赛克合成轮播出错：" + e.getLocalizedMessage());
				}
            }
            */
    	}
    	
    	//下发给多画
    	//增加多画面的通道号
    	
    	//解析出节目信息
    	channlLists=parse(iasdoc);
    	
    	List ipmList=coreData.getIPMList();
    	for(int i=0;i<ipmList.size();i++){
    		IPMInfoVO ipm = (IPMInfoVO) ipmList.get(i);
    		if(ipm.getRecordType()==3){
    			try {
//    				String tomcatUrl="http://"+coreData.getSysVO().getLocalRedirectIp()+":"+coreData.getSysVO().getTomcatPort()+"/transmit/servlet/transmit";
					utilXML.SendDownNoneReturn(iasdoc.asXML(), ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
					
				} catch (CommonException e) {
					log.error("马赛克轮训下发多画错误："+e.getMessage());
					isErr = true;
				}
    		}
    	}
    	MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
		} catch (DaoException e1) {
			log.error("取得多画面合成(马赛克)URL错误: " + e1.getMessage());
			isErr = true;
		}
    	
		RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setFreq(rtvsVO.getFreq());
        rtvsVO.setServiceID(rtvsVO.getServiceID());
        rtvsVO.setIndex(0);
        String rtvsString = createForDownXML(bsData, rtvsVO);
        
        
        if (isErr) {
            // 失败
            upString = utilXML.getReturnXML(bsData, 1);
        } else {
	        try {
	        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
	        } catch (CommonException e) {
	            log.error("多画面合成(马赛克)下发RTVS修改输入流的IP和端口出错：" + rtvsVO.getRTVSResetURL());
	            isErr = true;
	        }
        }
        
        String url = "";
        Document doc = null;
        try {
        	doc = utilXML.StringToXML(upString);
            url = RTVSReset.getReturnURL(doc);
        } catch (CommonException e) {
        	isErr = true;
            log.error("视频URL StringToXML Error: " + e.getMessage());
        }
        
        if (isErr) {
            // 失败
            upString = getReturnXML(url, bsData, 1);
        } else {
	        try {
	        	upString = getReturnXML(url, bsData, 0);
	            utilXML.SendUpXML(upString, bsData);
	            //成功后启动 马赛克到点 发送自动录像删除协议
	            
	        } catch (CommonException e) {
	            log.error("多画面合成(马赛克)信息失败: " + e.getMessage());
	        }
        }
        
        //成功后启动 马赛克到点 发送自动录像删除协议、
        //保存马赛克轮播 持续 时间 到 AutoAnalysisTime.properties  
//        AutoAnalysisTimeQueryConfigFile autoAnalysisTimeOueryConfigFile=new AutoAnalysisTimeQueryConfigFile();
//        int hours=Integer.parseInt(RunTime);
//        Date date=new Date();
//        hours=hours+date.getHours();
//        date.setHours(hours);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 
//        autoAnalysisTimeOueryConfigFile.setStreamRoundInfoQueryStopTime(formatter.format(date));
//        
//		String StopTime = autoAnalysisTimeOueryConfigFile.getStreamRoundInfoQueryStopTime();
//		String[] strDate = StopTime.split("-");
//		Date stopDate = new Date(Integer.parseInt(strDate[0]) - 1900, Integer.parseInt(strDate[1]) - 1, Integer.parseInt(strDate[2]), Integer.parseInt(strDate[3]), Integer.parseInt(strDate[4]), Integer.parseInt(strDate[5]));
//        
//        Timer stopTimer=new Timer();
//        stopTimer.schedule(new MosaicStreamRoundInfoStopTimerTask(), stopDate);
       
        
        
        //by tqy 如果RUNTIME=""，不进行马赛克轮播停止线程，只有视频停止协议下发时候，才进行处理
        
//        AutoAnalysisTimeQueryConfigFile autoAnalysisTimeOueryConfigFile=new AutoAnalysisTimeQueryConfigFile();
//        String StopTime = autoAnalysisTimeOueryConfigFile.getStreamRoundInfoQueryStopTime();
//        
//		String[] strStartDate = StopTime.split(" ");
//		String[] sStartDate = strStartDate[0].split("-");
//		String[] sEndDate = strStartDate[1].split(":");
//		String[] strDate =  new String[6];
//		
//		//YYYY-MM-DD
//		strDate[0]=sStartDate[0];
//		strDate[1]=sStartDate[1];
//		strDate[2]=sStartDate[2];
//		
//		//HH:MM:SS
//		strDate[3]=sEndDate[0];
//		strDate[4]=sEndDate[1];
//		strDate[5]=sEndDate[2];
//		
//		Date stopDate = new Date(Integer.parseInt(strDate[0]) - 1900, Integer.parseInt(strDate[1]) - 1, Integer.parseInt(strDate[2]), Integer.parseInt(strDate[3]), Integer.parseInt(strDate[4]), Integer.parseInt(strDate[5]));
//		//若时间过期，删除马赛克轮播，那么比对，启动新业务，否则不启动定时器
//		if (stopDate.before(new Date())) {
//			Timer stopTimer=new Timer();
//	        stopTimer.schedule(new MosaicStreamRoundInfoStopTimerTask(), stopDate);
//		}
//		
		
        bsData = null;
        this.downString = null;
        utilXML = null;
    }
    
    //获取轮播窗体个数
    @SuppressWarnings({ "deprecation", "unchecked", "unused" })
	private int getParseWindowNum(Document document){
    	int windowNum =1;
    	Element root=document.getRootElement();
    	String tomcatUrl="http://"+coreData.getSysVO().getLocalRedirectIp()+":"+coreData.getSysVO().getTomcatPort()+"/transmit/servlet/transmit";
    	root.setAttributeValue("SrcURL", tomcatUrl);
    	for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
    		Element StreamRoundInfoQuery = iter.next();
    		for(Iterator<Element> ite=StreamRoundInfoQuery.elementIterator();ite.hasNext();){
    			Element RoundStream = ite.next();
    			//Index=RoundStream.attributeValue("Index");
    			//RunTime=RoundStream.attributeValue("RunTime").trim();
    			if(RoundStream.attributeValue("WindowNumber").equals("")){
    				windowNum=1;
    			}
    			else
    			{
    			windowNum=Integer.valueOf(RoundStream.attributeValue("WindowNumber"));
    			}
    			break;
    		}
    	}
    	return windowNum;
    }
    
    @SuppressWarnings("deprecation")
	private List<String> parse(Document document){
//    	private String Index="";
//        
//        private String RunTime="";
//        
//        private String WindowNumber="";
    	
    	List<String> channlList=new ArrayList<String>();
    	Element root=document.getRootElement();
    	String tomcatUrl="http://"+coreData.getSysVO().getLocalRedirectIp()+":"+coreData.getSysVO().getTomcatPort()+"/transmit/servlet/transmit";
    	root.setAttributeValue("SrcURL", tomcatUrl);
    	for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
    		Element StreamRoundInfoQuery = iter.next();
    		for(Iterator<Element> ite=StreamRoundInfoQuery.elementIterator();ite.hasNext();){
    			Element RoundStream = ite.next();
    			Index=RoundStream.attributeValue("Index");
    			RunTime=RoundStream.attributeValue("RunTime").trim();
    			WindowNumber=RoundStream.attributeValue("WindowNumber");
    			for(Iterator<Element> it=RoundStream.elementIterator();it.hasNext();){
    				Element Channel = it.next();
    				String Freq=Channel.attributeValue("Freq");
    				String SymbolRate=Channel.attributeValue("SymbolRate");
    				String QAM=Channel.attributeValue("QAM");
    				String ServiceID=Channel.attributeValue("ServiceID");
    				String VideoPID=Channel.attributeValue("VideoPID");
    				String AudioPID=Channel.attributeValue("AudioPID");
    				//如果是一对一的，那么将获取到通道号，否则，通道号为空
    				//by tqy 2012-05-15
   					Channel.addAttribute("DevIndex", getDevIndex(Freq,ServiceID));
    				channlList.add(Freq+","+SymbolRate+","+QAM+","+ServiceID+","+VideoPID+","+AudioPID);
    			}
    		}
    	}
    	return channlList;
    }
    
    private List<String>  isChannlExist(List<String> list){
    	Statement statement = null;
		Connection conn = null;
		ResultSet rs = null;
		List<String> channlList=new ArrayList<String>();
    	try {
    		conn = DaoSupport.getJDBCConnection();
	    	for(int i=0;i<list.size();i++){
	    		String[] strArr=list.get(i).split(",");
	    		String sql="select * FROM channelremapping where delflag =0 and Freq = "+strArr[0]+" and ServiceID = "+strArr[3]+" and StatusFlag = 1" ;
	    		try {
	    			statement=conn.createStatement();
	    			rs=statement.executeQuery(sql);
	    			boolean flag=true;
	    			while(rs.next()){
	    				flag=false;
	    			}
	    			if(flag){
	    				channlList.add(list.get(i));
	    			}
	    			
				} catch (Exception e) {
					log.error("查询自动录像是否存在错误："+e.getMessage());
				}finally{
					DaoSupport.close(statement);
					DaoSupport.close(rs);
				}
	    	}
    	} catch (Exception e) {
    		log.error("查询自动录像是否存在错误："+e.getMessage());
    	}finally{
    		try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭自动录像查询连接错误："+e.getMessage());
			}
    	}
    	return channlList;
    }
    
    /* 
     *  把数组转换为SetAutoRecordChannelVO对象的list
     *  By: Bian Jiang 2012.3.21
     */
    @SuppressWarnings("static-access")
	private List<SetAutoRecordChannelVO> setAutoRecord(List<String> list){

 //   	Statement statement = null;
//		Connection conn = null;
		//ResultSet rs = null;
		List<SetAutoRecordChannelVO> autoRecordList=new ArrayList<SetAutoRecordChannelVO>();
		SetAutoRecordChannelHandle setAutoRecordChannel = new SetAutoRecordChannelHandle();
		
		try {
//			conn = DaoSupport.getJDBCConnection();
			for(int i=0;i<list.size();i++){
				String[] strArr=list.get(i).split(",");
//				String sql="select DevIndex FROM channelremapping where Freq = "+strArr[0];
				try {
//	    			statement=conn.createStatement();
//	    			rs=statement.executeQuery(sql);
//	    			String devIndex="0";
//	    			while(rs.next()){
//	    				devIndex=rs.getString("DevIndex");
//	    				break;
//	    			}
	    			SetAutoRecordChannelVO vo=new SetAutoRecordChannelVO();
	    			vo.setAction("Set");
//	    			vo.setIndex(Integer.parseInt(devIndex));
	    			vo.setFreq(Integer.parseInt(strArr[0]));
	    			vo.setSymbolRate(Integer.parseInt(strArr[1]));
	    			vo.setQAM(Integer.parseInt(strArr[2].substring(3, strArr[2].length())));
	    			vo.setServiceID(Integer.parseInt(strArr[3]));
	    			vo.setVideoPID(Integer.parseInt(strArr[4]));
	    			vo.setAudioPID(Integer.parseInt(strArr[5]));
	    			try
	    			{
	    				//获取节目名称和HDFLAG
		    			vo = setAutoRecordChannel.getHDFlagByProgram(vo);	
	    				// 分配通道信息
		    			setAutoRecordChannel.GetChannelRemappingbyFreq(vo);
	    			}catch(Exception ex1)
	    			{
	    				log.error(ex1.getMessage());
	    			}
	    			
	    			// 4: 马赛克合成轮播 Add By: Bian Jiang 2012.3.21
	    			vo.setRecordType(4);
	    			autoRecordList.add(vo);
	    			//by tqy 添加到自动录制一对一表中
	    			saveMosaicProgramToMonitor(vo);
	    			
				} catch (Exception e) {
					e.printStackTrace();
					log.error("查询自动录像是否存在错误："+e.getMessage());
				}finally{
					//DaoSupport.close(statement);
					//DaoSupport.close(rs);
				}
			}
			
			/* Disable By: Bian Jiang 2012.3.21
			String sql="SELECT * FROM channelremapping where StatusFlag = 1 ;";
			try {

    			statement=conn.createStatement();
    			rs=statement.executeQuery(sql);
    			while(rs.next()){
    				SetAutoRecordChannelVO vo=new SetAutoRecordChannelVO();
	    			vo.setAction(rs.getString("Action"));
	    			vo.setIndex(rs.getInt("DevIndex"));
	    			vo.setFreq(rs.getInt("Freq"));
	    			vo.setSymbolRate(rs.getInt("SymbolRate"));
	    			vo.setQAM(rs.getInt("qam"));
	    			vo.setServiceID(rs.getInt("ServiceID"));
	    			vo.setVideoPID(rs.getInt("VideoPID"));
	    			vo.setAudioPID(rs.getInt("AudioPID"));
	    			autoRecordList.add(vo);	
    			}

			} catch (Exception e) {
				log.error("查询自动录像是否存在错误："+e.getMessage());
			}finally{
				DaoSupport.close(statement);
				DaoSupport.close(rs);
			}
			*/
			
		} catch (Exception e) {
			log.error("查询自动录像是否存在错误："+e.getMessage());
		}finally{
//    		try {
//				DaoSupport.close(conn);
//			} catch (DaoException e) {
//				log.error("关闭自动录像查询连接错误："+e.getMessage());
//			}
    	}
    	return autoRecordList;
    }
		
   public void saveMosaicProgramToMonitor(SetAutoRecordChannelVO vo ) throws DaoException
   {
	   		SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
	   		//增加判断如果该录像之前已经存在并且状态为录制 则还按照原来通道下发
			 Statement statement = null;
			 ResultSet rs = null;
			 Connection conn = null;
			 try {
				 conn=DaoSupport.getJDBCConnection();
				 String sql="SELECT * FROM channelremapping where delflag =0 and Freq = "+vo.getFreq()+" and ServiceId = "+vo.getServiceID()+" and RecordType = 2 and StatusFlag = 1;";
				 boolean temp=false;
				 int tscindex=0;
				 //添加ias index  Ji Long  2011-08-09
				 int iasindex=0;
				 try {
					 statement=conn.createStatement();
					 rs=statement.executeQuery(sql);
					 if(rs.next()){
						 temp=true;
						 tscindex=rs.getInt("TscIndex");
						 iasindex=rs.getInt("IpmIndex");
					 }
				} catch (Exception e) {
					log.error("查询自动录像状态错误："+e.getMessage());
				}finally{
					DaoSupport.close(statement);
					DaoSupport.close(rs);
				}
				 if(temp){
					 vo.setTscIndex(tscindex);
					 vo.setIpmIndex(iasindex);
				 }else{
					 //TODO 如果是高清节目则平均分配高清节目给TSC 
					 //否则把同一个频点的节目发给同一个tsc
					vo.setTscIndex(handle.getTSCIndex(vo.getHDFlag(),vo.getFreq()));
					
					//ias 通道号 平均分配  一个高清等于5个标清  Ji Long 
					//vo.setIpmIndex(handle.getIASIndex(vo.getHDFlag(),vo.getFreq()));
				 }
				 
				 //statusflag =1 代表有效的节目信息
				 handle.upChannelRemappingIndex(vo);
			 } catch (Exception e) {
				 e.printStackTrace();
			 }finally{
				DaoSupport.close(conn);
			 }
   }
    
	private String createForDownXML(MSGHeadVO bsData, List<SetAutoRecordChannelVO> AutoRecordlist, String action){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"" + bsData.getSrcURL() + "\" Priority=\"" + bsData.getPriority() + "\">\r\n");
        
        strBuff.append("<SetAutoRecordChannel>\r\n");
        strBuff.append("   <Channel Action=\"" + action + "\">\r\n");
        
        for(int i=0; i< AutoRecordlist.size(); i++) 
        {
        	
        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
        	if (vo.getFreq() == 0 ) {
        		continue;
        	}
        	if(vo.getAction() == null || vo.getAction().trim().equals("") || vo.getAction().trim().equals("null")) {
        		vo.setAction("Set");
        	}else{
        		vo.setAction("Del");
        	}
        	if(vo.getQAM() == 0) {
        		vo.setQAM(64);
        	}
        	if(vo.getSymbolRate() == 0) {
        		vo.setSymbolRate(6875);
        	}
            
        	//strBuff.append("		<ChCode Index=\"" + (isSMG?vo.getDevIndex():vo.getIpmIndex()) + "\" DevIndex=\"" + vo.getDevIndex() + "\" TscIndex=\"" + vo.getTscIndex() + "\" Freq=\"" + vo.getFreq() + "\" SymbolRate=\""+ vo.getSymbolRate() +"\" ");
        	
        	strBuff.append(" <ChCode Index=\"" + vo.getDevIndex() + "\" DevIndex=\"" + vo.getDevIndex() + "\" TscIndex=\"" + vo.getTscIndex() + "\" Freq=\"" + vo.getFreq() + "\" SymbolRate=\""+ vo.getSymbolRate() +"\" ");
        	strBuff.append(" QAM=\"QAM" + vo.getQAM() + "\" ServiceID=\""+ vo.getServiceID() +"\" VideoPID=\""+vo.getVideoPID()+"\"");
        	strBuff.append(" AudioPID=\""+vo.getAudioPID()+"\" />\r\n");
            
        }
        strBuff.append("   </Channel>\r\n");
        if(AutoRecordlist.size() == 0) {
        	 strBuff.append("   <Channel Action=\"Del\">\r\n");
        	 strBuff.append("   </Channel>\r\n");
        }
        
        strBuff.append("</SetAutoRecordChannel>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}
	
	
    /**
     * 取得返回的XML信息
     * @param head XML数据对象 
     * @param value 0:成功 1:失败
     * @return XML文本信息
     */
    private String getReturnXML(String url, MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
        }
        strBuf.append("<ReturnInfo> \r\n ");
        strBuf.append("<StreamRoundInfoQuery WindowNumber = \""+WindowNumber+"\"> \r\n ");
        strBuf.append("<RoundStream Index=\""+Index+"\" URL=\""+url+"\"/> \r\n ");
        strBuf.append("</StreamRoundInfoQuery>\r\n ");
        strBuf.append("</ReturnInfo>\r\n");
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
    
	private String createForDownXML(MSGHeadVO bsData, MonitorProgramQueryVO vo){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"" + bsData.getSrcURL() + "\" Priority=\"" + bsData.getPriority() + "\">\r\n");
        
        strBuff.append("<RTVSResetConfig>\r\n");
    	strBuff.append(" <RTVSConfig  Index=\"" + vo.getIndex() + "\" Freq=\"" + vo.getFreq() + "\" ");
    	strBuff.append(" ServiceID=\""+ vo.getServiceID() +"\"");
    	strBuff.append(" RunTime=\""+ RunTime +"\"");
    	strBuff.append(" SMGUdpIP=\"" + vo.getRtvsIP() + "\" SMGUdpPort=\""+ vo.getRtvsPort() +"\" Type=\"" + vo.getStatusFlag() + "\"  />\r\n");
        strBuff.append("</RTVSResetConfig>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}
	
	private void getSmgUrl(List<String> channlList,List<SMGCardInfoVO> smgList){
		if(channlList.size()==0){
			return;
		}
		Statement statement = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn=DaoSupport.getJDBCConnection();
			String sql="";
			for(int i=0;i<channlList.size();i++){
				String[] strArr=channlList.get(i).split(",");
				sql="SELECT DevIndex FROM channelremapping where Freq = "+strArr[0]+" group by DevIndex;";
				try {
					statement=conn.createStatement();
					rs=statement.executeQuery(sql);
					while(rs.next()){
						int DevIndex=rs.getInt("DevIndex");
						CommonUtility.checkSMGChannelIndex(DevIndex, smgList);
					}
				} catch (Exception e) {
					log.info("查询板卡通道错误："+e.getMessage());
				}finally{
					DaoSupport.close(statement);
					DaoSupport.close(rs);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.info("关闭连接错误："+e.getMessage());
			}
		}
	}
	
	private String  getDevIndex(String freq,String ServiceID){
		Statement statement = null;
		Connection conn = null;
		ResultSet rs = null;
		String DevIndex="";
		try {
			conn=DaoSupport.getJDBCConnection();
			String sql="";
				sql="SELECT DevIndex FROM channelremapping where Freq = "+freq+" and ServiceID = "+ServiceID+"  and delflag =0 group by DevIndex;";
				try {
					statement=conn.createStatement();
					rs=statement.executeQuery(sql);
					while(rs.next()){
						DevIndex=rs.getString("DevIndex");
					}
				} catch (Exception e) {
					log.info("查询板卡通道错误："+e.getMessage());
				}finally{
					DaoSupport.close(statement);
					DaoSupport.close(rs);
				}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.info("关闭连接错误："+e.getMessage());
			}
		}
		return DevIndex;
	}
	
	
	
}



















