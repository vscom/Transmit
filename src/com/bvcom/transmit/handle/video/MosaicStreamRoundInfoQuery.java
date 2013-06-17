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
     * 1. ����xml��ȡ��Ŀ��Ϣ
     * 2. �жϸý�Ŀ�Ƿ��Ѿ�����Ϊһ��һ
     * 3. �Ѳ��ǵ��·��Զ�¼����忨
     * 4. ���½�Ŀӳ���recordType = 4
     * 4. �·��໭
     * 5. �·�RTVM
     * 6. ����ƽ̨
     * TODO �����˹���ɾ����صĽ�Ŀ��Ϣ 
     * Ŀǰֻ�����忨�·�Del��� 
     * ��Ҫɾ��recordType = 4�Ľ�Ŀ
     * ����Ҫ������Щ��Ϣ�·�Set����
     * By: Bian Jiang 2012.3.21
     * @throws DaoException 
     */
    @SuppressWarnings({ "deprecation", "unchecked", "static-access" })
	public void downXML() throws DaoException {
    	// ��������
        String upString = "";
       
        boolean isErr = false;
        
    	Document document=null;
    	@SuppressWarnings("unused")
		Document iasdoc =null;
    	
    	try {
			document=utilXML.StringToXML(downString);
			iasdoc = utilXML.StringToXML(downString);
			
		} catch (CommonException e) {
			log.error("Stringת��xml����"+e.getMessage());
			isErr = true;
		}
		
		//��ȡƽ̨�·����ֲ�������
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
		
		//�Ƚ��ֲ��Ľ�Ŀɾ��������ƽ̨û�е��ֹͣ��ťʱ����Ҫ������ֲ��Ľ�Ŀ��Ϣ��
		//����һ��һʵʱ��Ƶ���ҵ������״̬recordtype=4
    	// ȡ����������صĽ�Ŀ��Ϣ
		StopPlayingVideoHandle stopPlayingVideoHandle =new StopPlayingVideoHandle();
		@SuppressWarnings("unused")
		List<SetAutoRecordChannelVO> voList = stopPlayingVideoHandle.getProgramInfoByIndex();
		//ͬʱ����һ��һ��Ŀ����recordtype=4 �޸�Ϊ��ʼ״̬
		stopPlayingVideoHandle.updateMosaicChannelMapping(voList);
		
		
    	List<String> channlLists=new ArrayList<String>();
        SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
        List SMGSendList = new ArrayList();//SMG���б���Ϣ
		List channelSendList = new ArrayList();//Channel Index List
		List<SetAutoRecordChannelVO> autoRecordList = new ArrayList();
		
    	//��������Ŀ��Ϣ
    	channlLists=parse(document);
    	List<String> channlList=new ArrayList<String>();
    	//��ŷ�һ��һ�Ľ�Ŀ��Ϣ����Ŀ�֣�һ��һ����һ��һ��
    	channlList = isChannlExist(channlLists);
    	//���channlList.size() ������0
    	//��ô�Ѽ����еĽ�Ŀ��Ϣ��װΪ�Զ�¼���·����忨
    	if(channlList.size()!=0){
    		//�ѽ�Ŀ��װ���Զ�¼���·����忨
    		 autoRecordList = setAutoRecord(channlList);
    	     int channel = 0;
    	     for(int i=0;i<autoRecordList.size();i++){   
    	    	 SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)autoRecordList.get(i);
	    		 //���忨�Ŵ���channelSendList�У��ұ�֤channelSendList���ظ���ͨ����
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
    	    	// *************�·�smg(��Ҫ����Щ�忨����Ϣ)**************
	    		CommonUtility.checkSMGChannelIndex(channelIndex, SMGSendList);
    	    }
    	
    	     
    	     String msgStr ="";
    	     
    	     if (channelSendList.size() != 0) {
    	    		@SuppressWarnings("unused")
					List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = handle.GetProgramInfoByIndex(channelSendList, true);
    	    		msgStr = createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set");
    	     }
    	    
    	     /*
    		
    		//��ȡ��Ҫ��smg�忨 �·��� url
    		List<SMGCardInfoVO> smgList=new ArrayList<SMGCardInfoVO>();
    		//���·�����Դ���޷��ҵ�һ��һ�Ľ�Ŀ��Ϣ����һ��һ�Ľ�Ŀ��Ϣ
    		getSmgUrl(channlList,smgList);
    		String url="";
    		for(int i=0;i<smgList.size();i++){
    			SMGCardInfoVO smg = (SMGCardInfoVO) smgList.get(i);
    			if(smg.getIndexType().equals("AutoRecord")){
    				//�˵ط�����Ϊ��һ���Զ�¼��ͨ��
	    			if(!url.equals(smg.getURL().trim())){
	    				try {
							utilXML.SendDownNoneReturn(msgStr, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
							url = smg.getURL().trim();
							try {
								Thread.sleep(1000 * 1);
							} catch (InterruptedException e) {
		
							}
						} catch (CommonException e) {
							log.error("�·��Զ�¼��SMG����" + smg.getURL());
							isErr = true;
						}
	    			}
    			}
    		}
    		*/
    	     //�������ֲ����õ�ʱ�򣬲���Ҫ��TSC�·��Զ�¼�ƣ�ֻ��Ҫ��IAS��SMG
    	     
    	     //1��û�и�TSC��IPM���ͳɹ�ʱ���ڸ��忨����Ϣ
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
 		                // �Զ�¼���·� timeout 1000*30 ��ʮ��
 		                utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(msgStr), smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
 		                url = smg.getURL().trim();
 		                }
 		                try {
 							Thread.sleep(1000 * 1);
 						} catch (InterruptedException e) {
 	
 						}
 		            } catch (CommonException e) {
 		                log.error("�·��Զ�¼��SMG����" + smg.getURL());
 		            }
 		            
 		           //���°忨��URL��ַ
		            try {
		            	handle.updateSMGURLByDevIndex(smg.getIndex(), smg.getURL());
		            } catch (Exception ex) {
		            	
		            }
 		        }
 	        }
 	            
    		/*
    		// ���½�Ŀӳ��� 
    		SetAutoRecordChannelHandle setAutoRecordChannel = new SetAutoRecordChannelHandle();
    		// RecordType 0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��)	3: ����¼��  4: �����˺ϳ��ֲ�
            for(int i=0; i< autoRecordList.size(); i++) {
            	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)autoRecordList.get(i);
    			try {
    				// 4: �����˺ϳ��ֲ� Add By: Bian Jiang 2012.3.21
        			vo.setRecordType(4);
					setAutoRecordChannel.upChannelRemappingIndex(vo);
				} catch (DaoException e) {
					log.error("���������˺ϳ��ֲ�����" + e.getLocalizedMessage());
				}
            }
            */
    	}
    	
    	//�·����໭
    	//���Ӷ໭���ͨ����
    	
    	//��������Ŀ��Ϣ
    	channlLists=parse(iasdoc);
    	
    	List ipmList=coreData.getIPMList();
    	for(int i=0;i<ipmList.size();i++){
    		IPMInfoVO ipm = (IPMInfoVO) ipmList.get(i);
    		if(ipm.getRecordType()==3){
    			try {
//    				String tomcatUrl="http://"+coreData.getSysVO().getLocalRedirectIp()+":"+coreData.getSysVO().getTomcatPort()+"/transmit/servlet/transmit";
					utilXML.SendDownNoneReturn(iasdoc.asXML(), ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
					
				} catch (CommonException e) {
					log.error("��������ѵ�·��໭����"+e.getMessage());
					isErr = true;
				}
    		}
    	}
    	MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
		} catch (DaoException e1) {
			log.error("ȡ�ö໭��ϳ�(������)URL����: " + e1.getMessage());
			isErr = true;
		}
    	
		RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setFreq(rtvsVO.getFreq());
        rtvsVO.setServiceID(rtvsVO.getServiceID());
        rtvsVO.setIndex(0);
        String rtvsString = createForDownXML(bsData, rtvsVO);
        
        
        if (isErr) {
            // ʧ��
            upString = utilXML.getReturnXML(bsData, 1);
        } else {
	        try {
	        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
	        } catch (CommonException e) {
	            log.error("�໭��ϳ�(������)�·�RTVS�޸���������IP�Ͷ˿ڳ���" + rtvsVO.getRTVSResetURL());
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
            log.error("��ƵURL StringToXML Error: " + e.getMessage());
        }
        
        if (isErr) {
            // ʧ��
            upString = getReturnXML(url, bsData, 1);
        } else {
	        try {
	        	upString = getReturnXML(url, bsData, 0);
	            utilXML.SendUpXML(upString, bsData);
	            //�ɹ������� �����˵��� �����Զ�¼��ɾ��Э��
	            
	        } catch (CommonException e) {
	            log.error("�໭��ϳ�(������)��Ϣʧ��: " + e.getMessage());
	        }
        }
        
        //�ɹ������� �����˵��� �����Զ�¼��ɾ��Э�顢
        //�����������ֲ� ���� ʱ�� �� AutoAnalysisTime.properties  
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
       
        
        
        //by tqy ���RUNTIME=""���������������ֲ�ֹͣ�̣߳�ֻ����ƵֹͣЭ���·�ʱ�򣬲Ž��д���
        
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
//		//��ʱ����ڣ�ɾ���������ֲ�����ô�ȶԣ�������ҵ�񣬷���������ʱ��
//		if (stopDate.before(new Date())) {
//			Timer stopTimer=new Timer();
//	        stopTimer.schedule(new MosaicStreamRoundInfoStopTimerTask(), stopDate);
//		}
//		
		
        bsData = null;
        this.downString = null;
        utilXML = null;
    }
    
    //��ȡ�ֲ��������
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
    				//�����һ��һ�ģ���ô����ȡ��ͨ���ţ�����ͨ����Ϊ��
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
					log.error("��ѯ�Զ�¼���Ƿ���ڴ���"+e.getMessage());
				}finally{
					DaoSupport.close(statement);
					DaoSupport.close(rs);
				}
	    	}
    	} catch (Exception e) {
    		log.error("��ѯ�Զ�¼���Ƿ���ڴ���"+e.getMessage());
    	}finally{
    		try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر��Զ�¼���ѯ���Ӵ���"+e.getMessage());
			}
    	}
    	return channlList;
    }
    
    /* 
     *  ������ת��ΪSetAutoRecordChannelVO�����list
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
	    				//��ȡ��Ŀ���ƺ�HDFLAG
		    			vo = setAutoRecordChannel.getHDFlagByProgram(vo);	
	    				// ����ͨ����Ϣ
		    			setAutoRecordChannel.GetChannelRemappingbyFreq(vo);
	    			}catch(Exception ex1)
	    			{
	    				log.error(ex1.getMessage());
	    			}
	    			
	    			// 4: �����˺ϳ��ֲ� Add By: Bian Jiang 2012.3.21
	    			vo.setRecordType(4);
	    			autoRecordList.add(vo);
	    			//by tqy ��ӵ��Զ�¼��һ��һ����
	    			saveMosaicProgramToMonitor(vo);
	    			
				} catch (Exception e) {
					e.printStackTrace();
					log.error("��ѯ�Զ�¼���Ƿ���ڴ���"+e.getMessage());
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
				log.error("��ѯ�Զ�¼���Ƿ���ڴ���"+e.getMessage());
			}finally{
				DaoSupport.close(statement);
				DaoSupport.close(rs);
			}
			*/
			
		} catch (Exception e) {
			log.error("��ѯ�Զ�¼���Ƿ���ڴ���"+e.getMessage());
		}finally{
//    		try {
//				DaoSupport.close(conn);
//			} catch (DaoException e) {
//				log.error("�ر��Զ�¼���ѯ���Ӵ���"+e.getMessage());
//			}
    	}
    	return autoRecordList;
    }
		
   public void saveMosaicProgramToMonitor(SetAutoRecordChannelVO vo ) throws DaoException
   {
	   		SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
	   		//�����ж������¼��֮ǰ�Ѿ����ڲ���״̬Ϊ¼�� �򻹰���ԭ��ͨ���·�
			 Statement statement = null;
			 ResultSet rs = null;
			 Connection conn = null;
			 try {
				 conn=DaoSupport.getJDBCConnection();
				 String sql="SELECT * FROM channelremapping where delflag =0 and Freq = "+vo.getFreq()+" and ServiceId = "+vo.getServiceID()+" and RecordType = 2 and StatusFlag = 1;";
				 boolean temp=false;
				 int tscindex=0;
				 //���ias index  Ji Long  2011-08-09
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
					log.error("��ѯ�Զ�¼��״̬����"+e.getMessage());
				}finally{
					DaoSupport.close(statement);
					DaoSupport.close(rs);
				}
				 if(temp){
					 vo.setTscIndex(tscindex);
					 vo.setIpmIndex(iasindex);
				 }else{
					 //TODO ����Ǹ����Ŀ��ƽ����������Ŀ��TSC 
					 //�����ͬһ��Ƶ��Ľ�Ŀ����ͬһ��tsc
					vo.setTscIndex(handle.getTSCIndex(vo.getHDFlag(),vo.getFreq()));
					
					//ias ͨ���� ƽ������  һ���������5������  Ji Long 
					//vo.setIpmIndex(handle.getIASIndex(vo.getHDFlag(),vo.getFreq()));
				 }
				 
				 //statusflag =1 ������Ч�Ľ�Ŀ��Ϣ
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
     * ȡ�÷��ص�XML��Ϣ
     * @param head XML���ݶ��� 
     * @param value 0:�ɹ� 1:ʧ��
     * @return XML�ı���Ϣ
     */
    private String getReturnXML(String url, MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
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
					log.info("��ѯ�忨ͨ������"+e.getMessage());
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
				log.info("�ر����Ӵ���"+e.getMessage());
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
					log.info("��ѯ�忨ͨ������"+e.getMessage());
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
				log.info("�ر����Ӵ���"+e.getMessage());
			}
		}
		return DevIndex;
	}
	
	
	
}



















