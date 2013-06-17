package com.bvcom.transmit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.handle.MatrixQueryHandle;
import com.bvcom.transmit.handle.OSDSetHandle;
import com.bvcom.transmit.handle.alarm.AlarmProgramSetHandle;
import com.bvcom.transmit.handle.alarm.AlarmSearchESetHandle;
import com.bvcom.transmit.handle.alarm.AlarmSearchFSetHandle;
import com.bvcom.transmit.handle.alarm.AlarmSearchLSetHandle;
import com.bvcom.transmit.handle.alarm.AlarmSearchPSetHandle;
import com.bvcom.transmit.handle.alarm.AlarmSetHandle;
import com.bvcom.transmit.handle.alarm.LoopAlaInfHandle;
import com.bvcom.transmit.handle.index.GetIndexESetHandle;
import com.bvcom.transmit.handle.si.AutoAnalysisTimeQueryHandle;
import com.bvcom.transmit.handle.si.ChangeQAMQueryHandle;
import com.bvcom.transmit.handle.si.ChannelScanQueryHandle;
import com.bvcom.transmit.handle.si.ConstellationQueryHandle;
import com.bvcom.transmit.handle.si.EPGQueryHandle;
import com.bvcom.transmit.handle.si.GetIndexSetHandle;
import com.bvcom.transmit.handle.si.IndexCompensationSetHandle;
import com.bvcom.transmit.handle.si.MHPQueryHandle;
import com.bvcom.transmit.handle.si.NephogramQueryHandle;
import com.bvcom.transmit.handle.si.SpectrumScanQueryHandle;
import com.bvcom.transmit.handle.smginfo.AgentInfoSet;
import com.bvcom.transmit.handle.smginfo.GetNvrStatus;
import com.bvcom.transmit.handle.smginfo.ICInfoQueryHandle;
import com.bvcom.transmit.handle.smginfo.NvrStatusSet;
import com.bvcom.transmit.handle.smginfo.RebootSet;
import com.bvcom.transmit.handle.video.AlarmTimeSetHandle;
import com.bvcom.transmit.handle.video.ChangeProgramQueryHandle;
import com.bvcom.transmit.handle.video.ManualRecordQueryHandle;
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.handle.video.MosaicConfigHandle;
import com.bvcom.transmit.handle.video.MosaicStreamRoundInfoQuery;
import com.bvcom.transmit.handle.video.NVRHDParamSetHandle;
import com.bvcom.transmit.handle.video.NVRSteamRateSetHandle;
import com.bvcom.transmit.handle.video.NVRTaskRecordDownInquiryHandle;
import com.bvcom.transmit.handle.video.NVRTaskRecordInquiryHandle;
import com.bvcom.transmit.handle.video.NVRVideoHistoryDownInquiryHandle;
import com.bvcom.transmit.handle.video.NVRVideoHistoryInquiryHandle;
import com.bvcom.transmit.handle.video.ProgramPatrolHandle;
import com.bvcom.transmit.handle.video.ProvisionalRecordTaskSetHandle;
import com.bvcom.transmit.handle.video.ReceiveMosaicStreamRoundInfoQuery;
import com.bvcom.transmit.handle.video.RecordCapabilityQuery;
import com.bvcom.transmit.handle.video.RecordParamSetExHandle;
import com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle;
import com.bvcom.transmit.handle.video.StopPlayingVideoHandle;
import com.bvcom.transmit.handle.video.StreamRoundInfoQueryHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.ReadEPGandMHP;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.handle.smginfo.ICInfoChannelEncryptQuery;;



public class TransmitThread extends Thread {
    /**
     * 对转发程序实现并行处理
     * 
     * @version  V1.0
     * @author Bian Jiang
     * Date 2009-12-9
     */

