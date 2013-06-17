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

public class StopPlayingVideoHandle {
	
    private static Logger log = Logger.getLogger(StopPlayingVideoHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public StopPlayingVideoHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    public StopPlayingVideoHandle(){
    	
    }
    
    
    private List SMGSendList = new ArrayList();//SMG的列表信息
    
    /**
     * 1. 取得RTVM所有的地址
     * 2. 把视频停止协议发给所有rtvm
     */
    @SuppressWarnings({ "unchecked", "static-access" })
	public void downXML() {

    	 SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
    	 SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
    	 List channelSendList = new ArrayList();//Channel Index List
         int channel = 0;
         
    	 SMGSendList = new ArrayList();
    	 
    	//更改一对一实时视频表的业务类型状态recordtype=4
    	// 取得马赛克相关的节目信息
		@SuppressWarnings("unused")
		//1：需要获取RECORDTYPE!=4，而且RECORDTYPE=2的节目信息，重新下发指令给板卡
		List<SetAutoRecordChannelVO> voList = getProgramInfoByIndex();

		try{
			//2012-07-20新增加删除SMG的节目信息
			for(int i=0; i< voList.size(); i++)  {//得到解析后的xml的对象数组中的一个操作
	        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)voList.get(i);
	        	@SuppressWarnings("unused")
				int channelIndex = (int)vo.getIndex();
	        
	        	 // 删除通道使用标记 TODO 删除返回通道有问题
				handle.GetIndexByProgram(vo, true);
				channelIndex = vo.getDevIndex();
				handle.updateDelFlagChannelRemappingByProgram(vo);
				
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
	
				//*************下发smg(需要给哪些板卡发信息)**************
				CommonUtility.checkSMGChannelIndex(channelIndex, SMGSendList);
	   		
			}
		}catch(Exception ex){
			
		}
		String url = "";
		String smgDownString="";
		
		if (channelSendList.size() != 0) {
				try{
					List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = handle.GetProgramInfoByIndex(channelSendList, true);
					smgDownString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set", true);
					
	        		for(int l=0;l<SMGSendList.size();l++)
	  		        {
	  		            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(l);
	  		            try {
	  		                if (!url.equals(smg.getURL().trim()))
	  		                {
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
	  		                log.error("下发马赛克自动录像到SMG出错：" + smg.getURL());
	  		            }
	  		        }
				}catch(Exception ex){
					
				}
		}
		
		//同时更新一对一节目表，将recordtype=4 修改为初始状态
		//BY TQY 监测四期
		updateMosaicChannelMapping(voList);
		
    	// 返回数据
        @SuppressWarnings("unused")
		String upString = "";
        boolean isErr = false;
        List<MonitorProgramQueryVO> rtvsList =new ArrayList<MonitorProgramQueryVO>();
        try {
        	rtvsList = MonitorProgramQueryHandle.GetChangeProgramInfoList(rtvsList);
		} catch (DaoException e1) {
			isErr = true;
		}
        
		//给RTVM下发视频停止命令
        if (isErr) {
            // 失败
            upString = utilXML.getReturnXML(bsData, 1);
        } else {
        	for (int i = 0; i < rtvsList.size(); i++) {
		        try {
		        	upString = utilXML.SendDownXML(this.downString, rtvsList.get(i).getRTVSResetURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
		        } catch (CommonException e) {
		            isErr = true;
		        }
        	}
        }
        
        //BY TQY 
     	//给IPM下发给停止上报轮播节目信息
    	List ipmList=coreData.getIPMList();
    	for(int i=0;i<ipmList.size();i++){
    		IPMInfoVO ipm = (IPMInfoVO) ipmList.get(i);
    		if(ipm.getRecordType()==3){
    			try {
				  upString=utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
				} catch (CommonException e) {
					log.error("马赛克轮训下发多画错误："+e.getMessage());
					isErr = true;
				}
    		}
    	}
    	//by tqy 2012-07-09 增加删除轮播板卡信息
    	
    	
        String returnstr="";
        if(isErr){
        	returnstr = utilXML.getReturnXML(this.bsData, 1);
        	try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
            }
        }else{
        	returnstr = utilXML.getReturnXML(this.bsData, 0);
        	try {
                utilXML.SendUpXML(returnstr, bsData);
            } catch (CommonException e) {
                log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
            }
        }
        
        bsData = null;
        this.downString = null;
        SMGSendList = null;
        utilXML = null;
    }
    
    /**
     * 取得返回的XML信息
     * @param head XML数据对象 
     * @param value 0:成功 1:失败
     * @return XML文本信息
     */
    public String getReturnXML(String url, MSGHeadVO head, int value) {
        
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
        strBuf.append("<ReturnInfo> \r\n <MosaicUrl URL=\"" + url	+ "\" /> \r\n</ReturnInfo>\r\n");
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
    
    /*
	 *根据马赛克相关节目信息，更新一对一节目表
	 * 
	 */
	public void updateMosaicChannelMapping(List<SetAutoRecordChannelVO>  voList)
	{
		for(int i =0;i<voList.size();i++)
		{
			@SuppressWarnings("unused")
			SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)voList.get(i);
			try {
				this.delChannelRemappingIndex(vo);
			} catch (DaoException e) {
				e.printStackTrace();
			}
		}
	}
	
    
    public  void delChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// 取得相关节目频点信息
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, RecordType=0,Action=null,");
		strBuff.append(" DownIndex = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where channelindex = " + vo.getIndex());
		strBuff.append(" and devIndex = " + vo.getDevIndex());
		//System.out.println(strBuff.toString());
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
	 * 取得马赛克相关的节目信息
	 * recordType 0：不录像，1:代表故障触发录制   2：24小时录像(默认)	3: 任务录像  4: 马赛克合成轮播
	 * @return
	 */
	public List<SetAutoRecordChannelVO> getProgramInfoByIndex() {

		List<SetAutoRecordChannelVO> voList = new ArrayList<SetAutoRecordChannelVO>();

		Statement statement = null;
		Connection conn = null;

		ResultSet rs = null;
		try {
			conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff = new StringBuffer();
			// 取得相关节目频点信息
			strBuff.append("select *  from channelremapping where delflag =0 and  RecordType = 4 ;");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				while (rs.next()) {
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					//if (freq == 0 || serverID == 0) {
					//	continue;
					//}
					SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
					vo.setServiceID(serverID);
					vo.setFreq(freq);
					vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
					vo.setSymbolRate(Integer.parseInt(rs.getString("symbolrate")));
					vo.setQAM(Integer.parseInt(rs.getString("qam")));
					vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
					vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
					vo.setAction(rs.getString("Action"));
					vo.setRecordType(Integer.parseInt(rs.getString("RecordType")));
					vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
					voList.add(vo);
				}

			} catch (Exception e) {
				log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return voList;
	}

}
