package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

/**
 * 自动录制
 * @author FeiChunteng
 *
 */
public class SetAutoRecordChannelHandle {

	private static Logger log = Logger.getLogger(SetAutoRecordChannelHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    private static MemCoreData coreData = MemCoreData.getInstance();
    
    private static SysInfoVO sysInfoVO = coreData.getSysVO();
    
    private static List SMGSendList = new ArrayList();//SMG的列表信息
    private static List TSCSendList = coreData.getTSCList();//tsc的列表信息
    private static List IPMSendList = coreData.getIPMList();//IPM的列表信息
    
    public SetAutoRecordChannelHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    public SetAutoRecordChannelHandle() {
    }
    
    /**
     * 1. 解析xml得到自动录像的全部信息
     * 2. 转发到TSC和SMG
     * 3. 下发成功后返回成功。
     *
     */
    @SuppressWarnings("unchecked")
	public void downXML(){
    	 // 返回数据
		@SuppressWarnings("unused")
		String upString = "";
		String smgDownString = "";

        List channelSendList = new ArrayList();//Channel Index List
        
        int channel = 0;
        TSCSendList = coreData.getTSCList();//tsc的列表信息
        IPMSendList = coreData.getIPMList();//IPM的列表信息
        SMGSendList = new ArrayList();
        
        String action = "Del";
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("自动录像StringToXML Error: " + e.getMessage());
        }
        SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
        List<SetAutoRecordChannelVO> AutoRecordlist = setAutoRecordChannel.getDownXml(document);
        
        // 判断是否自动录像是否超过配置文件中的最大值(MaxAutoRecordNum)
        int count = 0;
        for(int i=0; i< AutoRecordlist.size(); i++)  {//得到解析后的xml的对象数组中的一个操作
        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
        	if ((vo.getRecordType() == 1 || vo.getRecordType() == 2) && vo.getAction().equals("Set")) {
        		try {
					if(isHaveProgramInRemapping(vo) != 1) {
						vo = getHDFlagByProgram(vo);						
						//判断是否高清  是的话加5  不是的话加1
						if(vo.getHDFlag()==0){
							count++;
						}else{
							count+=5;
						}
					}
				} catch (DaoException e) {
					log.error("判断是否有更多的资源失败: " + e.getMessage());
				}
        		
        	}
        }
        
        try {
        	count += getAutoRecordNumbers();
		} catch (DaoException e2) {
		}
        
		//TODO  存在问题 我改 我改 我改改改 
		
		if (count > sysInfoVO.getMaxAutoRecordNum()) {
			// 自动录像超过配置文件中的最大值(MaxAutoRecordNum)
	        try {
	        	int temp=sysInfoVO.getMaxAutoRecordNum()-getAutoRecordNumbers();//剩余资源
	        	upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 1,temp);
	        	log.warn("### 自动录像7*24小时录像-没有更多的资源可用,剩余资源为："+temp+" ### ");
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("自动录像回复失败: " + e.getMessage());
	        }
			return;
		}
        
