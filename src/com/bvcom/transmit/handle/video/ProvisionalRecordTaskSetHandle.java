package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.rec.ProvisionalRecordTaskSetParse;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.task.RecordTaskThread;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

/**
 * 任务录像设置
 * @author Bian Jiang
 *
 */
public class ProvisionalRecordTaskSetHandle {
    
    private static Logger log = Logger.getLogger(ProvisionalRecordTaskSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public ProvisionalRecordTaskSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    public ProvisionalRecordTaskSetHandle() {

    }
    
    /**
     * 1. 分别下发给SMG和TSC
     * 2. 下发成功，返回成功
     * 3. 向中心上报成功信息
     *
     */
    public void downXML() {
        // 返回数据
        String upString = "";
        
        List SMGList = new ArrayList();
//        List TSCList = coreData.getTSCList();//tsc的列表信息
//        List TSCList = new ArrayList();//tsc的列表信息
        List channelSendList = new ArrayList();// SMG downChannelList Index List
        
    	this.downString = this.downString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
    	this.downString = this.downString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
    	
        String smgDownString = this.downString;
        String tscDownString = this.downString;
        // 判断是否分配到channel
        boolean isNoMoreChannel = false;
        
        SysInfoVO sysInfoVO = coreData.getSysVO();
        List TSCSendList = coreData.getTSCList();//tsc的列表信息
        
        SetAutoRecordChannelHandle setAutoRecordChannelHandle = new SetAutoRecordChannelHandle();
        SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
       
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("任务录像StringToXML Error: " + e.getMessage());
        }
       
        ProvisionalRecordTaskSetParse RecordTaskSet = new ProvisionalRecordTaskSetParse();
        List<ProvisionalRecordTaskSetVO> RecordTaskSetList = RecordTaskSet.getIndexByDownXml(document);
        
        int index = 0;
        int indexOrg = 0;
        // 取得下发SMG URL列表信息
        SetAutoRecordChannelVO recordVO = null;
        
    	int channelIndex = 0;
        int channel = 0;
        
        for(int i= 0; i<RecordTaskSetList.size(); i++) {
            ProvisionalRecordTaskSetVO vo = RecordTaskSetList.get(i);
        	
	       	if(vo.getAction().equals("Set")) {
	       		// 任务录像设置
				indexOrg = vo.getIndex();
				try {
					vo = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
					index = vo.getIndex();
					channelIndex = vo.getDevIndex();
				} catch (DaoException e1) {
				
				}
				//不是一对一的节目
				if(index == 0) {
					// 从数据库中自动找一个没有用过的通道号
					recordVO = new SetAutoRecordChannelVO();
					recordVO.setFreq(vo.getFreq());
					recordVO.setServiceID(vo.getServiceID());
					recordVO.setAudioPID(vo.getAudioPID());
					recordVO.setVideoPID(vo.getVideoPID());
					recordVO.setSymbolRate(vo.getSymbolRate());
					
					try {
						recordVO.setQAM(Integer.parseInt(vo.getQAM()));
					} catch (Exception ex) {
						recordVO.setQAM(64);
					}
					
					try {
						
						recordVO = SetAutoRecordChannelHandle.getHDFlagByProgram(recordVO);
						//在所有一对一板卡中分配板卡资源
						setAutoRecordChannelHandle.GetChannelRemappingbyFreq(recordVO);
						if(recordVO.getIndex() == 0) {
							isNoMoreChannel = true;
							log.error("任务录像通道出错, 没有分配到相关的通道号");
							break;
						}
						// 任务录像 0：不录像，1:代表故障触发录制   2：24小时录像(默认) 3: 任务录像
						recordVO.setRecordType(3);

	    				 // 取得TSCIndex通道号 By: Bian Jiang 2011.4.8
						recordVO.setTscIndex(SetAutoRecordChannelHandle.getTSCIndex(recordVO.getHDFlag(),recordVO.getFreq()));
	    				 
						//任务录像节目更新一对一映射表：设置RecordType =3 
						setAutoRecordChannelHandle.upChannelRemappingIndex(recordVO);
						
						vo.setIndex(recordVO.getIndex());
						vo.setDevIndex(recordVO.getDevIndex());
					 	channelIndex = vo.getDevIndex();
					} catch (DaoException e) {
						log.error("取得任务录像通道出错: " + e.getMessage());
					}
				}
				
				tscDownString = tscDownString.replaceAll("Index=\"" + indexOrg + "\"", "Index=\"" + vo.getIndex() + "\"");
	       	} else {
	       		// 任务录像删除
	       		recordVO = new SetAutoRecordChannelVO();
	       		try {
	       			recordVO.setFreq(vo.getFreq());
	       			recordVO.setServiceID(vo.getServiceID());
	       			//清除一对一映射表内容
	       			recordVO = setAutoRecordChannelHandle.delRecordTaskIndex(recordVO);
	       			channelIndex = recordVO.getDevIndex();
	       			//清除更新任务录像表
					updateRecordTaskIndex(vo);
					log.info("任务录像删除成功: Freq:" + vo.getFreq() + " ServiceID:" + vo.getServiceID());
				} catch (DaoException e) {
					log.error("任务录像删除出错: " + e.getMessage());
				}
	       	}
        	
	       	
	       	//获取任务录像协议中有多少个板卡通道
			if (channel != channelIndex && channelIndex != 0) {
				channelSendList.add(channelIndex);
				channel = channelIndex;
			} else {
				channel = channelIndex;
			}
			//获取通道对应的SMG板卡对象信息
            CommonUtility.checkSMGChannelIndex(vo.getDevIndex(), SMGList);
//            CommonUtility.checkTSCChannelIndex(vo.getIndex(), TSCList);
        }
        
        if (channelSendList.size() != 0 && !isNoMoreChannel) {
        	try {
        		SetAutoRecordChannelHandle SetAutoRecordChannel = new SetAutoRecordChannelHandle();
        		List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = SetAutoRecordChannel.GetProgramInfoByIndex(channelSendList, true);
        		
	        	smgDownString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set", true);
        		
			} catch (Exception e) {
				log.error("通过通道取得节目信息出错: " + e.getMessage());
			}
        }
        
        if(!isNoMoreChannel) {
            // SMG 下发指令
            for (int i=0; i< SMGList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGList.get(i);
                try {
                    // 任务录像信息下发 timeout 1000*30 三十秒

                    utilXML.SendDownNoneReturn(smgDownString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                } catch (CommonException e) {
                    log.error("任务录像向SMG下发任务录像出错：" + smg.getURL());
                }
                try {
                	SetAutoRecordChannelHandle.updateSMGURLByDevIndex(smg.getIndex(), smg.getURL());
                } catch (Exception ex) {
                	
                }
            } // SMG 下发指令 END
            
            // TSC 下发指令：给所有的TSC都下发任务录像
            for (int i=0; i< TSCSendList.size(); i++) {
                TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(i);
                try {
            		// 任务录像信息下发 timeout 1000*30 三十秒
                    utilXML.SendDownNoneReturn(tscDownString, tsc.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                } catch (CommonException e) {
                    log.error("任务录像向TSC下发任务录像出错：" + tsc.getURL());
                }
            } // TSC 下发指令 END
            upString = RecordTaskSet.ReturnXMLByURL(bsData, 0);
        } 
        //没有可使用的板卡资源
        else 
        {
        	upString = RecordTaskSet.ReturnXMLByURL(bsData, 1);
        }

        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("任务录像选台信息失败: " + e.getMessage());
        }
        
        //保存任务录制信息
        if(!isNoMoreChannel) {
        	this.insertNewTaskRecordTable(RecordTaskSetList);	
        }
        
        
        //BY TQY 2012-05-20  1： 监视任务录像是否过期 2：在程序启动时同样也需要监视任务录制信息
        // 任务录像处理, 删除已经过期的任务录像
        //RecordTaskThread RecordTaskProcess = new RecordTaskThread();
        //RecordTaskProcess.start();
        
        bsData = null;
        this.downString = null;
        SMGList = null;
        utilXML = null;
        RecordTaskSet = null;
        
    }
    
    /**
     * 任务录像不用记录入库(目前不用这个函数)
     * 任务录像状态入库
     * @param RecordTaskSetList
     */
    private void insertNewTaskRecordTable(List RecordTaskSetList) {
        
        Statement statement = null;
        Connection conn = null;
        StringBuffer sqlBuff = null;
        try {
            conn = DaoSupport.getJDBCConnection();
            statement = conn.createStatement();
            downString = downString.replaceAll("'", "\"");
            for(int i= 0; i<RecordTaskSetList.size(); i++) {
                ProvisionalRecordTaskSetVO vo = (ProvisionalRecordTaskSetVO)RecordTaskSetList.get(i);
                sqlBuff = new StringBuffer();
                sqlBuff.append("insert into taskrecord (Taskid, tr_action, tr_index, url, lasttime, xml, ExpireDays, Freq, ServiceID, EndDateTime, statusFlag) values ('");
                sqlBuff.append(vo.getTaskID() + "', ");
                sqlBuff.append("'" + vo.getAction() + "', ");
                sqlBuff.append(vo.getIndex() + ", ");
                
                //TODO 这个URL是自己生成的
                sqlBuff.append("'" + vo.getURL() + "', ");
                
                sqlBuff.append("'" + CommonUtility.getDateTime() + "', ");
                sqlBuff.append("'" + downString + "', ");
                sqlBuff.append("'" + vo.getExpireDays() + "' , ");
                sqlBuff.append("'" + vo.getFreq() + "' , ");
                sqlBuff.append("'" + vo.getServiceID() + "', ");
                sqlBuff.append("'" + vo.getEndDateTime() + "', ");
                sqlBuff.append("1)");
                
                statement.executeUpdate(sqlBuff.toString());
            }
            
        } catch (Exception e) {
        	log.error("错误SQL: " + sqlBuff.toString());
        	log.error("任务录像更新数据库错误: " + e.getMessage());
        } finally {
            try {
                DaoSupport.close(statement);
                DaoSupport.close(conn);
            } catch (DaoException e) {
            }
        }
        log.info("任务录像更新数据库成功!");
    }
    
    /**
     * 删除任务录像表相关的信息
     * @param vo
     * @throws DaoException
     */
    public static void updateRecordTaskIndex(ProvisionalRecordTaskSetVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.taskrecord c set ");
		strBuff.append(" statusFlag = 0 ");
		strBuff.append(" where Taskid = " + vo.getTaskID());
		
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
		log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public List selectRunTaskList()  throws DaoException {
    	
    	List recordTaskList = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		Connection conn;
		
		try {
			conn = DaoSupport.getJDBCConnection();
		
			try {
				StringBuffer strBuff = new StringBuffer();
				strBuff.append("select * from taskrecord where statusFlag = 1 ");
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				while(rs.next()){
					ProvisionalRecordTaskSetVO vo = new ProvisionalRecordTaskSetVO();
					vo.setTaskID(rs.getString("Taskid"));
					vo.setFreq(Integer.parseInt(rs.getString("Freq")));
					vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
					vo.setExpireDays(Integer.parseInt(rs.getString("ExpireDays")));
					vo.setLasttime(rs.getString("lasttime"));
					vo.setEndDateTime(rs.getString("EndDateTime"));
					recordTaskList.add(vo);
				}
				
			} catch (Exception e) {
				log.error("任务录像查询数据库错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		} catch (DaoException e1) {
			log.error("任务录像查询数据库错误: " + e1.getMessage());
		}
    	
    	return recordTaskList;
    }
    
}