    private static Logger log = Logger.getLogger(TransmitThread.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    /**
     * 
     * @param centerDownStr
     * @param bsData
     */
    public TransmitThread(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
         
        this.bsData = bsData;
    }
    
    public void run() {
        
        String returnstr = null;
        
        // 接受信息保存文件
        this.saveReceXML2File();
        long t1 = System.currentTimeMillis();
//        System.out.println("\n === 查询属性: " + this.bsData.getStatusQueryType() + " ThreadID: " + this.getId() + " ===\n");

        // 协议判别
        try {
            this.transmitMainCtrl();
        } catch (CommonException e) {
        }
        long t2 = System.currentTimeMillis();
//        log.info(this.bsData.getStatusQueryType() + " ThreadID: " + this.getId() + " ===  系统处理时间: " + ((t2-t1)/1000) + "s");
    }
    
    /*
     * 协议判别
     */
    private void transmitMainCtrl() throws CommonException {
        // 查询属性
        String statusQueryType = bsData.getStatusQueryType();
        
        String returnstr = "";
        
        try {
        	 this.downString = this.downString.replaceAll("Index=\"\"", "Index=\"0\"");
        	 this.downString = this.downString.replaceAll("Index=\"-1\"", "Index=\"0\"");
        	 this.downString = this.downString.replaceAll("SymbolRate=\"\"", "SymbolRate=\"6875\"");
        	 this.downString = this.downString.replaceAll("SymbolRate=\"0\"", "SymbolRate=\"6875\"");
        	 this.downString = this.downString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
        	 this.downString = this.downString.replaceAll("QAM=\"QAM\"", "QAM=\"QAM64\"");
        	 this.downString = this.downString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
        	 this.downString = this.downString.replaceAll("QAM=\"16\"", "QAM=\"QAM16\"");
        	 this.downString = this.downString.replaceAll("QAM=\"32\"", "QAM=\"QAM32\"");
        	 this.downString = this.downString.replaceAll("QAM=\"128\"", "QAM=\"QAM128\"");
        	 this.downString = this.downString.replaceAll("QAM=\"256\"", "QAM=\"QAM256\"");
        } catch (Exception ex) {
        	
        }
        
        UtilXML utilXML = new UtilXML();
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
            document.setXMLEncoding("UTF-8");
            this.downString = document.asXML();
        } catch (CommonException e) {
        }
        
        if (statusQueryType.equals("table")) {
            ReadEPGandMHP epg = new ReadEPGandMHP();
            epg.downXML(downString, bsData);
            epg = null;
        } else if(statusQueryType.equals("MHPQuery")) {
        	MHPQueryHandle MHPQuery = new MHPQueryHandle(this.downString, this.bsData);
        	MHPQuery.downXML();
            
        	MHPQuery = null;
        	//TODO 
        	//关注一下
        }  else if(statusQueryType.equals("EPGQuery")) {
        	EPGQueryHandle EPGQuery = new EPGQueryHandle(this.downString, this.bsData);
        	EPGQuery.downXML();
            
        	EPGQuery = null;
        }
        else  if(statusQueryType.equals("ChannelScanQuery")) {
            // 频道扫描
            ChannelScanQueryHandle ChannelScanQuery = new ChannelScanQueryHandle(this.downString, this.bsData);
            ChannelScanQuery.downXML();
            //TODO 关注一下 
            ChannelScanQuery = null;
            
            
        } else if(statusQueryType.equals("AutoAnalysisTimeQuery")) {
            // 数据业务分析时间设置
            AutoAnalysisTimeQueryHandle AutoAnalysis = new AutoAnalysisTimeQueryHandle(this.downString, this.bsData);
            AutoAnalysis.downXML();
            //TODO 关注一下 
            AutoAnalysis = null;
            
            
        } else if(statusQueryType.equals("SpectrumScanQuery")) {
        	// 频谱扫描
        	SpectrumScanQueryHandle SpectrumScanQueryHandle = new SpectrumScanQueryHandle(this.downString, this.bsData);
        	SpectrumScanQueryHandle.downXML();
            
        	SpectrumScanQueryHandle = null;
        }
        else if(statusQueryType.equals("ChangeProgramQuery")) {
            // 手动选台
            ChangeProgramQueryHandle ChangeProgram = new ChangeProgramQueryHandle(this.downString, this.bsData);
            ChangeProgram.downXML();
            
            ChangeProgram = null;
        } else if(statusQueryType.equals("ManualRecordQuery")) {
            // 手动录制
            ManualRecordQueryHandle ManualRecord = new ManualRecordQueryHandle(this.downString, this.bsData);
            ManualRecord.downXML();
            
            ManualRecord = null;
        }else if(statusQueryType.equals("SetAutoRecordChannel")) {
            // 自动录像
        	SetAutoRecordChannelHandle SetAutoRecord = new SetAutoRecordChannelHandle(this.downString, this.bsData);
        	SetAutoRecord.downXML();
            
        	SetAutoRecord = null;
        }else if(statusQueryType.equals("NVRVideoHistoryDownInquiry")) {
            // 历史视频下载
        	NVRVideoHistoryDownInquiryHandle nvrHistroydownRecord = new NVRVideoHistoryDownInquiryHandle(this.downString, this.bsData);
        	nvrHistroydownRecord.downXML();
            
        	nvrHistroydownRecord = null;
        }else if(statusQueryType.equals("NVRVideoHistoryInquiry")) {
            // 历史视频查看
        	NVRVideoHistoryInquiryHandle nvrHistroyRecord = new NVRVideoHistoryInquiryHandle(this.downString, this.bsData);
        	nvrHistroyRecord.downXML();
            
        	nvrHistroyRecord = null;
        } else if(statusQueryType.equals("NVRSteamRateSet")) {
            // 实时视频流率
        	NVRSteamRateSetHandle nvrStream = new NVRSteamRateSetHandle(this.downString, this.bsData);
        	nvrStream.downXML();
            
        	nvrStream = null; 
        } else if(statusQueryType.equals("MatrixQuery")) {
            //矩阵切换
        	MatrixQueryHandle matrixRecord = new MatrixQueryHandle(this.downString, this.bsData);
        	matrixRecord.downXML();
            
        	matrixRecord = null;
        } else if(statusQueryType.equals("OSDSet")) {
            //osd设置
        	OSDSetHandle osdRecord = new OSDSetHandle(this.downString, this.bsData);
        	osdRecord.downXML();
            
        	osdRecord = null;
        } else if(statusQueryType.equals("ChangeQAMQuery")) {
            //QAM设置
        	ChangeQAMQueryHandle QAMRecord = new ChangeQAMQueryHandle(this.downString, this.bsData);
        	QAMRecord.downXML();
            
        	QAMRecord = null;
        } else if(statusQueryType.equals("StreamRoundInfoQuery")&& Pattern.compile("WindowNumber=\"1\"").matcher(this.downString).find()) {
        		 
            //自动轮播
        	StreamRoundInfoQueryHandle streamRoundInfoQuery = new StreamRoundInfoQueryHandle(this.downString, this.bsData);
        	streamRoundInfoQuery.downXML();
            
        	streamRoundInfoQuery = null;
        } else if(statusQueryType.equals("IndexCompensationSet")) {
            //指标补偿
        	IndexCompensationSetHandle indexCompensationSetQuery = new IndexCompensationSetHandle(this.downString, this.bsData);
        	indexCompensationSetQuery.downXML();
            
        	indexCompensationSetQuery = null;
        } else if(statusQueryType.equals("AlarmSearchLSet")) {
            //循切报警查询
        	AlarmSearchLSetHandle alarmSearchLSetQuery = new AlarmSearchLSetHandle(this.downString, this.bsData);
        	alarmSearchLSetQuery.downXML();
            
        	alarmSearchLSetQuery = null;
        } else if(statusQueryType.equals("GetIndexSet")) {
            //性能指标查询
        	GetIndexSetHandle GetIndexSetQuery = new GetIndexSetHandle(this.downString, this.bsData);
        	GetIndexSetQuery.downXML();
            
        	GetIndexSetQuery = null;
        } else if(statusQueryType.equals("GetNvrStatus_BAK")) {
            //1:原通道状态查询、废弃
        	/*
        	GetNvrStatusHandle getNvrStatusQuery = new GetNvrStatusHandle(this.downString, this.bsData);
        	getNvrStatusQuery.downXML();
            
        	getNvrStatusQuery = null;
        	*/
        } else if(statusQueryType.equals("GetIndexESet")) {
            //运行环境指标查询
        	GetIndexESetHandle getIndexESetQuery = new GetIndexESetHandle(this.downString, this.bsData);
        	getIndexESetQuery.downXML();
            
        	getIndexESetQuery = null;
        } else if(statusQueryType.equals("AlarmSearchPSet")) {
            // 节目报警查询
            AlarmSearchPSetHandle AlarmSearchPSet = new AlarmSearchPSetHandle(this.downString, this.bsData);
            AlarmSearchPSet.downXML();
            
            AlarmSearchPSet = null;
        }
        else if(statusQueryType.equals("AlarmSearchESet")) {
            //报警上报指标查询
        	AlarmSearchESetHandle alarmSearchESetQuery = new AlarmSearchESetHandle(this.downString, this.bsData);
        	alarmSearchESetQuery.downXML();
            
        	alarmSearchESetQuery = null;
        }
        else if(statusQueryType.equals("AlarmSearchFSet")) {
            //报警上报频率查询
        	AlarmSearchFSetHandle alarmSearchFSetQuery = new AlarmSearchFSetHandle(this.downString, this.bsData);
        	alarmSearchFSetQuery.downXML();
            
        	alarmSearchFSetQuery = null;
        }
        
        else if(statusQueryType.equals("ProvisionalRecordTaskSet")) {
            // 任务录像设置
            ProvisionalRecordTaskSetHandle RecordTaskSet = new ProvisionalRecordTaskSetHandle(this.downString, this.bsData);
            RecordTaskSet.downXML();
            
            RecordTaskSet = null;
        } else if(statusQueryType.equals("NVRTaskRecordInquiry")) {
            // 任务录像查看
            NVRTaskRecordInquiryHandle TaskRecordInquiry = new NVRTaskRecordInquiryHandle(this.downString, this.bsData);
            TaskRecordInquiry.downXML();
            
            TaskRecordInquiry = null;
        } else if(statusQueryType.equals("NVRTaskRecordDownInquiry")) {
            // 任务录像下载
            NVRTaskRecordDownInquiryHandle TaskRecordInquiry = new NVRTaskRecordDownInquiryHandle(this.downString, this.bsData);
            TaskRecordInquiry.downXML();
            
            TaskRecordInquiry = null;
        } else if(statusQueryType.equals("RecordParamSet") || 
        		statusQueryType.equals("HDDefAudioParamSet") ||
        		statusQueryType.equals("AudioParamSet") ) {
        	// 视频转码录像默认参数设置
        	// 高清音频默认参数设置
        	// 音频参数设置
        	NVRHDParamSetHandle nvrHDParamSetHandle = new NVRHDParamSetHandle(this.downString, this.bsData);
        	nvrHDParamSetHandle.downXML();
        	nvrHDParamSetHandle = null;

        } else if (statusQueryType.equals("RecordCapabilityQuery")) {
        	// 录像路数查询
        	RecordCapabilityQuery RecordCapabilityQuery = new RecordCapabilityQuery(this.downString, this.bsData);
        	RecordCapabilityQuery.downXML();
        	
        	RecordCapabilityQuery = null;
        	
        } else if(statusQueryType.equals("AlarmTimeSet")) {
            // 运行图
            AlarmTimeSetHandle AlarmTimeSet = new AlarmTimeSetHandle(this.downString, this.bsData);
            AlarmTimeSet.downXML();
            
            AlarmTimeSet = null;
        } else if(statusQueryType.equals("AlarmThresholdSet") ||
                  statusQueryType.equals("AlarmSwitchSet") ||
                  statusQueryType.equals("AlarmTypeSet") ||
                  statusQueryType.equals("ClearAlarmState")) {
            // 报警门限, 开关 ,方式 和报警状态清除
            AlarmSetHandle AlarmSet = new AlarmSetHandle(this.downString, this.bsData);
            AlarmSet.downXML();
            AlarmSet = null;
        } else if(statusQueryType.equals("AlarmProgramSwitchSet") ||
                  statusQueryType.equals("AlarmProgramThresholdSet")) {
            // 报警门限和开关节目相关
            AlarmProgramSetHandle AlarmProgramSet = new AlarmProgramSetHandle(this.downString, this.bsData);
            AlarmProgramSet.downXML();
            
            AlarmProgramSet = null;
        } else if(statusQueryType.equals("LoopAlaInf")) {
            // 循切报警设置
            LoopAlaInfHandle LoopAlaInf = new LoopAlaInfHandle(this.downString, this.bsData);
            LoopAlaInf.downXML();
            
            LoopAlaInf = null;
        } else if(statusQueryType.equals("ProgramPatrol")) {
        	// 轮巡监测设置 广州监测 Add By Bian Jiang 2010.9.15
        	
        	ProgramPatrolHandle programPatrol = new ProgramPatrolHandle(this.downString, this.bsData);
        	programPatrol.downXML();
            
        	programPatrol = null;
        } else if(statusQueryType.equals("MonitorProgramQuery")) {
        	// 实时视频监看 广州监测 Add By Bian Jiang 2010.9.15
        	MonitorProgramQueryHandle monitorProgramQuery = new MonitorProgramQueryHandle(this.downString, this.bsData);
        	monitorProgramQuery.downXML();
            
        	monitorProgramQuery = null;
        	//TODO 增加码率判断
        }  else if(statusQueryType.equals("RecordParamSetEx")) {
        	// 录像码率设置 广州监测 Add By Bian Jiang 2010.9.23
        	RecordParamSetExHandle RecordParamSetExHandle = new RecordParamSetExHandle(this.downString, this.bsData);
        	RecordParamSetExHandle.downXML();
            
        	RecordParamSetExHandle = null;
        } else if(statusQueryType.equals("ConstellationQuery")) {
        	// 星座图  广州监测 Add By Bian Jiang 2010.10.12
        	ConstellationQueryHandle ConstellationQueryHandle = new ConstellationQueryHandle(this.downString, this.bsData);
        	ConstellationQueryHandle.downXML();
        	
        	ConstellationQueryHandle = null;
        } else if(statusQueryType.equals("NephogramQuery")) {
        	// Web 2.0 星座图 Add By Bian Jiang 2011.1.7
        	NephogramQueryHandle nephogramQueryHandle = new NephogramQueryHandle(this.downString, this.bsData);
        	nephogramQueryHandle.downXML();
        	
        	nephogramQueryHandle = null;
        } else if(statusQueryType.equals("MosaicConfig")) {
        	// 多画面合成(马赛克) 广州监测 Add By Bian Jiang 2010.12.9
        	MosaicConfigHandle mosaicConfigHandle = new MosaicConfigHandle(this.downString, this.bsData);
        	mosaicConfigHandle.downXML();
        	
        	mosaicConfigHandle = null;
        }else if(statusQueryType.equals("StopPlayingVideo")){
        	//视频播放停止 Add By tqy 2011-07-25
        	//如果是视频停播协议则转发给RTVM、IAS停止节目轮播上报
        	StopPlayingVideoHandle stopPlayingVideoHandle =new StopPlayingVideoHandle(this.downString, this.bsData);
        	stopPlayingVideoHandle.downXML();
        	stopPlayingVideoHandle =null;
        }else if(statusQueryType.equals("ICInfoQuery")){	
        	//小卡信息查询 Add By Ji Long 2011-07-28,
        	//BY TQY MODIFIED从数据库SMG_CARD_INFO中读取信息
        	ICInfoQueryHandle iCInfoQueryHandle=new ICInfoQueryHandle(this.downString, this.bsData);
        	iCInfoQueryHandle.downXML();
        	iCInfoQueryHandle =null;
        }else if(statusQueryType.equals("GetNvrStatus")){
        	//板卡通道查看 Add By  Ji Long 2011-08-01
        	//modify by tqy 2012-10-18
        	GetNvrStatus getNvrStatus=new GetNvrStatus(this.downString, this.bsData);
        	getNvrStatus.downXML();
        	getNvrStatus =null;
        }else if(statusQueryType.equals("NvrStatusSet")){	
        	//板卡通道设置 Add By Ji Long 2011-07-28 
        	NvrStatusSet nvrStatusSet=new NvrStatusSet(this.downString, this.bsData);
        	nvrStatusSet.downXML();
        	nvrStatusSet =null;
        }else if(statusQueryType.equals("StreamRoundInfoQuery")&&bsData.getVersion().equals("2.5")){
        	//马赛克合成轮播:added by tqy  
        	//马赛克过期删除相关的节目信息 
        	MosaicStreamRoundInfoQuery mosaicStreamRoundInfoQuery=new MosaicStreamRoundInfoQuery(this.downString, this.bsData);
        	mosaicStreamRoundInfoQuery.downXML();
        	mosaicStreamRoundInfoQuery=null;
        	
        }else if(statusQueryType.equals("AgentInfoSet")){
        	//前端属性配置 Add By Ji Long 2011-08-01
        	AgentInfoSet agentInfoSet=new AgentInfoSet(this.downString, this.bsData);
        	agentInfoSet.downXML();
        	agentInfoSet=null;
        	
        }else if(statusQueryType.equals("RebootSet")){
        	//前端重启 Add By Ji Long 2011-08-01
        	RebootSet rebootSet=new RebootSet(this.downString, this.bsData);
        	rebootSet.downXML();
        	rebootSet=null;
        	
        }else if(statusQueryType.equals("Return")){
        	//V2.5接口协议：增加ICInfoChannelEncryptQuery的处理（解析，根据小卡卡号存数据库）
        	String str= bsData.getReturn_Type();
        	if(str.equals("ICInfoChannelEncryptQuery")){
        		//ICInfoChannelEncryptParase(document);
        	}
        	else
        	{
	        	//收到多画返回 马赛克下次轮播节目  Add By  Ji Long  2011-08-01
	        	ReceiveMosaicStreamRoundInfoQuery  receiveMosaicStreamRoundInfoQuery=new ReceiveMosaicStreamRoundInfoQuery(this.downString, this.bsData);
	        	receiveMosaicStreamRoundInfoQuery.downXML();
	        	receiveMosaicStreamRoundInfoQuery=null;
        	}
        	
        }else if(statusQueryType.equals("ICInfoChannelEncryptQuery")){
        	//2012-10-18 V2.5 by tqy  小卡节目授权查询
        	ICInfoChannelEncryptQuery ICInfoChannelEncryptQueryHandle= new ICInfoChannelEncryptQuery(this.downString,this.bsData);
        	ICInfoChannelEncryptQueryHandle.downXML();
        	ICInfoChannelEncryptQueryHandle=null;
        	
        }else if(statusQueryType.equals("GetNvrIndexTotal")){
        	System.out.println(this.downString);
        }else {
            // 其他信息都返回成功
            returnstr = utilXML.getReturnXML(this.bsData, 0);
            try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
            }
            utilXML = null;
        }
    }
    
    
    /**
     * 保存接收到的XML数据
     *
     */
    private void saveReceXML2File() {
        
        // 接受信息保存文件Start
        //String receFilePath = pData.getReceFilePath();
        // List 从配置文件取得文件路径
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        
        String receFilePath = sysVO.getReceFilePath();
        // 创建错误XML信息存放目录
        CommonUtility.CreateFolder(receFilePath);
        // 接受信息保存文件
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss_SS"); 
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd"); 
        Date desData = new Date();
        
        // 目录
        String desDataStr = formatDate.format(desData);
        // 文件名
        String desDataTimeStr = formatter.format(desData);
        
        // 创建目录
        String fileFlod = receFilePath + "\\";
        
        String[] dataFold = desDataStr.split("/");
        // 创建目录
        for(int i=0; i<dataFold.length; i++) {
        	fileFlod += dataFold[i] + "/";
        	CommonUtility.CreateFolder(fileFlod);
        }
        
        String fileName =  fileFlod + "\\" + desDataTimeStr + "_"+ bsData.getStatusQueryType() + "_Rece.xml";
        log.info("ThreadID: " + this.getId() + " 存放接受的XML存放路径：" + fileName);
        CommonUtility.WriteFile(downString, fileName);
    }
    
    
    
   
}