        for(int i=0; i< AutoRecordlist.size(); i++)  {//得到解析后的xml的对象数组中的一个操作

        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
        	
        	int channelIndex = (int)vo.getIndex();
        	
        	try {
        		
        		 // 无论是采用那个版本的xml协议自动录像都由转发自动分配节目 By Bian Jiang 2010.9.6
    			 if(vo.getAction().equals("Set")) {
    				 
    				 // Get Program HDFlag
    				 vo = getHDFlagByProgram(vo);
    				 
    				 if (vo.getDownIndex() != 0) {
    					 // 东软下发有通道号的处理
    					 GetChannelRemappingbyIndex(vo);
    				 }
    				 /** 广州去掉高清判断
					 // 东软下发没有通道号的处理
    				 if (vo.getHDFlag() == 1) {
    					 getHDFlagProgramIndex(vo);
    				 } else {
    					 GetChannelRemappingbyFreq(vo);
    				 }
    				 */
					 
    				 GetChannelRemappingbyFreq(vo);
    				 
    				 // 取得TSCIndex通道号 By: Bian Jiang 2011.4.8
    				 
    				 //增加判断如果该录像之前已经存在并且状态为录制 则还按照原来通道下发
    				 Statement statement = null;
    				 ResultSet rs = null;
    				 Connection conn = null;
    				 try {
    					 conn=DaoSupport.getJDBCConnection();
	    				 String sql="SELECT * FROM channelremapping where Freq = "+vo.getFreq()+" and ServiceId = "+vo.getServiceID()+" and RecordType = 2 and StatusFlag = 1;";
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
	    					vo.setTscIndex(getTSCIndex(vo.getHDFlag(),vo.getFreq()));
	    					
	    					//ias 通道号 平均分配  一个高清等于5个标清  Ji Long 
	    					vo.setIpmIndex(getIASIndex(vo.getHDFlag(),vo.getFreq()));
	    				 }
	    				 
	    				 channelIndex = vo.getDevIndex();
	    				 //statusflag =1 代表有效的节目信息
	    				 upChannelRemappingIndex(vo);
	    				 action = vo.getAction();
    				 } catch (Exception e) {
    					 e.printStackTrace();
					 }finally{
						DaoSupport.close(conn);
					 }
    			 } else {
    				 // 删除通道使用标记 TODO 删除返回通道有问题
    				 GetIndexByProgram(vo, true);
    				 channelIndex = vo.getDevIndex();
    				 updateDelFlagChannelRemappingByProgram(vo);
    			 }
        			 
        		
    			 //将板卡号存入channelSendList中，且保证channelSendList无重复的通道号
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
        			channel = channelIndex;
    			} else {
    				channel = channelIndex;
    			}

//                  *************下发smg(需要给哪些板卡发信息)**************
        		CommonUtility.checkSMGChannelIndex(channelIndex, SMGSendList);
        		
			} catch (DaoException e) {
				log.info("自动录像更新数据库错误:"+e.getMessage());
				//e.printStackTrace();
			}
        }
        
        
        if (channelSendList.size() != 0) {
	        	
	        // 广州监测2.0版本 通过通道映射
        	// 无论是采用那个版本的xml协议自动录像都由转发自动分配节目 By Bian Jiang 2010.9.6
	        //if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_0)) {
	        	try {
	        		List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = GetProgramInfoByIndex(channelSendList, true);
	        		List<SetAutoRecordChannelVO> AutoRecordlistNew = GetProgramInfoByDownIndex(channelSendList, true);	        		
	    	       
	    	        
		        	// 通过通道号取得所有节目信息
		        	if (action.equals("Del")) {
		        		//by tqy 2012-05-23 :只下发删除的节目信息
		        		
		        		smgDownString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set", true);
		        		//this.downString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistNew, "Del",false);
		        		AutoRecordlistNew.clear();
		        		AutoRecordlistNew = GetDelProgramInfoByIndex(channelSendList, true);
		        		this.downString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistNew, "Del", true);
		        		
		        	} else {
			        	smgDownString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set", true);
			        	this.downString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistNew, "Set", false);
		        	}
		        	
				} catch (Exception e) {
					log.error("通过通道取得节目信息出错: " + e.getMessage());
				}
	        //}
	        
	        
	        String url = "";	        
	        String retString = "";
	        
	        int isError = 0;
	        //TODO TSC下发
	        for(int t=0;t<TSCSendList.size();t++){
	            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);
	            try {
	            	if (tsc.getRecordType() != 1 && tsc.getRecordType() != 2) {
	            		// Add By Bian Jiang 2011.1.28
	            		// 如果不是自动录像或异态录像就进行下一个
	            		continue;
	            	}
	            	//原语句不能保证一个板卡只发一次：BY TQY 
	                if (!url.equals(tsc.getURL().trim())) {
	                    // 自动录像下发
	                	retString = utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	                    url = tsc.getURL().trim();
	                }
	                if(!retString.equals("")) {
	                	isError = utilXML.getReturnValue(retString);
		                if (isError == 1) {
		                	break;
		                }
	                }
	                
	            } catch (CommonException e) {
	                log.error("下发自动录像到TSC出错：" + tsc.getURL());
	            }
	        }
	        
	        /* IPM 不用发送 */
	        for(int t=0;t<IPMSendList.size();t++){
	        	IPMInfoVO ipm = (IPMInfoVO) IPMSendList.get(t);
	            try {
	            	if(ipm.getRecordType()==2){
	            		if (!url.equals(ipm.getURL().trim())) {
	            			// 自动录像下发
	            			retString = utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	            			url = ipm.getURL().trim();
	            			if(!retString.equals("")) {
	            				isError = utilXML.getReturnValue(retString);
	            				if (isError == 1) {
	            					break;
	            				}
	            			}
	            		}
	            	}
	                
	            } catch (CommonException e) {
	                log.error("下发自动录像到IPM出错：" + ipm.getURL());
	            }
	        }
	        
	    	if(AutoRecordlist.size() > 0 && isError==0) {
	    		upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 0,-1);
	    	} else {
	    		upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 1,-1);
	    	}
	    	
	        try {
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("自动录像回复失败: " + e.getMessage());
	        }
	       
	        //1：没有给TSC和IPM发送成功时不在给板卡发信息
	        if(AutoRecordlist.size() > 0 && isError==0) {
	        
		        for(int l=0;l<SMGSendList.size();l++)
		        {
		            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(l);
		            try {
		                if (!url.equals(smg.getURL().trim())) {
		                smgDownString = smgDownString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
		            	smgDownString = smgDownString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
		                // 自动录像下发 timeout 1000*30 三十秒
		                utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(smgDownString), smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
		                url = smg.getURL().trim();
		                }
		                // 高清相关配置
		                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
		                	// 高清转码下发
		                	utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(smgDownString), smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
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
		            	updateSMGURLByDevIndex(smg.getIndex(), smg.getURL());
		            } catch (Exception ex) {
		            	
		            }
		        }
	            
	        } 
	        else	
	        {
	        	//发送失败后更新数据库delflag=1数据已经删除
	            for(int i=0; i< AutoRecordlist.size(); i++)  {//得到解析后的xml的对象数组中的一个操作

	            	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
	            	try {
						updateDelFlagChannelRemappingByProgram(vo);
					} catch (DaoException e) {
						
					}
	            }
	        }
		     
	        //对无用的数据信息进行恢复 delflag =1  无用（已经）的节目信息
		    try {
		    	// 更新数据库
			   delChannelRemappingByProgram();
			} catch (DaoException e1) {
				
			}
        } else {
        	//BY TQY 2012-04-12
        	//系统默认为删除所有节目信息协议处理
        	//1:平台设置完后，先上报给平台,否则需要等待时间比较长
        	//2:在下发给所有的SMG板卡进行删除命令
        	//3:同样也要给TSC下发删除所有自动录制信息、JAVA转发未处理
        	if(action.equals("Del")) {
	    		upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 0,-1);
        	} else {
        		upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 1,-1);
        	}
	        try {
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("自动录像回复失败: " + e.getMessage());
	        }
        	
        }
        
        if(AutoRecordlist.size() == 0) {
        	// 自定义协议， 任务录像删除
        	log.info("自定义协议， 任务录像删除 Start");
            MemCoreData coreData = MemCoreData.getInstance();
            // 取得SMG配置文件信息
            List SMGCardList = coreData.getSMGCardList();
            
            for (int i=0; i< SMGCardList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
                try {
                    // 自定义协议， 任务录像删除
                    utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                } catch (CommonException e) {
                    log.error("自定义协议, 任务录像删除向SMG下发任务录像出错：" + smg.getURL());
                }
            } // SMG 下发指令 END
            log.info("自定义协议， 任务录像删除 End");
        }
        
	        bsData = null;
	        downString = null;
	        SMGSendList = null;
	        TSCSendList = null;
	        IPMSendList = null;
	        utilXML = null;
	        AutoRecordlist = null;
    }
    
    public void updateAutoRecordTable(SetAutoRecordChannelVO vo)
			throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// update channelstatus c set freq = 6000000, qam = 'QAM128' where
		// channelindex = 1
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("update channelstatus set ");
		strBuff.append(" ProgramName = '" + vo.getProgramName() + "', ");
		strBuff.append("freq = " + vo.getFreq() + ", ");
		strBuff.append("SymbolRate = " + vo.getSymbolRate() + ", ");
		strBuff.append("qam = " + vo.getQAM() + ", ");
		strBuff.append("ServiceID = " + vo.getServiceID() + ", ");
		strBuff.append("VideoPID = " + vo.getVideoPID() + ", ");
		strBuff.append("AudioPID = " + vo.getAudioPID() + ", ");
		strBuff.append("HDFlag = " + vo.getHDFlag() + ", ");
		strBuff.append("lasttime = '" + CommonUtility.getDateTime() + "', ");
		strBuff.append("indexstatus = 2 "); // 默认自动录制
		strBuff.append("where channelindex = " + vo.getIndex());
		//log.info("自动录像更新数据库：" + strBuff.toString());

		try {
			statement = conn.createStatement();
			statement.executeUpdate(strBuff.toString());

		} catch (Exception e) {
			log.error("自动录像更新数据库错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		log.info("自动录像更新数据库成功!");
		DaoSupport.close(conn);

	}
    
    private void insertNewIndex(SetAutoRecordChannelVO vo, int index) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// insert into channelprogramstatus(channelindex, freq, symbolrate, qam, serviceID, videoPID, audioPID, lasttime, HDFlag) 
		// values(1, 1, 1, 1 ,1, 1, 1, '2010-06-02 16:46:15', 0)
		strBuff.append("insert into channelprogramstatus(channelindex, freq, symbolrate, qam, serviceID, videoPID, audioPID, lasttime, HDFlag)");
		strBuff.append(" values(");
		strBuff.append(vo.getIndex() + ", ");
		strBuff.append(vo.getFreq() + ", ");
		strBuff.append(vo.getSymbolRate() + ", ");
		strBuff.append("'" + vo.getQAM() + "', ");
		strBuff.append(vo.getServiceID() + ", ");
		strBuff.append(vo.getVideoPID() + ", ");
		strBuff.append(vo.getAudioPID() + ", ");
		strBuff.append("'" + CommonUtility.getDateTime() + "', ");
		strBuff.append(vo.getHDFlag() + ")");
		
		try {
			statement = conn.createStatement();
			
			statement.executeUpdate(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像插入通道数据库错误: " + e.getMessage());
			log.error("自动录像插入通道数据库错误 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		//log.info("自动录像插入通道数据库成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		DaoSupport.close(conn);
	}
    
    /**
     * 在自动录像删除的时, 删除相关节目信息
     * @param vo
     * @param index
     * @throws DaoException
     */
    private void delProgramInfo(SetAutoRecordChannelVO vo, int index) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// delete from channelprogramstatus where channelindex = 1 and freq = 1 and serviceID = 1
		strBuff.append("delete from channelprogramstatus where ");
		strBuff.append(" channelindex=" + vo.getIndex() + " and ");
		strBuff.append(" freq=" + vo.getFreq() + " and ");
		strBuff.append(" serviceID=" + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像删除节目信息错误: " + e.getMessage());
			log.error("自动录像删除节目信息错误 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		//log.info("自动录像删除节目信息成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		DaoSupport.close(conn);
	}
    private void upFreqByIndex(SetAutoRecordChannelVO vo, int index) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		strBuff.append("update transmit.channelprogramstatus c set freq= " + vo.getFreq() + ", ");
		strBuff.append("SymbolRate = " + vo.getSymbolRate() + ", ");
		strBuff.append("qam = '" + vo.getQAM() + "', ");
		strBuff.append(" ProgramName = '" + vo.getProgramName() + "', ");
		strBuff.append("ServiceID = " + vo.getServiceID() + ", ");
		strBuff.append("VideoPID = " + vo.getVideoPID() + ", ");
		strBuff.append("AudioPID = " + vo.getAudioPID() + ", ");
		strBuff.append("HDFlag = " + vo.getHDFlag() + ", ");
		if(vo.getAction().equals("Set")) {
			strBuff.append("channelflag = 1, ");
		} else {
			strBuff.append("channelflag = 0, ");
		}
		
		strBuff.append("lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append("where channelindex = " + index);
		if(vo.getAction().equals("Del")) {
			strBuff.append(" and channelflag = 1 ");
		} else {
			strBuff.append(" and channelflag = 0 ");
		}
		
		try {
			statement = conn.createStatement();
			
			statement.executeUpdate(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像更新通道数据库错误: " + e.getMessage());
			log.error("自动录像更新通道数据库错误 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		//log.info("自动录像更新通道数据库成功!");
		DaoSupport.close(conn);
	}
    
    private  List<SetAutoRecordChannelVO> GetProgramInfoByDownIndex(List downChannelSendList, boolean isChannelRemapping) throws DaoException {

		List<SetAutoRecordChannelVO> voList = new ArrayList();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		for(int i=0; i<downChannelSendList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			// 取得相关节目频点信息
			strBuff.append("select *  from channelremapping where DevIndex = " + downChannelSendList.get(i));
			
			strBuff.append("  order by channelindex");
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					
					if (freq == 0 || serverID == 0) {
						continue;
					}
					
					SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
					
					// channelprogramstatus(channelindex, freq, symbolrate, qam, serviceID, videoPID, audioPID, lasttime, HDFlag) 
					vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
					vo.setIpmIndex(Integer.parseInt(rs.getString("IpmIndex")));
					vo.setFreq(freq);
					vo.setSymbolRate(Integer.parseInt(rs.getString("symbolrate")));
					vo.setQAM(Integer.parseInt(rs.getString("qam")));
					vo.setAction(rs.getString("Action"));
					  
	                if("Set".equals(rs.getString("Action"))){
	                vo.setCodingFormat(rs.getString("CodingFormat"));
	                vo.setWidth(rs.getString("Width"));
	                vo.setHeight(rs.getString("Height"));
	                vo.setFps(rs.getString("Fps"));
	                vo.setBps(rs.getString("Bps"));
	                }
					vo.setServiceID(serverID);
					
					vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
					vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
					vo.setRecordType(Integer.parseInt(rs.getString("RecordType")));
					if (isChannelRemapping) {
						vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
					} else {
						vo.setHDFlag(Integer.parseInt(rs.getString("HDFlag")));
					}
					
					voList.add(vo);
				}
				
			} catch (Exception e) {
				log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
		
			}
			strBuff = null;
		}
		DaoSupport.close(conn);
		
		return voList;
	}
    
    
    
    
    public  List<SetAutoRecordChannelVO> GetProgramInfoByIndex(List channelSendList, boolean isChannelRemapping) throws DaoException {

		List<SetAutoRecordChannelVO> voList = new ArrayList();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		for(int i=0; i<channelSendList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			// 取得相关节目频点信息
			if (isChannelRemapping) {
				strBuff.append("select *  from channelremapping where DevIndex = " + channelSendList.get(i) + " and DelFlag = 0 ");
			} else {
				strBuff.append("select *  from channelprogramstatus where channelindex = " + channelSendList.get(i) + " and DelFlag = 0 ");	
			}
			
			strBuff.append("  order by channelindex");
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					
					if (freq == 0 || serverID == 0) {
						continue;
					}
					
					int lastflag = 0;
					
					try {
						lastflag = Integer.parseInt(rs.getString("StatusFlag"));
						if (lastflag == 0) {
							continue;
						}
					} catch (Exception e) {
						
					}
					SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
					// channelprogramstatus(channelindex, freq, symbolrate, qam, serviceID, videoPID, audioPID, lasttime, HDFlag) 
					vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
					vo.setIpmIndex(Integer.parseInt(rs.getString("IpmIndex")));
					vo.setFreq(freq);
					vo.setSymbolRate(Integer.parseInt(rs.getString("symbolrate")));
					vo.setQAM(Integer.parseInt(rs.getString("qam")));
					vo.setServiceID(serverID);
					vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
					vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
					vo.setAction(rs.getString("Action"));
					
					vo.setRecordType(Integer.parseInt(rs.getString("RecordType")));
					if (isChannelRemapping) {
						vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
					} else {
						vo.setHDFlag(Integer.parseInt(rs.getString("HDFlag")));
					}
					voList.add(vo);
				}
				
			} catch (Exception e) {
				log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
		
			}
			strBuff = null;
		}
		DaoSupport.close(conn);
		
		return voList;
	}

    
    public  List<SetAutoRecordChannelVO> GetDelProgramInfoByIndex(List channelSendList, boolean isChannelRemapping) throws DaoException {

		List<SetAutoRecordChannelVO> voList = new ArrayList();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		for(int i=0; i<channelSendList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			// 取得相关节目频点信息
			if (isChannelRemapping) {
				strBuff.append("select *  from channelremapping where DevIndex = " + channelSendList.get(i) + " and DelFlag = 1 ");
			} else {
				strBuff.append("select *  from channelprogramstatus where channelindex = " + channelSendList.get(i) + " and DelFlag = 1 ");	
			}
			
			strBuff.append("  order by channelindex");
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					
					if (freq == 0 || serverID == 0) {
						continue;
					}
					
					int lastflag = 0;
					
					try {
						lastflag = Integer.parseInt(rs.getString("StatusFlag"));
						if (lastflag == 0) {
							continue;
						}
					} catch (Exception e) {
						
					}
					SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
					// channelprogramstatus(channelindex, freq, symbolrate, qam, serviceID, videoPID, audioPID, lasttime, HDFlag) 
					vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
					vo.setIpmIndex(Integer.parseInt(rs.getString("IpmIndex")));
					vo.setFreq(freq);
					vo.setSymbolRate(Integer.parseInt(rs.getString("symbolrate")));
					vo.setQAM(Integer.parseInt(rs.getString("qam")));
					vo.setServiceID(serverID);
					vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
					vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
					vo.setAction(rs.getString("Action"));
					
					vo.setRecordType(Integer.parseInt(rs.getString("RecordType")));
					if (isChannelRemapping) {
						vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
					} else {
						vo.setHDFlag(Integer.parseInt(rs.getString("HDFlag")));
					}
					voList.add(vo);
				}
				
			} catch (Exception e) {
				log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
		
			}
			strBuff = null;
		}
		DaoSupport.close(conn);
		
		return voList;
	}

    
    public static void GetIndexByProgram(SetAutoRecordChannelVO vo, boolean isChannelRemapping) throws DaoException {

		int index = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// 取得相关节目频点信息
		if (isChannelRemapping) {
			strBuff.append("select channelindex, DevIndex, TscIndex  from channelremapping where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		} else {
			strBuff.append("select channelindex, DevIndex  from channelprogramstatus where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());	
		}
		
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
				//channelFlag = Integer.parseInt(rs.getString("channelFlag"));
				vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
				if (isChannelRemapping) {
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));	
					vo.setIpmIndex(Integer.parseInt(rs.getString("IpmIndex")));	
				}
				return;
			}
			
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("自动录像 取得节目相关通道成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		
		return;
	}
    
    public static int GetIndexByFreq(SetAutoRecordChannelVO vo) throws DaoException {

		int index = 0;
		int indexChannel = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// 监测中心三期
		//strBuff.append("select channelindex,channelFlag  from channelprogramstatus where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		
		// 统一频点同时监测多套节目
		strBuff.append("select channelindex  from channelprogramstatus where Freq = " + vo.getFreq());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				index = Integer.parseInt(rs.getString("channelindex"));
				//channelFlag = Integer.parseInt(rs.getString("channelFlag"));
				vo.setIndex(index);
//				if (vo.getAction().equals("Del")) {
//					strBuff = new StringBuffer();
//					strBuff.append("update channelprogramstatus c set channelflag = 0 where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
//					strBuff.append(" and channelindex != 1 and channelindex != 11 and channelindex != 12 and channelindex != 27 and channelindex != 28 and channelindex != 29 and channelindex != 30 and channelindex != 31");
//					statement.executeUpdate(strBuff.toString());
//				}
				return index;
			}
			
			if(index == 0) {
				strBuff = new StringBuffer();
				strBuff.append("select channelindex from channelprogramstatus");
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					indexChannel = Integer.parseInt(rs.getString("channelindex"));
					
					if (indexChannel != index && index != 0) {
						vo.setIndex(index);
						return index;
					}
					// TODO Index 有个最大值需要在配置文件中配置
					index += 1;
				}
			}
			
