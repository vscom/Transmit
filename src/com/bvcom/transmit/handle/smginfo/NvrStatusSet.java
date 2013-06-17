package com.bvcom.transmit.handle.smginfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.video.ChangeProgramQueryVO;

public class NvrStatusSet {
	
    private static Logger log = Logger.getLogger(NvrStatusSet.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public NvrStatusSet(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. 解析通道设置协议
     * 2. 修改配置文件与数据库
     * @throws DaoException 
     */
    @SuppressWarnings("unchecked")
	public void downXML() throws DaoException {
    	Document document=null;
		try {
			document = utilXML.StringToXML(downString);
		} catch (CommonException e) {
			log.info("字符串转换xml错误："+e.getMessage());
		}
		//解析协议内容并保存
    	List<String> strList=parse(document);
    	for(int i=0;i<strList.size();i++){
    		String[] arrStr=strList.get(i).split(",");
    		int Index=Integer.parseInt(arrStr[0].trim());
    		int IndexType=Integer.parseInt(arrStr[1].trim());
    		//0 代表 停用，1 代表实时视频，2 轮播辅助，3 轮循测量，4 录像，5空闲
    		//1：初始的通道状态为配置文件中的状态信息
    		//2:通过平台修改此配置
    		//3:平台设置通道状态
    		//4:读取状态信息
    		
    		//更新数据库业务类型
    		updateSmgCardInfo(Index,IndexType);
    	}
    	//上报给平台设置成功信息
    	String returnstr="";
    	//封装MemCoreData对象的内容 保存TransmitConfig.xml
    	//isErr=saveMemCoreDataToTransmitConfig(coreData);
    	
    	returnstr = getReturnXML(this.bsData, 0);
        try {
            utilXML.SendUpXML(returnstr, bsData);
        } catch (CommonException e) {
            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
        }
        
        //更新SMG的配置文件信息
        MemCoreData coreDate = MemCoreData.getInstance();
        List SMGCardList = coreDate.getSMGCardList();
        SMGCardList.clear();
    	List<SMGCardInfoVO> NvrStatusList = new ArrayList();
    	@SuppressWarnings("unused")
		Statement statement = null;
		@SuppressWarnings("unused")
		ResultSet rs = null;
		try
		{
			@SuppressWarnings("unused")
			Connection conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff1 = new StringBuffer();
			strBuff1.append("select * from smg_card_info order by smgIndex");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff1.toString());
				while(rs.next()){
					SMGCardInfoVO smginfo = new SMGCardInfoVO();
					int index = rs.getInt("smgIndex");
					smginfo.setIndex(index);
					String ip = rs.getString("smgIp");
					smginfo.setIP(ip);
					int inputtype = rs.getInt("smgInputtype");
					smginfo.setIndexType(String.valueOf(inputtype));
					String url = rs.getString("smgURL");
					smginfo.setURL(url);
					int status = rs.getInt("smgStatus");
					smginfo.setStatus(status);
					NvrStatusList.add(smginfo);
				}
			} catch (Exception e) {
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		
        for(int i =0;i <NvrStatusList.size();i++){
        	  SMGCardInfoVO smgCardInfo = new SMGCardInfoVO();
        	  SMGCardInfoVO smginfo = (SMGCardInfoVO)NvrStatusList.get(i);
        	  @SuppressWarnings("unused")
			  SMGCardInfoVO smginfo_scan = (SMGCardInfoVO)NvrStatusList.get(i);
              // 取得通道信息
              smgCardInfo.setIndex(smginfo.getIndex());
              smgCardInfo.setURL(smginfo.getURL());
              smgCardInfo.setIP(smginfo.getIP());
              smgCardInfo.setHDFlag(0);
              smgCardInfo.setHDURL("http://192.168.100.101:8080/Setup/");
              //通道查询的业务类型：IndexType（0 代表 停用，1 代表实时视频，2 轮播辅助，3 轮循测量，4 自动录像 ，5任务录像 ，6数据采集，7空闲）
              //通道设置业务类型：0 代表 停用，1 代表实时视频，2 轮播辅助，3 轮循测量，4录像 、5空闲
              // 通道类型(1:ChangeProgramQuery(手动选台, 频道扫描 和指标) 2:GetIndexSet(指标查询)  AutoRecord
              int inputtype = Integer.valueOf(smginfo.getIndexType());
              switch(inputtype)
              {
	              case 0:
	            	  smginfo.setIndexType("Stop");
	            	  break;
	              case 1:
	            	  smginfo.setIndexType("ChangeProgramQuery");
	            	  break;
	              case 2:
	            	  smginfo.setIndexType("StreamRoundInfoQuery");
	            	  break;
	              case 3:
	            	  smginfo.setIndexType("AutoAnalysisTimeQuery");
	            	  break;
	              case 4:
	            	  smginfo.setIndexType("AutoRecord");
	            	  break;
	              case 5:
	            	  smginfo.setIndexType("Free");
	            	  break;
	              case 6:
	            	  smginfo.setIndexType("ChannelScanQuery");//GetIndexSet
	            	  break;
	              case 7:
	            	  smginfo.setIndexType("Free");
	            	  break;
              }
              smgCardInfo.setIndexType(smginfo.getIndexType());
              SMGCardList.add(smgCardInfo);
              
              if(inputtype==6){
            	  smgCardInfo = new SMGCardInfoVO();
            	  smginfo = (SMGCardInfoVO)NvrStatusList.get(i);
                  // 取得通道信息
                  smgCardInfo.setIndex(smginfo.getIndex());
                  smgCardInfo.setURL(smginfo.getURL());
                  smgCardInfo.setIP(smginfo.getIP());
                  smgCardInfo.setIndexType("GetIndexSet");
                  SMGCardList.add(smgCardInfo);
              }
              //手动选台：实时视频
              if(inputtype==1){
            	  //更改一对一实时视频表：StatusFlag=3的SMGURL
            	  try {
					upChangeProgramTable(smgCardInfo);
				} catch (DaoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
              }
              //停用一对一表中的通道
              if(inputtype==0){
            	  delChannelFromChannelMapping(smginfo.getIndex());
              }
              else{
            	  recoverChannelFromChannelMapping(smginfo.getIndex());
              }
              // 高清转码标记
              //smgCardInfo.setHDFlag(Integer.valueOf(SMGCardInfo.attribute("HDFlag").getValue()));
              // 高清转码URL
              //smgCardInfo.setHDURL(SMGCardInfo.attribute("HDURL").getValue());
        }
        coreDate.setSMGCardList(SMGCardList);
  }
    
    
    @SuppressWarnings("unused")
	private static void recoverChannelFromChannelMapping(int index) throws DaoException{
    	 StringBuffer strBuff = new StringBuffer();
         Statement statement = null;
         ResultSet rs = null;
         Connection conn = DaoSupport.getJDBCConnection();
         
 		strBuff.append("update channelremapping c set ");
 		strBuff.append("DelFlag = 0");
 		strBuff.append(" where DevIndex = "+index);
         
        try {
             statement = conn.createStatement();
             
             statement.executeUpdate(strBuff.toString());
             
         } catch (Exception e) {
             log.error("一对一节目表更新数据库错误: " + e.getMessage());
         } finally {
             DaoSupport.close(rs);
             DaoSupport.close(statement);
             DaoSupport.close(conn);
         }
    }
  
    @SuppressWarnings("unused")
	private static void delChannelFromChannelMapping(int index) throws DaoException{
    	 StringBuffer strBuff = new StringBuffer();
         Statement statement = null;
         ResultSet rs = null;
         Connection conn = DaoSupport.getJDBCConnection();
         
 		strBuff.append("update channelremapping c set ");
 		strBuff.append("DelFlag = 1");
 		strBuff.append(" where DevIndex = "+index);
         
        try {
             statement = conn.createStatement();
             
             statement.executeUpdate(strBuff.toString());
             
         } catch (Exception e) {
             log.error("一对一节目表更新数据库错误: " + e.getMessage());
         } finally {
             DaoSupport.close(rs);
             DaoSupport.close(statement);
             DaoSupport.close(conn);
         }
    }
    /**
     * 更新入库一对一表
     * @throws DaoException 
     */
    private static void upChangeProgramTable(SMGCardInfoVO vo) throws DaoException {
        StringBuffer strBuff = new StringBuffer();
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = DaoSupport.getJDBCConnection();
        
		strBuff.append("update monitorprogramquery c set ");
		// statusFlag: 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播
		strBuff.append("statusFlag = 3, ");
		// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
		strBuff.append(" RunType = 1, ");
		strBuff.append(" smgURL = '"+vo.getURL()+"'");
		strBuff.append(" ,lastDatatime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where statusFlag = 3 ");
        
        try {
            statement = conn.createStatement();
            
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("手动选台更新数据库错误: " + e.getMessage());
        } finally {
            DaoSupport.close(rs);
            DaoSupport.close(statement);
            DaoSupport.close(conn);
        }
        //log.info("手动选台更新数据库成功!");
    }
    
  //根据通道号更新业务类型信息，增加判断机制，如果有多个实时视频，报错，有多个轮询测量，报错？？--平台应该做限制，前端只负责处理状态更新
  private boolean updateSmgCardInfo(int index,int inputtype)
  {
	  	boolean ret = false;
		Statement statement = null;
		ResultSet rs = null;
		StringBuffer strBuff2 = new StringBuffer();
		
		try {
			Connection conn = DaoSupport.getJDBCConnection();
			strBuff2.append("update smg_card_info set smgInputtype=");
			strBuff2.append(inputtype+",updateTime='");
			strBuff2.append(CommonUtility.getDateTime() + "',smgRemark='");
			strBuff2.append("业务类型变更' where smgIndex=");
			strBuff2.append(index);
			System.out.println(strBuff2.toString());
			conn.setAutoCommit(false);
			try {
				statement=conn.createStatement();
				statement.executeUpdate(strBuff2.toString());
			} catch (Exception e) {
				//log.info("更新通道状态表失败："+e.getMessage());
				ret = false;
			}finally{
				DaoSupport.close(statement);
			}
			ret = true;
			conn.commit();
			DaoSupport.close(conn);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
			ret = false;
		}
	   return ret;
  }
  private String getReturnXML(MSGHeadVO head, int value) {
        
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
            strBuf.append("<ErrReport>\r\n");
            //<NvrStatusSetRecord  Index="0"  IndexType = “0” Comment="内部错误"/>
            strBuf.append("<RebootSetRecord Comment=\"内部错误\"/>\r\n");
            strBuf.append("</ErrReport>\r\n");
        }
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
  
    private List<String> parse(Document document){
    	List<String> list=new ArrayList<String>();
    	Element root=document.getRootElement();
    	for (Iterator<Element> iter=root.elementIterator(); iter.hasNext(); ) {
			Element NvrStatusSet =iter.next();
			for(Iterator<Element> ite=NvrStatusSet.elementIterator();ite.hasNext();){
				Element NvrStatusSetRecord=ite.next();
				String Index=NvrStatusSetRecord.attributeValue("Index");
				String IndexType=NvrStatusSetRecord.attributeValue("IndexType");
				list.add(Index+","+IndexType);
			}
		}
    	return list;
    }
}
