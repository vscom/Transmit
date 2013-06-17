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
     * ��ת������ʵ�ֲ��д���
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
        
        // ������Ϣ�����ļ�
        this.saveReceXML2File();
        long t1 = System.currentTimeMillis();
//        System.out.println("\n === ��ѯ����: " + this.bsData.getStatusQueryType() + " ThreadID: " + this.getId() + " ===\n");

        // Э���б�
        try {
            this.transmitMainCtrl();
        } catch (CommonException e) {
        }
        long t2 = System.currentTimeMillis();
//        log.info(this.bsData.getStatusQueryType() + " ThreadID: " + this.getId() + " ===  ϵͳ����ʱ��: " + ((t2-t1)/1000) + "s");
    }
    
    /*
     * Э���б�
     */
    private void transmitMainCtrl() throws CommonException {
        // ��ѯ����
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
        	//��עһ��
        }  else if(statusQueryType.equals("EPGQuery")) {
        	EPGQueryHandle EPGQuery = new EPGQueryHandle(this.downString, this.bsData);
        	EPGQuery.downXML();
            
        	EPGQuery = null;
        }
        else  if(statusQueryType.equals("ChannelScanQuery")) {
            // Ƶ��ɨ��
            ChannelScanQueryHandle ChannelScanQuery = new ChannelScanQueryHandle(this.downString, this.bsData);
            ChannelScanQuery.downXML();
            //TODO ��עһ�� 
            ChannelScanQuery = null;
            
            
        } else if(statusQueryType.equals("AutoAnalysisTimeQuery")) {
            // ����ҵ�����ʱ������
            AutoAnalysisTimeQueryHandle AutoAnalysis = new AutoAnalysisTimeQueryHandle(this.downString, this.bsData);
            AutoAnalysis.downXML();
            //TODO ��עһ�� 
            AutoAnalysis = null;
            
            
        } else if(statusQueryType.equals("SpectrumScanQuery")) {
        	// Ƶ��ɨ��
        	SpectrumScanQueryHandle SpectrumScanQueryHandle = new SpectrumScanQueryHandle(this.downString, this.bsData);
        	SpectrumScanQueryHandle.downXML();
            
        	SpectrumScanQueryHandle = null;
        }
        else if(statusQueryType.equals("ChangeProgramQuery")) {
            // �ֶ�ѡ̨
            ChangeProgramQueryHandle ChangeProgram = new ChangeProgramQueryHandle(this.downString, this.bsData);
            ChangeProgram.downXML();
            
            ChangeProgram = null;
        } else if(statusQueryType.equals("ManualRecordQuery")) {
            // �ֶ�¼��
            ManualRecordQueryHandle ManualRecord = new ManualRecordQueryHandle(this.downString, this.bsData);
            ManualRecord.downXML();
            
            ManualRecord = null;
        }else if(statusQueryType.equals("SetAutoRecordChannel")) {
            // �Զ�¼��
        	SetAutoRecordChannelHandle SetAutoRecord = new SetAutoRecordChannelHandle(this.downString, this.bsData);
        	SetAutoRecord.downXML();
            
        	SetAutoRecord = null;
        }else if(statusQueryType.equals("NVRVideoHistoryDownInquiry")) {
            // ��ʷ��Ƶ����
        	NVRVideoHistoryDownInquiryHandle nvrHistroydownRecord = new NVRVideoHistoryDownInquiryHandle(this.downString, this.bsData);
        	nvrHistroydownRecord.downXML();
            
        	nvrHistroydownRecord = null;
        }else if(statusQueryType.equals("NVRVideoHistoryInquiry")) {
            // ��ʷ��Ƶ�鿴
        	NVRVideoHistoryInquiryHandle nvrHistroyRecord = new NVRVideoHistoryInquiryHandle(this.downString, this.bsData);
        	nvrHistroyRecord.downXML();
            
        	nvrHistroyRecord = null;
        } else if(statusQueryType.equals("NVRSteamRateSet")) {
            // ʵʱ��Ƶ����
        	NVRSteamRateSetHandle nvrStream = new NVRSteamRateSetHandle(this.downString, this.bsData);
        	nvrStream.downXML();
            
        	nvrStream = null; 
        } else if(statusQueryType.equals("MatrixQuery")) {
            //�����л�
        	MatrixQueryHandle matrixRecord = new MatrixQueryHandle(this.downString, this.bsData);
        	matrixRecord.downXML();
            
        	matrixRecord = null;
        } else if(statusQueryType.equals("OSDSet")) {
            //osd����
        	OSDSetHandle osdRecord = new OSDSetHandle(this.downString, this.bsData);
        	osdRecord.downXML();
            
        	osdRecord = null;
        } else if(statusQueryType.equals("ChangeQAMQuery")) {
            //QAM����
        	ChangeQAMQueryHandle QAMRecord = new ChangeQAMQueryHandle(this.downString, this.bsData);
        	QAMRecord.downXML();
            
        	QAMRecord = null;
        } else if(statusQueryType.equals("StreamRoundInfoQuery")&& Pattern.compile("WindowNumber=\"1\"").matcher(this.downString).find()) {
        		 
            //�Զ��ֲ�
        	StreamRoundInfoQueryHandle streamRoundInfoQuery = new StreamRoundInfoQueryHandle(this.downString, this.bsData);
        	streamRoundInfoQuery.downXML();
            
        	streamRoundInfoQuery = null;
        } else if(statusQueryType.equals("IndexCompensationSet")) {
            //ָ�겹��
        	IndexCompensationSetHandle indexCompensationSetQuery = new IndexCompensationSetHandle(this.downString, this.bsData);
        	indexCompensationSetQuery.downXML();
            
        	indexCompensationSetQuery = null;
        } else if(statusQueryType.equals("AlarmSearchLSet")) {
            //ѭ�б�����ѯ
        	AlarmSearchLSetHandle alarmSearchLSetQuery = new AlarmSearchLSetHandle(this.downString, this.bsData);
        	alarmSearchLSetQuery.downXML();
            
        	alarmSearchLSetQuery = null;
        } else if(statusQueryType.equals("GetIndexSet")) {
            //����ָ���ѯ
        	GetIndexSetHandle GetIndexSetQuery = new GetIndexSetHandle(this.downString, this.bsData);
        	GetIndexSetQuery.downXML();
            
        	GetIndexSetQuery = null;
        } else if(statusQueryType.equals("GetNvrStatus_BAK")) {
            //1:ԭͨ��״̬��ѯ������
        	/*
        	GetNvrStatusHandle getNvrStatusQuery = new GetNvrStatusHandle(this.downString, this.bsData);
        	getNvrStatusQuery.downXML();
            
        	getNvrStatusQuery = null;
        	*/
        } else if(statusQueryType.equals("GetIndexESet")) {
            //���л���ָ���ѯ
        	GetIndexESetHandle getIndexESetQuery = new GetIndexESetHandle(this.downString, this.bsData);
        	getIndexESetQuery.downXML();
            
        	getIndexESetQuery = null;
        } else if(statusQueryType.equals("AlarmSearchPSet")) {
            // ��Ŀ������ѯ
            AlarmSearchPSetHandle AlarmSearchPSet = new AlarmSearchPSetHandle(this.downString, this.bsData);
            AlarmSearchPSet.downXML();
            
            AlarmSearchPSet = null;
        }
        else if(statusQueryType.equals("AlarmSearchESet")) {
            //�����ϱ�ָ���ѯ
        	AlarmSearchESetHandle alarmSearchESetQuery = new AlarmSearchESetHandle(this.downString, this.bsData);
        	alarmSearchESetQuery.downXML();
            
        	alarmSearchESetQuery = null;
        }
        else if(statusQueryType.equals("AlarmSearchFSet")) {
            //�����ϱ�Ƶ�ʲ�ѯ
        	AlarmSearchFSetHandle alarmSearchFSetQuery = new AlarmSearchFSetHandle(this.downString, this.bsData);
        	alarmSearchFSetQuery.downXML();
            
        	alarmSearchFSetQuery = null;
        }
        
        else if(statusQueryType.equals("ProvisionalRecordTaskSet")) {
            // ����¼������
            ProvisionalRecordTaskSetHandle RecordTaskSet = new ProvisionalRecordTaskSetHandle(this.downString, this.bsData);
            RecordTaskSet.downXML();
            
            RecordTaskSet = null;
        } else if(statusQueryType.equals("NVRTaskRecordInquiry")) {
            // ����¼��鿴
            NVRTaskRecordInquiryHandle TaskRecordInquiry = new NVRTaskRecordInquiryHandle(this.downString, this.bsData);
            TaskRecordInquiry.downXML();
            
            TaskRecordInquiry = null;
        } else if(statusQueryType.equals("NVRTaskRecordDownInquiry")) {
            // ����¼������
            NVRTaskRecordDownInquiryHandle TaskRecordInquiry = new NVRTaskRecordDownInquiryHandle(this.downString, this.bsData);
            TaskRecordInquiry.downXML();
            
            TaskRecordInquiry = null;
        } else if(statusQueryType.equals("RecordParamSet") || 
        		statusQueryType.equals("HDDefAudioParamSet") ||
        		statusQueryType.equals("AudioParamSet") ) {
        	// ��Ƶת��¼��Ĭ�ϲ�������
        	// ������ƵĬ�ϲ�������
        	// ��Ƶ��������
        	NVRHDParamSetHandle nvrHDParamSetHandle = new NVRHDParamSetHandle(this.downString, this.bsData);
        	nvrHDParamSetHandle.downXML();
        	nvrHDParamSetHandle = null;

        } else if (statusQueryType.equals("RecordCapabilityQuery")) {
        	// ¼��·����ѯ
        	RecordCapabilityQuery RecordCapabilityQuery = new RecordCapabilityQuery(this.downString, this.bsData);
        	RecordCapabilityQuery.downXML();
        	
        	RecordCapabilityQuery = null;
        	
        } else if(statusQueryType.equals("AlarmTimeSet")) {
            // ����ͼ
            AlarmTimeSetHandle AlarmTimeSet = new AlarmTimeSetHandle(this.downString, this.bsData);
            AlarmTimeSet.downXML();
            
            AlarmTimeSet = null;
        } else if(statusQueryType.equals("AlarmThresholdSet") ||
                  statusQueryType.equals("AlarmSwitchSet") ||
                  statusQueryType.equals("AlarmTypeSet") ||
                  statusQueryType.equals("ClearAlarmState")) {
            // ��������, ���� ,��ʽ �ͱ���״̬���
            AlarmSetHandle AlarmSet = new AlarmSetHandle(this.downString, this.bsData);
            AlarmSet.downXML();
            AlarmSet = null;
        } else if(statusQueryType.equals("AlarmProgramSwitchSet") ||
                  statusQueryType.equals("AlarmProgramThresholdSet")) {
            // �������޺Ϳ��ؽ�Ŀ���
            AlarmProgramSetHandle AlarmProgramSet = new AlarmProgramSetHandle(this.downString, this.bsData);
            AlarmProgramSet.downXML();
            
            AlarmProgramSet = null;
        } else if(statusQueryType.equals("LoopAlaInf")) {
            // ѭ�б�������
            LoopAlaInfHandle LoopAlaInf = new LoopAlaInfHandle(this.downString, this.bsData);
            LoopAlaInf.downXML();
            
            LoopAlaInf = null;
        } else if(statusQueryType.equals("ProgramPatrol")) {
        	// ��Ѳ������� ���ݼ�� Add By Bian Jiang 2010.9.15
        	
        	ProgramPatrolHandle programPatrol = new ProgramPatrolHandle(this.downString, this.bsData);
        	programPatrol.downXML();
            
        	programPatrol = null;
        } else if(statusQueryType.equals("MonitorProgramQuery")) {
        	// ʵʱ��Ƶ�࿴ ���ݼ�� Add By Bian Jiang 2010.9.15
        	MonitorProgramQueryHandle monitorProgramQuery = new MonitorProgramQueryHandle(this.downString, this.bsData);
        	monitorProgramQuery.downXML();
            
        	monitorProgramQuery = null;
        	//TODO ���������ж�
        }  else if(statusQueryType.equals("RecordParamSetEx")) {
        	// ¼���������� ���ݼ�� Add By Bian Jiang 2010.9.23
        	RecordParamSetExHandle RecordParamSetExHandle = new RecordParamSetExHandle(this.downString, this.bsData);
        	RecordParamSetExHandle.downXML();
            
        	RecordParamSetExHandle = null;
        } else if(statusQueryType.equals("ConstellationQuery")) {
        	// ����ͼ  ���ݼ�� Add By Bian Jiang 2010.10.12
        	ConstellationQueryHandle ConstellationQueryHandle = new ConstellationQueryHandle(this.downString, this.bsData);
        	ConstellationQueryHandle.downXML();
        	
        	ConstellationQueryHandle = null;
        } else if(statusQueryType.equals("NephogramQuery")) {
        	// Web 2.0 ����ͼ Add By Bian Jiang 2011.1.7
        	NephogramQueryHandle nephogramQueryHandle = new NephogramQueryHandle(this.downString, this.bsData);
        	nephogramQueryHandle.downXML();
        	
        	nephogramQueryHandle = null;
        } else if(statusQueryType.equals("MosaicConfig")) {
        	// �໭��ϳ�(������) ���ݼ�� Add By Bian Jiang 2010.12.9
        	MosaicConfigHandle mosaicConfigHandle = new MosaicConfigHandle(this.downString, this.bsData);
        	mosaicConfigHandle.downXML();
        	
        	mosaicConfigHandle = null;
        }else if(statusQueryType.equals("StopPlayingVideo")){
        	//��Ƶ����ֹͣ Add By tqy 2011-07-25
        	//�������Ƶͣ��Э����ת����RTVM��IASֹͣ��Ŀ�ֲ��ϱ�
        	StopPlayingVideoHandle stopPlayingVideoHandle =new StopPlayingVideoHandle(this.downString, this.bsData);
        	stopPlayingVideoHandle.downXML();
        	stopPlayingVideoHandle =null;
        }else if(statusQueryType.equals("ICInfoQuery")){	
        	//С����Ϣ��ѯ Add By Ji Long 2011-07-28,
        	//BY TQY MODIFIED�����ݿ�SMG_CARD_INFO�ж�ȡ��Ϣ
        	ICInfoQueryHandle iCInfoQueryHandle=new ICInfoQueryHandle(this.downString, this.bsData);
        	iCInfoQueryHandle.downXML();
        	iCInfoQueryHandle =null;
        }else if(statusQueryType.equals("GetNvrStatus")){
        	//�忨ͨ���鿴 Add By  Ji Long 2011-08-01
        	//modify by tqy 2012-10-18
        	GetNvrStatus getNvrStatus=new GetNvrStatus(this.downString, this.bsData);
        	getNvrStatus.downXML();
        	getNvrStatus =null;
        }else if(statusQueryType.equals("NvrStatusSet")){	
        	//�忨ͨ������ Add By Ji Long 2011-07-28 
        	NvrStatusSet nvrStatusSet=new NvrStatusSet(this.downString, this.bsData);
        	nvrStatusSet.downXML();
        	nvrStatusSet =null;
        }else if(statusQueryType.equals("StreamRoundInfoQuery")&&bsData.getVersion().equals("2.5")){
        	//�����˺ϳ��ֲ�:added by tqy  
        	//�����˹���ɾ����صĽ�Ŀ��Ϣ 
        	MosaicStreamRoundInfoQuery mosaicStreamRoundInfoQuery=new MosaicStreamRoundInfoQuery(this.downString, this.bsData);
        	mosaicStreamRoundInfoQuery.downXML();
        	mosaicStreamRoundInfoQuery=null;
        	
        }else if(statusQueryType.equals("AgentInfoSet")){
        	//ǰ���������� Add By Ji Long 2011-08-01
        	AgentInfoSet agentInfoSet=new AgentInfoSet(this.downString, this.bsData);
        	agentInfoSet.downXML();
        	agentInfoSet=null;
        	
        }else if(statusQueryType.equals("RebootSet")){
        	//ǰ������ Add By Ji Long 2011-08-01
        	RebootSet rebootSet=new RebootSet(this.downString, this.bsData);
        	rebootSet.downXML();
        	rebootSet=null;
        	
        }else if(statusQueryType.equals("Return")){
        	//V2.5�ӿ�Э�飺����ICInfoChannelEncryptQuery�Ĵ�������������С�����Ŵ����ݿ⣩
        	String str= bsData.getReturn_Type();
        	if(str.equals("ICInfoChannelEncryptQuery")){
        		//ICInfoChannelEncryptParase(document);
        	}
        	else
        	{
	        	//�յ��໭���� �������´��ֲ���Ŀ  Add By  Ji Long  2011-08-01
	        	ReceiveMosaicStreamRoundInfoQuery  receiveMosaicStreamRoundInfoQuery=new ReceiveMosaicStreamRoundInfoQuery(this.downString, this.bsData);
	        	receiveMosaicStreamRoundInfoQuery.downXML();
	        	receiveMosaicStreamRoundInfoQuery=null;
        	}
        	
        }else if(statusQueryType.equals("ICInfoChannelEncryptQuery")){
        	//2012-10-18 V2.5 by tqy  С����Ŀ��Ȩ��ѯ
        	ICInfoChannelEncryptQuery ICInfoChannelEncryptQueryHandle= new ICInfoChannelEncryptQuery(this.downString,this.bsData);
        	ICInfoChannelEncryptQueryHandle.downXML();
        	ICInfoChannelEncryptQueryHandle=null;
        	
        }else if(statusQueryType.equals("GetNvrIndexTotal")){
        	System.out.println(this.downString);
        }else {
            // ������Ϣ�����سɹ�
            returnstr = utilXML.getReturnXML(this.bsData, 0);
            try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
            }
            utilXML = null;
        }
    }
    
    
    /**
     * ������յ���XML����
     *
     */
    private void saveReceXML2File() {
        
        // ������Ϣ�����ļ�Start
        //String receFilePath = pData.getReceFilePath();
        // List �������ļ�ȡ���ļ�·��
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        
        String receFilePath = sysVO.getReceFilePath();
        // ��������XML��Ϣ���Ŀ¼
        CommonUtility.CreateFolder(receFilePath);
        // ������Ϣ�����ļ�
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss_SS"); 
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd"); 
        Date desData = new Date();
        
        // Ŀ¼
        String desDataStr = formatDate.format(desData);
        // �ļ���
        String desDataTimeStr = formatter.format(desData);
        
        // ����Ŀ¼
        String fileFlod = receFilePath + "\\";
        
        String[] dataFold = desDataStr.split("/");
        // ����Ŀ¼
        for(int i=0; i<dataFold.length; i++) {
        	fileFlod += dataFold[i] + "/";
        	CommonUtility.CreateFolder(fileFlod);
        }
        
        String fileName =  fileFlod + "\\" + desDataTimeStr + "_"+ bsData.getStatusQueryType() + "_Rece.xml";
        log.info("ThreadID: " + this.getId() + " ��Ž��ܵ�XML���·����" + fileName);
        CommonUtility.WriteFile(downString, fileName);
    }
    
    
    
   
}