//			if(index == 0) {
//				strBuff = new StringBuffer();
//				strBuff.append("select channelindex,channelFlag from channelprogramstatus where Freq = " + vo.getFreq());
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				while(rs.next()){
//					index = Integer.parseInt(rs.getString("channelindex"));
//					channelFlag = Integer.parseInt(rs.getString("channelFlag"));
//					if (channelFlag == 0) {
//						vo.setIndex(index);
//						return index;
//					}
//				}
//			}
			
//			if(index == 0) {
//				index = 2;
//				strBuff = new StringBuffer();
//				strBuff.append("update channelprogramstatus c set channelflag = 0 where channelindex != 1 and channelindex != 11 and channelindex != 12 and channelindex != 27 and channelindex != 28 and channelindex != 29 and channelindex != 30 and channelindex != 31");
//				statement.executeUpdate(strBuff.toString());
//				log.info("清除自动录像标记");
//			}

		} catch (Exception e) {
			log.error("自动录像查询通道数据库错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("自动录像查询通道数据库成功!");
		
		vo.setIndex(index);
		return index;
	}
    
    /**
     * 通道分配函数，通过上层发生的信息自动分配相关通道
     * @param vo
     * @throws DaoException
     */
    public void GetChannelRemappingbyFreq(SetAutoRecordChannelVO vo) throws DaoException {

		int index = 0;
		int devIndex = 0;
		StringBuffer strBuff = new StringBuffer();
		
		boolean isreturn = false;
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
        SysInfoVO sysInfoVO = coreData.getSysVO();
        
		ResultSet rs = null;
		
		strBuff = new StringBuffer();
		//是否为一对一的节目信息
		strBuff.append("select DevIndex, channelindex from channelremapping where delflag =0 and freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		
		System.out.println("是否为一对一的节目信息{freq+serviceid}:"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				//by tqy :2012-05-15 当前通道停用，获取下一个通道信息
//				if(CommonUtility.checkSMGChannelStatus(devIndex)){
//					continue;
//				}
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isreturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误1: " + e.getMessage());
			log.error("自动录像 取得节目相关通道错误1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isreturn) {
				DaoSupport.close(conn);
			}
		}
		
		// 通过Freq 和 StatusFlag 来确定相关的频点是否分配
		strBuff = new StringBuffer();
		// select * from channelremapping where devindex in (select DevIndex from channelremapping where freq = 259000) and statusflag = 0
		// 取得相关节目频点信息
		//strBuff.append("select DevIndex, channelindex from channelremapping where freq = " + vo.getFreq() + " and statusflag = 0");
		
		//是否为未使用的频点通道信息
		strBuff.append("select * from channelremapping where devindex in (select DevIndex from channelremapping where delflag =0 and freq = " + vo.getFreq() + ") and delflag =0 and statusflag = 0");
		System.out.println("是否为未使用的频点通道信息{freq}"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				//by tqy :2012-05-15 当前通道停用，获取下一个通道信息
//				if(CommonUtility.checkSMGChannelStatus(devIndex)){
//					continue;
//				}
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isreturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误2: " + e.getMessage());
			log.error("自动录像 取得节目相关通道错误2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isreturn) {
				DaoSupport.close(conn);
			}
		}
		
		// 没有取得相关的通道，需要从新分配一个新的SMG板卡
		strBuff = new StringBuffer();
//		List<Integer> devIndexList = new ArrayList();
		// 取得相关节目频点信息
		// SELECT * FROM channelremapping c where statusflag = 0 and Devindex not in (select devindex from channelremapping where statusflag = 1) group by devindex
		strBuff.append("SELECT * FROM channelremapping c where delflag =0 and statusflag = 0 and Devindex not in (select devindex from channelremapping where delflag =0 and statusflag = 1) group by devindex");
		System.out.println("没有取得相关的通道，需要从新分配一个新的SMG板卡:"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 取得没有被分配的SMG设备
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				//by tqy :2012-05-15 当前通道停用，获取下一个通道信息
//				if(CommonUtility.checkSMGChannelStatus(devIndex)){
//					continue;
//				}
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isreturn = true;
				
//				devIndexList.add(devIndex);
				return;
				
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误3: " + e.getMessage());
			log.error("自动录像 取得节目相关通道错误3 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isreturn) {
				DaoSupport.close(conn);
			}
		}
		
		
//		for(int i=0; i<devIndexList.size(); i++) {
//			// 没有取得相关的通道，需要从新分配一个新的SMG板卡
//			strBuff = new StringBuffer();
//			// 取得相关节目频点信息
//			// SELECT count(DevIndex) FROM channelremapping c where statusflag = 0 and devindex = 1
//			strBuff.append("SELECT count(DevIndex), channelindex FROM channelremapping c where statusflag = 0 and devindex = " + devIndexList.get(i) + " order by channelindex");
//			
//			try {
//				statement = conn.createStatement();
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				// 取得没有被分配的SMG设备
//				while(rs.next()){
//					// 表示一个频点同时打几套节目，需要增加配置文件
//					if (Integer.parseInt(rs.getString("count(DevIndex)")) == sysInfoVO.getMAXProgram()) {
//						devIndex = devIndexList.get(i);
//						vo.setDevIndex(devIndex);
//						index = Integer.parseInt(rs.getString("channelindex"));
//						vo.setIndex(index);
//						upChannelRemappingToSameFreq(vo);
//						isreturn = true;
//						return;
//					}
//
//				}
//			} catch (Exception e) {
//				log.error("自动录像 取得节目相关通道错误4: " + e.getMessage());
//				log.error("自动录像 取得节目相关通道错误4 SQL: " + strBuff.toString());
//			} finally {
//				DaoSupport.close(rs);
//				DaoSupport.close(statement);
//				if(isreturn) {
//					DaoSupport.close(conn);
//				}
//			}
//		}
		
		strBuff = null;
		DaoSupport.close(conn);
		
		//log.info("自动录像 取得节目相关通道成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return;
	}
    
    private void GetChannelRemappingbyIndex(SetAutoRecordChannelVO vo) throws DaoException {

		int index = 0;
		int devIndex = 0;
		int freq = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// 取得相关节目频点信息
		strBuff.append("select DevIndex, channelindex, Freq from channelremapping where DownIndex = " + vo.getDownIndex());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				freq = Integer.parseInt(rs.getString("Freq"));
				
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				
				if(vo.getFreq() == freq) {
					// 如果是同一个频点更新当前节目信息
					this.upChannelRemappingIndex(vo);
				} else {
					// 更新原始节目statusFlag为0
					delChannelRemappingIndex(vo);
				}
				
				return;
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误1: " + e.getMessage());
			log.error("自动录像 取得节目相关通道错误1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		
		//log.info("自动录像 取得节目相关通道成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return;
	}
    
    /**
     * 是否存在当前节目
     * @param vo
     * @return 1:存在 0:不存在
     * @throws DaoException
     */
    public static int isHaveProgramInRemapping(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		int flag = 0;
		ResultSet rs = null;
		
		// 取得相关节目频点信息
		strBuff.append("select * from channelremapping where StatusFlag != 0 and freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				vo.setUdp(rs.getString("udp"));
				vo.setPort(Integer.parseInt(rs.getString("port")));
				flag = 1;
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误1: " + e.getMessage());
			log.error("自动录像 取得节目相关通道错误1 SQL: " + strBuff.toString());
			flag = 0;
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		
		
		//log.info("自动录像 取得节目相关通道成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return flag;
	}
    
    private static void upChannelRemappingToSameFreq(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set freq= " + vo.getFreq() + ", ");
		strBuff.append("StatusFlag = 0, ");
		
		strBuff.append("lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append("where devIndex = " + vo.getDevIndex());
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
			log.error("自动录像 更新通道映射表错误SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public void upChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set freq= " + vo.getFreq() + ", ");
		strBuff.append(" SymbolRate = " + vo.getSymbolRate() + ", ");
		strBuff.append(" qam = '" + vo.getQAM() + "', ");
		if(vo.getAction() == null || vo.getAction().trim().equals("") || vo.getAction().trim().equals("null")) {
			strBuff.append(" Action = 'Set', ");
		} else {
			strBuff.append(" Action = '" + vo.getAction() + "', ");
		}
		strBuff.append(" ProgramName = '" + vo.getProgramName() + "', ");
		strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
		strBuff.append(" TscIndex = " + vo.getTscIndex() + ", ");
		strBuff.append(" IpmIndex = " + vo.getIpmIndex() + ", ");
		strBuff.append(" VideoPID = " + vo.getVideoPID() + ", ");
		strBuff.append(" AudioPID = " + vo.getAudioPID() + ", ");
		strBuff.append(" StatusFlag = 1, " );
		strBuff.append(" DownIndex = " + vo.getDownIndex()+ ", ");
		strBuff.append(" RecordType = " + vo.getRecordType()+ ", ");
		strBuff.append(" HDFlag = " + vo.getHDFlag()+ ", ");
		strBuff.append(" DelFlag = 0, ");
		strBuff.append(" udp = '" + CommonUtility.getIPbyFreq(vo.getFreq()) + "', ");
		strBuff.append(" port = " + CommonUtility.getPortbyServiceID(vo.getServiceID()) + ", ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where channelindex = " + vo.getIndex());
		strBuff.append(" and devIndex = " + vo.getDevIndex());
		System.out.println("更新一对一表数据："+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
			log.error("自动录像 更新通道映射表错误SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public static void delChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, ");
		strBuff.append(" DownIndex = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where channelindex = " + vo.getIndex());
		strBuff.append(" and devIndex = " + vo.getDevIndex());
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    /**
     * 删除节目映射表相关的信息
     * @param vo
     * @throws DaoException
     */
    public static SetAutoRecordChannelVO delRecordTaskIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		
		// 取得相关节目频点信息
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, ");
		strBuff.append(" DownIndex = 0, RecordType = 0, Action='', delflag = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where Freq = " + vo.getFreq());
		strBuff.append(" and ServiceID = " + vo.getServiceID());
		strBuff.append(" and RecordType = 3 ");
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		
		//log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return vo;
	}
    
    
    /**
     * 删除节目映射表相关的信息
     * @param vo
     * @throws DaoException
     */
    public static void updateRecordTaskIndex() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		
		// 取得相关节目频点信息
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, ");
		strBuff.append(" DownIndex = 0, RecordType = 0, Action='', delflag = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where RecordType = 3 ");
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		
		
	}
    
    public static void updateDelFlagChannelRemappingByProgram(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" delflag = 1 ");
		strBuff.append("where ");
		strBuff.append(" freq = " + vo.getFreq());
		strBuff.append(" and ServiceID = " + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		
		strBuff = new StringBuffer();
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" delflag = 1 ");
		strBuff.append("where ");
		strBuff.append(" freq = " + vo.getFreq());
		strBuff.append(" and StatusFlag = 0");
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		//log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public static void delChannelRemappingByProgram() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, freq = 0, ServiceID=0, VideoPID=0, AudioPID=0, ProgramName='', ");
		strBuff.append(" DownIndex = 0, tscIndex = 0, IpmIndex = 0,");
		strBuff.append(" delFlag = 0, RecordType = 0, Action = '', udp='', port=0, smgURL='', HDFlag=0, ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append("where delFlag = 1");
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
	}
    
    private static void getHDFlagProgramIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		int devIndex = 0;
		int index = 0;
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		boolean isReturn = false;
		ResultSet rs = null;
		
		// 取得相关节目频点信息
		strBuff.append("select DevIndex, channelindex from channelremapping where HDFlag = 1 and freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());		
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("取得高清节目相关通道错误2: " + e.getMessage());
			log.error("取得高清节目相关通道错误2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
	
		strBuff = new StringBuffer();
		// 取得相关节目频点信息
		strBuff.append("select DevIndex, channelindex from channelremapping where statusflag = 0 and HDFlag = 1");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("取得高清节目相关通道错误2: " + e.getMessage());
			log.error("取得高清节目相关通道错误2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
		strBuff = null;
		
		strBuff = new StringBuffer();
		// 取得相关节目频点信息
		strBuff.append("select DevIndex, channelindex from channelremapping where HDFlag = 1 and freq = " + vo.getFreq());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("取得高清节目相关通道错误2: " + e.getMessage());
			log.error("取得高清节目相关通道错误2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
		strBuff = null;
		
		strBuff = new StringBuffer();
		// 取得相关节目频点信息
		strBuff.append("select DevIndex, channelindex from channelremapping where HDFlag = 1 order by lasttime");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("取得高清节目相关通道错误2: " + e.getMessage());
			log.error("取得高清节目相关通道错误2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
		strBuff = null;
		DaoSupport.close(conn);
		
		//log.info("取得高清节目相关通道成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return;
	}
    
    public static SetAutoRecordChannelVO getHDFlagByProgram(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		//LastFlag = 1最后一次更新的频道表内容、 取得相关节目频点信息
		strBuff.append("select * from channelscanlist where  Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID() + " and LastFlag = 1");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				
				vo.setHDFlag(Integer.parseInt(rs.getString("HDTV")));
				
				vo.setProgramName(rs.getString("Program"));

				return vo;
			}
		} catch (Exception e) {
			log.error("取得节目高清标记错误1: " + e.getMessage());
			log.error("取得节目高清标记错误1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		//log.info("取得节目高清标记成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return vo;
	}
    
    /**
     * 取得7*24小时自动录像的个数
     * @return
     * @throws DaoException
     */
    public static int getAutoRecordNumbers() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		int count = 0;
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// 取得相关节目频点信息
		//strBuff.append("SELECT count(*) FROM channelremapping c where (recordType = 2 or recordType = 1) and statusFlag = 1 ");
		
		//资源判断更改   JI LONG
		strBuff.append("SELECT HDFlag FROM channelremapping c where (recordType = 2 or recordType = 1) and statusFlag = 1 ");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				//更改前
				//count = Integer.parseInt(rs.getString("count(*)"));
				//更改后
				int isHD=Integer.parseInt(rs.getString("HDFlag"));
				if(isHD==0){
					count++;
				}else{
					count+=5;
				}
				return count;
			}
		} catch (Exception e) {
			log.error("取得7*24小时自动录像的个数: " + e.getMessage());
			log.error("取得7*24小时自动录像的个数 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		//log.info("取得节目高清标记成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return count;
	}
    
    /**
     * 更新映射表SMG接收URL地址，用于方便问题定位
     * @param devIndex SMG设备通道号
     * @param smgURL  SMG接收到URL
     * @throws DaoException
     */
    public static void updateSMGURLByDevIndex(int devIndex, String smgURL) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" smgURL = '" + smgURL + "' ");
		strBuff.append(" where StatusFlag != 0 and DevIndex = " + devIndex);
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("自动录像 更新通道映射表错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
	}
    
    /**
     * 根据节目频点 高清标记 获取最佳 ias 通道号分配  Ji Long 
     * @param isHD  高清标记
     * @param freq 
     * @return
     */
    
    public static int getIASIndex(int isHD,int freq){
    	int ipmIndex = 0;
    	
    	try {
			List ipmIndexList = getIPMIndexList();
			
			boolean isGetIndex = false;
			int count = 1;
			
			if(isHD == 1) {
				// 高清节目处理
				IPMInfoVO ipm = getBestIpmInfo();
				count = ipm.getIndexMin();
			}else{
				//获取该频点所在那个ipm 并且返回该ipm使用到的最大通道号
				//Ji Long 2011-06-26
				int temp=getMinIndexTsc(freq);
				
				List<TempTSC> tempIpms=new ArrayList<TempTSC>();
				for (int i = 0; i < IPMSendList.size(); i++) {
					IPMInfoVO ipm=(IPMInfoVO)IPMSendList.get(i);
					if(ipm.getRecordType()==2){
						TempTSC tempIpm =new TempTSC();
						List<Integer> tempList=new ArrayList<Integer>();
						for(int j=0;j<=ipm.getIndexMax()-ipm.getIndexMin();j++){
							tempList.add(ipm.getIndexMin()+j);
						}
						tempIpm.setKey(ipm.getIndexMin());
						tempIpm.setValue(tempList);
						tempIpms.add(tempIpm);
					}
				}
				for (int i = 0; i < tempIpms.size(); i++) {
					TempTSC tempIpm =tempIpms.get(i);
					boolean tep=false;
					for(int j=0; j<tempIpm.getValue().size();j++){
						if(tempIpm.getValue().get(j)==temp){
							count=tempIpm.getKey();
							tep=true;
							break;
						}
					}
					if(tep){
						break;
					}
				}
				
				for(int i = 0; i < IPMSendList.size(); i++){
					IPMInfoVO ipm=(IPMInfoVO)IPMSendList.get(i);
					if(ipm.getRecordType()==2){
						if(count==ipm.getIndexMin()){
							boolean tep=isExceedIpmMaxRecordNum(0,ipm);
							if(tep){
								for(int j=0;j<IPMSendList.size();j++){
									IPMInfoVO ip = (IPMInfoVO) IPMSendList.get(j);
									if(ip.getRecordType()==2){
										boolean tp=isExceedIpmMaxRecordNum(0,ip);
										if(!tp){
											count=ip.getIndexMin();
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			
			for(int i=0; i<ipmIndexList.size(); i++) {
				if(!numberIsList(count,ipmIndexList)) {
					ipmIndex = count;
					isGetIndex = true;
					break;
				}
				count++;
			}
			
			if (!isGetIndex) {
				if(ipmIndexList.size() < sysInfoVO.getMaxAutoRecordNum()) {
					ipmIndex = count;
				}
			}
			
			
		} catch (DaoException e) {
			log.error("getIPMIndex Error: " + e.getMessage());
		}
    	
    	
    	return ipmIndex;
    } 
    /**
     * 取得该频点的ias 通道号
     * @param freq
     * @return
     */
    public static int getTableIASIndex(int freq){
    	int iasIndex=1;
    	Statement statement = null;
		Connection conn = null;
		ResultSet rs=null;
		String sql="select max(IpmIndex) from channelremapping where freq = "+freq;
		try {
			conn=DaoSupport.getJDBCConnection();
			statement=conn.createStatement();
			rs=statement.executeQuery(sql);
			while(rs.next()){
				String tempIndex =rs.getString("max(IpmIndex)");
				if(tempIndex!=null){
					iasIndex=Integer.parseInt(tempIndex.trim());
				}
			}
			
		} catch (Exception e) {
			log.error("查询数据库ias通道错误:"+e.getMessage());
			log.error("查询数据库ias通道sql:"+sql);
		}finally{
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库资源错误:"+e.getMessage());
			}
		}
    	return iasIndex;
    }
    
    
    public static int getTSCIndex(int isHDProgram,int freq) {
    	int tscIndex = 0;
    	
    	try {
			List tscIndexList = getTSCIndexList();
			
			boolean isGetIndex = false;
			int count = 1;
			
			if(isHDProgram == 1) {
				// 高清节目处理
				TSCInfoVO tsc = getBestTscInfo();
				count = tsc.getIndexMin();
			}else{
				//获取该频点所在那个tsc 并且返回该tsc使用到的最大通道号
				//Ji Long 2011-06-26
				int temp=getMinIndexTsc(freq);
				
				List<TempTSC> tempTscs=new ArrayList<TempTSC>();
				try {
					for (int i = 0; i < TSCSendList.size(); i++) {
						TSCInfoVO tsc=(TSCInfoVO)TSCSendList.get(i);
						TempTSC tempTsc =new TempTSC();
						List<Integer> tempList=new ArrayList<Integer>();
						for(int j=0;j<=tsc.getIndexMax()-tsc.getIndexMin();j++){
							tempList.add(tsc.getIndexMin()+j);
						}
						tempTsc.setKey(tsc.getIndexMin());
						tempTsc.setValue(tempList);
						tempTscs.add(tempTsc);
					}
					
				} catch (Exception e) {
					if(TSCSendList==null||TSCSendList.size()==0){
						TSCSendList=coreData.getTSCList();
					}
					for (int i = 0; i < TSCSendList.size(); i++) {
						TSCInfoVO tsc=(TSCInfoVO)TSCSendList.get(i);
						TempTSC tempTsc =new TempTSC();
						List<Integer> tempList=new ArrayList<Integer>();
						for(int j=0;j<=tsc.getIndexMax()-tsc.getIndexMin();j++){
							tempList.add(tsc.getIndexMin()+j);
						}
						tempTsc.setKey(tsc.getIndexMin());
						tempTsc.setValue(tempList);
						tempTscs.add(tempTsc);
					}
				}
				
				
				for (int i = 0; i < tempTscs.size(); i++) {
					TempTSC tempTsc =tempTscs.get(i);
					boolean tep=false;
					for(int j=0; j<tempTsc.getValue().size();j++){
						if(tempTsc.getValue().get(j)==temp){
							count=tempTsc.getKey();
							tep=true;
							break;
						}
					}
					if(tep){
						break;
					}
				}
				
				for(int i = 0; i < TSCSendList.size(); i++){
					TSCInfoVO tsc=(TSCInfoVO)TSCSendList.get(i);
					if(count==tsc.getIndexMin()){
						boolean tep=isExceedTscMaxRecordNum(0,tsc);
						if(tep){
							for(int j=0;j<TSCSendList.size();j++){
								TSCInfoVO ts = (TSCInfoVO) TSCSendList.get(j);
								boolean tp=isExceedTscMaxRecordNum(0,ts);
								if(!tp){
									count=ts.getIndexMin();
									break;
								}
							}
						}
					}
				}
			}
			
			for(int i=0; i<tscIndexList.size(); i++) {
				if(!numberIsList(count,tscIndexList)) {
					tscIndex = count;
					isGetIndex = true;
					break;
				}
				count++;
			}
			
			if (!isGetIndex) {
				if(tscIndexList.size() < sysInfoVO.getMaxAutoRecordNum()) {
					tscIndex = count;
				}
			}
			
			
		} catch (DaoException e) {
			log.error("getTSCIndex Error: " + e.getMessage());
		}
    	
    	
    	return tscIndex;
    }
    //TODO
    /**
     * 取得没有分配的TSCIndex通道号
     * @param int isHDProgram 需要分配的是否为高清节目 1:高清 0:标清
     * @return
     
    public static int getTSCIndex(int isHDProgram) {
    	int tscIndex = 0;
    	String str="";
    	
    	try {
			List tscIndexList = getTSCIndexList();
			boolean isGetIndex = false;
			int count = 1;
			if(isHDProgram == 1) {
				// 高清节目处理
				TSCInfoVO tsc = getBestTscInfo();
				count = tsc.getIndexMin();
				str="高清";
			}else{
				TSCInfoVO tsc = getTsc();
				count = tsc.getIndexMin();
				str="标清";
			}
			
			for(int i=0; i<tscIndexList.size(); i++) {
				//if(count != Integer.valueOf((Integer)tscIndexList.get(i))) {
				//if(!numberIsList(count,tscIndexList)&&numberIsList(count,tempList)) {
				if(!numberIsList(count,tscIndexList)) {
					tscIndex = count;
					isGetIndex = true;
					break;
				}
				count++;
			}
			
			if (!isGetIndex) {
				if(tscIndexList.size() < sysInfoVO.getMaxAutoRecordNum()) {
					tscIndex = count;
				}
			}
			
			
		} catch (DaoException e) {
			log.error("getTSCIndex Error: " + e.getMessage());
		}
    	
    	log.info("本次通道分配情况："+tscIndex+str);
    	return tscIndex;
    }
    */
    
    /**
     * 查询集合中是否包含某个元素
     * 包含 true
     * 不包含 false
     */
    private  static boolean numberIsList(Integer number,List list){
    	boolean falg=false;
    	for(int i=0;i<list.size();i++){
    		if(number==(Integer)list.get(i)){
    			falg=true;
    			break;
    		}
    	}
    	return falg;
    }
    
    /**
     * 新加方法，若自动录像节目为标清 也平均分配
     * 取得最佳分配高清节目的IPM
     * @return
     * 
     * JI  LONG 2011-5-18
     */
    private static int getMinIndexIpm(int freq) {
    	
    	StringBuffer strBuff = new StringBuffer();
    	int ipmIndex=1;
    	Statement statement = null;
    	Connection conn=null;
    	try {
    		conn = DaoSupport.getJDBCConnection();
    	} catch (DaoException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
    	
    	ResultSet rs = null;
    	
    	strBuff.append("SELECT max(IpmIndex) FROM channelremapping where freq = "+freq+";");
    	
    	try {
    		statement = conn.createStatement();
    		
    		rs = statement.executeQuery(strBuff.toString());
    		
    		// 取得TSC通道号列表
    		while(rs.next()){
    			String str=rs.getString("max(IpmIndex)");
    			if(str!=null){
    				ipmIndex=Integer.parseInt(rs.getString("max(IpmIndex)"));
    			}
    		}
    	} catch (Exception e) {
    		log.error("获取错误1标清节目ipm通道错误: " + e.getMessage());
    		log.error("获取错误1标清节目ipm通道 SQL: " + strBuff.toString());
    	} finally {
    		try {
    			DaoSupport.close(rs);
    			DaoSupport.close(statement);
    			DaoSupport.close(conn);
    		} catch (DaoException e) {
    			log.error("关闭获取标清节目ipm通道资源错误: " + e.getMessage());
    		}
    	}
    	strBuff = null;
    	return ipmIndex;
    }
    /**
     * 新加方法，若自动录像节目为标清 也平均分配
     * 取得最佳分配高清节目的TSC
     * @return
     * 
     * JI  LONG 2011-5-18
     */
    private static int getMinIndexTsc(int freq) {

		StringBuffer strBuff = new StringBuffer();
		int tscIndex=1;
		Statement statement = null;
		Connection conn=null;
		try {
			conn = DaoSupport.getJDBCConnection();
		} catch (DaoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ResultSet rs = null;
		
		strBuff.append("SELECT max(TscIndex) FROM channelremapping where freq = "+freq+";");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 取得TSC通道号列表
			while(rs.next()){
				String str=rs.getString("max(TscIndex)");
				if(str!=null){
					tscIndex=Integer.parseInt(rs.getString("max(TscIndex)"));
				}
			}
		} catch (Exception e) {
			log.error("获取错误1标清节目tsc通道错误: " + e.getMessage());
			log.error("获取错误1标清节目tsc通道 SQL: " + strBuff.toString());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭获取标清节目tsc通道资源错误: " + e.getMessage());
			}
		}
		strBuff = null;
		return tscIndex;
	}
    
    /**
     * 取得最佳分配高清节目的TSC
     * @return
     */
    private static TSCInfoVO getBestTscInfo() {
    	TSCInfoVO minvo = null;
		// 从TSC设备列表里面取得, 最应该分配的设备
		try {
			// 取得每个TSC设备当前的高清节目数
			TSCSendList = getTscHDNums(TSCSendList);
		} catch (DaoException e2) {
		}
		for (int t = 0; t < TSCSendList.size(); t++) {
			TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);
			if (minvo == null) {
				minvo = tsc;
			}
			if (minvo.getHDNums() > tsc.getHDNums()) {
				minvo = tsc;
			} 
		}
		//log.info("取得最佳分配高清节目 IndexMin: " + minvo.getIndexMin() + " IndexMax: " + minvo.getIndexMax() + " HDCount: " + minvo.getHDNums());
		boolean flag=isExceedTscMaxRecordNum(1,minvo);
		if(flag){
			for(int i=0;i<TSCSendList.size();i++){
				TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(i);
				boolean temp=isExceedTscMaxRecordNum(1,tsc);
				if(!temp){
					minvo=tsc;
					break;
				}
			}
		}
    	return minvo;
    }
    /**
     * 取得最佳分配高清节目的IPM
     * @return
     */
    private static IPMInfoVO getBestIpmInfo() {
    	IPMInfoVO minvo = null;
    	// 从IPM设备列表里面取得, 最应该分配的设备
    	try {
    		// 取得每个IPM设备当前的高清节目数
    		IPMSendList = getIpmHDNums(IPMSendList);
    		
    	} catch (DaoException e2) {
    	}
    	
    	
    	for (int t = 0; t < IPMSendList.size(); t++) {
	    	IPMInfoVO ipm = (IPMInfoVO) IPMSendList.get(t);
	    	if(ipm.getRecordType()==2){
	    		if (minvo == null) {
	    			minvo = ipm;
	    		}
	    		if (minvo.getHDNums() > ipm.getHDNums()) {
	    			minvo = ipm;
	    		} 
    		}
    	}
    	//log.info("取得最佳分配高清节目 IndexMin: " + minvo.getIndexMin() + " IndexMax: " + minvo.getIndexMax() + " HDCount: " + minvo.getHDNums());
    	boolean flag=isExceedIpmMaxRecordNum(1,minvo);
    	if(flag){
    		for(int i=0;i<IPMSendList.size();i++){
    			IPMInfoVO ipm = (IPMInfoVO) IPMSendList.get(i);
    			if(ipm.getRecordType()==2){
	    			boolean temp=isExceedIpmMaxRecordNum(1,ipm);
	    			if(!temp){
	    				minvo=ipm;
	    				break;
	    			}
    			}
    		}
    	}
    	return minvo;
    }
    
    /**
     * 返回该ipm加上该节目后是否超过最大录像资源
     * isHD 该节目是否高清（一个高清则为5个标清）
     *  返回参数flag ,true超过 false未超过
     *  Ji Long  2011-08-03 
     */
    private static  boolean isExceedIpmMaxRecordNum(int isHD,IPMInfoVO ipm){
    	boolean flag=false;
    	Statement statement = null;
    	Connection conn=null;
    	ResultSet rs = null;
    	/*
    	//整个前端最多录像标清节目数  一个高清等于五个标清
    	int MaxAutoRecordNum=coreData.getSysVO().getMaxAutoRecordNum();
    	//整个前端ipm的个数  只要一对一监测的
    	int IpmNum=0;
    	List IPMList=coreData.getIPMList();
    	for (int i = 0; i < IPMList.size(); i++) {
    		IPMInfoVO ip=(IPMInfoVO)IPMList.get(i);
    		if(ip.getRecordType()==2){
    			IpmNum++;
    		}
		}
    	//每个ipm最多录制多少套标清  一个高清等于五个标清
    	int ipmMaxRecordNum=MaxAutoRecordNum/IpmNum;
    	*/
    	//TODO 重新计算 每个ipm 处理一对一的个数
    	
    	int ipmMaxRecordNum=(ipm.getIndexMax()-ipm.getIndexMin())+1;
    	
    	String sql=" SELECT HDFlag FROM channelremapping  where IpmIndex >= "+ipm.getIndexMin()+" and IpmIndex <= "+ipm.getIndexMax()+" ;";
    	int count=0;
    	try {
    		conn=DaoSupport.getJDBCConnection();
    		statement=conn.createStatement();
    		rs=statement.executeQuery(sql);
    		while(rs.next()){
    			int HDFlag=rs.getInt("HDFlag");
    			int temp=1;
    			if(HDFlag==1){
    				temp=5;
    			}
    			count+=temp;
    		}
    		//加上本次分配的节目  高清加5  标清加1
    		if(isHD==1){
    			count+=5;
    		}else{
    			count+=1;
    		}
    	} catch (Exception e) {
    		log.info("查询ipm录像个数错误："+e.getMessage());
    		log.info("查询ipm录像个数SQL："+sql);
    	} finally {
    		try {
    			DaoSupport.close(rs);
    			DaoSupport.close(statement);
    			DaoSupport.close(conn);
    		} catch (Exception e2) {
    			log.info("关闭查询tsc录像个数资源错误："+e2.getMessage());
    		}
    	}
    	//如果本次分配的 节目数 加库中的节目数  大于 tsc录制的最大节目数
    	if(count > ipmMaxRecordNum ){
    		flag=true;
    	}
    	return flag;
    }
    /**
     * 返回该tsc加上该节目后是否超过最大录像资源
     * isHD 该节目是否高清（一个高清则为5个标清）
     *  返回参数flag ,true超过 false未超过
     *  Ji Long  2011-08-03 
     */
    private static  boolean isExceedTscMaxRecordNum(int isHD,TSCInfoVO tsc){
    	boolean flag=false;
    	Statement statement = null;
		Connection conn=null;
		ResultSet rs = null;
		/*
		
    	//整个前端最多录像标清节目数  一个高清等于五个标清
    	int MaxAutoRecordNum=coreData.getSysVO().getMaxAutoRecordNum();
    	//整个前端TSC的个数
    	int TscNum=coreData.getTSCList().size();
    	//每个tsc最多录制多少套标清  一个高清等于五个标清
    	int tscMaxRecordNum=MaxAutoRecordNum/TscNum;
		 */
    	
    	//TODO 重新计算每台tac的 录像个数
    	int tscMaxRecordNum=(tsc.getIndexMax()-tsc.getIndexMin())+1;
    	
    	String sql=" SELECT HDFlag FROM channelremapping  where TscIndex >= "+tsc.getIndexMin()+" and TscIndex <= "+tsc.getIndexMax()+" ;";
    	int count=0;
    	try {
    		conn=DaoSupport.getJDBCConnection();
    		statement=conn.createStatement();
    		rs=statement.executeQuery(sql);
    		while(rs.next()){
    			int HDFlag=rs.getInt("HDFlag");
    			int temp=1;
    			if(HDFlag==1){
    				temp=5;
    			}
    			count+=temp;
    		}
    		//加上本次分配的节目  高清加5  标清加1
    		if(isHD==1){
    			count+=5;
    		}else{
    			count+=1;
    		}
		} catch (Exception e) {
			log.info("查询tsc录像个数错误："+e.getMessage());
			log.info("查询tsc录像个数SQL："+sql);
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (Exception e2) {
				log.info("关闭查询tsc录像个数资源错误："+e2.getMessage());
			}
		}
		//如果本次分配的 节目数 加库中的节目数  大于 tsc录制的最大节目数
		if(count > tscMaxRecordNum ){
			flag=true;
		}
    	return flag;
    }
    
    /**
     * 取得TSC通道号列表
     * @return List
     * @throws DaoException
     */
    private static List getTSCIndexList() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		List tscIndexList = new ArrayList();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		strBuff.append("select * from channelremapping where StatusFlag != 0 and TscIndex != 0  order by tscIndex, channelindex");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// 取得TSC通道号列表
			while(rs.next()){
				tscIndexList.add(Integer.parseInt(rs.getString("TscIndex")));
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误1: " + e.getMessage());
			log.error("自动录像 取得节目相关通道错误1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		
		return tscIndexList;
	}
    
    /**
     * 取得IPM通道号列表
     * @return List
     * @throws DaoException
     */
    private static List getIPMIndexList() throws DaoException {
    	
    	StringBuffer strBuff = new StringBuffer();
    	List IpmIndexList = new ArrayList();
    	
    	Statement statement = null;
    	Connection conn = DaoSupport.getJDBCConnection();
    	
    	ResultSet rs = null;
    	
    	strBuff.append("select * from channelremapping where StatusFlag != 0 and IpmIndex != 0  order by IpmIndex, channelindex");
    	
    	try {
    		statement = conn.createStatement();
    		
    		rs = statement.executeQuery(strBuff.toString());
    		
    		// 取得TSC通道号列表
    		while(rs.next()){
    			IpmIndexList.add(Integer.parseInt(rs.getString("IpmIndex")));
    		}
    	} catch (Exception e) {
    		log.error("自动录像 取得节目相关通道错误1: " + e.getMessage());
    		log.error("自动录像 取得节目相关通道错误1 SQL: " + strBuff.toString());
    	} finally {
    		DaoSupport.close(rs);
    		DaoSupport.close(statement);
    		DaoSupport.close(conn);
    	}
    	
    	strBuff = null;
    	
    	return IpmIndexList;
    }
    
    /**
     * 取得TSC设备列表中当前标清节目数
     * @return List
     * @throws DaoException
     */
    private static List getTscTVNums(List TSCSendList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for (int t = 0; t < TSCSendList.size(); t++) {
			TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);

			StringBuffer strBuff = new StringBuffer();

			ResultSet rs = null;

			strBuff.append("select count(*) from channelremapping where StatusFlag != 0 and TscIndex >= " + tsc.getIndexMin());
			strBuff.append(" and TscIndex <= " + tsc.getIndexMax() + " and HDFlag =0");
			
			try {
				statement = conn.createStatement();

				rs = statement.executeQuery(strBuff.toString());

				// 取得TSC通道号列表
				while (rs.next()) {
					tsc.setTVNums(Integer.parseInt(rs.getString("count(*)")));
					//log.info("getTscTVNums IndexMin: " + tsc.getIndexMin() + " IndexMax: " + tsc.getIndexMax() + " HDCount: " + tsc.getTVNums());
					break;
				}
			} catch (Exception e) {
				log.error("取得TSC设备当前高清节目数错误1: " + e.getMessage());
				log.error("取得TSC设备当前高清节目数错误1 SQL: " + strBuff.toString());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}

			strBuff = null;
		}
		DaoSupport.close(conn);
		return TSCSendList;
	}
    
    
    

    /**
     * 取得IPM设备列表中当前高清节目数
     * @return List
     * @throws DaoException
     */
    private static List getIpmHDNums(List IPMSendList) throws DaoException {
    	
    	Statement statement = null;
    	Connection conn = DaoSupport.getJDBCConnection();
    	
    	for (int t = 0; t < IPMSendList.size(); t++) {
    		IPMInfoVO ipm = (IPMInfoVO) IPMSendList.get(t);
    		//只有做一对一监视时 才 给予平均分配
    		if(ipm.getRecordType()==2){
	    		StringBuffer strBuff = new StringBuffer();
	    		
	    		ResultSet rs = null;
	    		
	    		strBuff.append("select count(*) from channelremapping where StatusFlag != 0 and IpmIndex >= " + ipm.getIndexMin());
	    		strBuff.append(" and IpmIndex <= " + ipm.getIndexMax() + " and HDFlag =1");
	    		
	    		try {
	    			statement = conn.createStatement();
	    			
	    			rs = statement.executeQuery(strBuff.toString());
	    			
	    			// 取得TSC通道号列表
	    			while (rs.next()) {
	    				ipm.setHDNums(Integer.parseInt(rs.getString("count(*)")));
	    				//log.info("getTscHDNums IndexMin: " + tsc.getIndexMin() + " IndexMax: " + tsc.getIndexMax() + " HDCount: " + tsc.getHDNums());
	    				break;
	    			}
	    		} catch (Exception e) {
	    			log.error("取得Ipm设备当前高清节目数错误1: " + e.getMessage());
	    			log.error("取得Ipm设备当前高清节目数错误1 SQL: " + strBuff.toString());
	    		} finally {
	    			DaoSupport.close(rs);
	    			DaoSupport.close(statement);
	    		}
	    		strBuff = null;
    		}
    	}
    	DaoSupport.close(conn);
    	return IPMSendList;
    }
    /**
     * 取得TSC设备列表中当前高清节目数
     * @return List
     * @throws DaoException
     */
    private static List getTscHDNums(List TSCSendList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for (int t = 0; t < TSCSendList.size(); t++) {
			TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);

			StringBuffer strBuff = new StringBuffer();

			ResultSet rs = null;

			strBuff.append("select count(*) from channelremapping where StatusFlag != 0 and TscIndex >= " + tsc.getIndexMin());
			strBuff.append(" and TscIndex <= " + tsc.getIndexMax() + " and HDFlag =1");
			
			try {
				statement = conn.createStatement();

				rs = statement.executeQuery(strBuff.toString());

				// 取得TSC通道号列表
				while (rs.next()) {
					tsc.setHDNums(Integer.parseInt(rs.getString("count(*)")));
					//log.info("getTscHDNums IndexMin: " + tsc.getIndexMin() + " IndexMax: " + tsc.getIndexMax() + " HDCount: " + tsc.getHDNums());
					break;
				}
			} catch (Exception e) {
				log.error("取得TSC设备当前高清节目数错误1: " + e.getMessage());
				log.error("取得TSC设备当前高清节目数错误1 SQL: " + strBuff.toString());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}

			strBuff = null;
		}
		DaoSupport.close(conn);
		return TSCSendList;
	}
    
	public MSGHeadVO getBsData() {
		return bsData;
	}

	public void setBsData(MSGHeadVO bsData) {
		this.bsData = bsData;
	}

	public String getDownString() {
		return downString;
	}

	public void setDownString(String downString) {
		this.downString = downString;
	}

	public UtilXML getUtilXML() {
		return utilXML;
	}

	public void setUtilXML(UtilXML utilXML) {
		this.utilXML = utilXML;
	}
}
