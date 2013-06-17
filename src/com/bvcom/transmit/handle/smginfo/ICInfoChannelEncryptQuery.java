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

import com.bvcom.transmit.TransmitThread;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;

public class ICInfoChannelEncryptQuery {

	 private static Logger log = Logger.getLogger(ICInfoChannelEncryptQuery.class.getSimpleName());
	    
	    private MSGHeadVO bsData = new MSGHeadVO();
	    
	    private String downString = new String();
	    
	    private UtilXML utilXML = new UtilXML();
	    
	    MemCoreData coreData = MemCoreData.getInstance();
	    
	  public ICInfoChannelEncryptQuery(String centerDownStr, MSGHeadVO bsData) {
	        this.downString = centerDownStr;
	        this.bsData = bsData;
	    }
	    /**
	     * 1. 根据平台下发的小卡卡号，下发给板卡（从小卡对应关系中找到板卡URL），等待板卡返回授权信息，然后合并信息并上报给中心，上报的地址为马赛克轮播URL
	     */
	    public void downXML() {
	    	//String CenterURL=coreData.getSysVO().getCenterRoundChannelURL();
	    	//log.error("上发 "+ bsData.getStatusQueryType()+"轮播上报URL:"+CenterURL);
	    	
	    	//根据小卡卡号查找板卡URL
	    	List<String> cardlist=new ArrayList<String>();
	    	Document document;
	  		try {
	  			document = utilXML.StringToXML(downString);
	  			cardlist= parseCardNo(document);
	  		} catch (CommonException e1) {
	  			e1.printStackTrace();
	  		}
	  		
	    	//下发协议给所有的板卡
	  		List<String> smgUrlList_Setup1=new ArrayList<String>();
	  		List<String> smgUrlList_tmp=new ArrayList<String>();
	  		for(int i=0;i<cardlist.size();i++){
	  			String cardno= cardlist.get(i);
	  			String url =getSmgUrlByCardNo(cardno);
	  			if(url.equals("")) continue;
	  			String sendXml = MakeSendXml(this.bsData,cardno);
	  			//比对将相同板子信息放到
	  			if(url.contains("Setup1")){
	  				smgUrlList_Setup1.add(url+","+sendXml);	
	  			}
	  			else {
	  				//临时存放作为比对用
	  				smgUrlList_tmp.add(url+","+sendXml);
	  			}
	  		}
	  		
	  		//比对
	  		List<String> smgUrlList_Setup2=new ArrayList<String>();
	  		for(int i=0;i<smgUrlList_Setup1.size();i++){
	  			String setupStr1 = smgUrlList_Setup1.get(i);
	  			String[] tmp = setupStr1.split(",");
	  			String setupIp1="";
	  			if(tmp.length>1){
	  				setupIp1=tmp[1];
	  			}
	  			for(int j=0;j<smgUrlList_tmp.size();j++){
	  				String setupStr2=smgUrlList_tmp.get(j);
	  				String[] temp=setupStr2.split(",");
	  				String setupIp2="";
	  				if(temp.length>1){
	  					setupIp2=temp[1];
	  				}
	  				if(setupIp1.equals(setupIp2)){
	  					smgUrlList_Setup2.add(setupStr2);
	  				}
	  			}
	  		}
	  		
	  		//====是否要删除节目授权表中的所有节目信息===
	  		delete_allchannelencrypt();
	  		
	  		//通道1的小卡授权信息
	  		String url = "";
	        for (int i=0; i< smgUrlList_Setup1.size(); i++) {
	            String smgStr = (String) smgUrlList_Setup1.get(i);
	            try {
	                if(!url.equals(smgStr)) {
	                    ICInfoChannelEncryptQueryThread ICInfoThread = new ICInfoChannelEncryptQueryThread(smgStr, bsData,null);
	        	        ICInfoThread.start();
	                    url = smgStr;
	                }
	            } catch (Exception e) {
	                log.error("小卡卡号发送失败：" + smgStr);
	            }
	        } 
	        //通道2的小卡授权信息
	        
	        url = "";
	        for (int i=0; i< smgUrlList_Setup2.size(); i++) {
	            String smgStr = (String) smgUrlList_Setup2.get(i);
	            try {
	                if(!url.equals(smgStr)) {
	                    ICInfoChannelEncryptQueryThread ICInfoThread = new ICInfoChannelEncryptQueryThread(smgStr, bsData,smgUrlList_Setup2);
	        	        ICInfoThread.start();
	                    url = smgStr;
	                }
	            } catch (Exception e) {
	                log.error("小卡卡号发送失败：" + smgStr);
	            }
	        } 
	    	//上报中心平台返回信息
	    	String returnstr="";
	        try {
	        	returnstr = getReturnXML(this.bsData, 0);
	            utilXML.SendUpXML(returnstr, bsData);
	        } catch (CommonException e) {
	            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
	        }
	        //开启打包上报线程：参数 通道2的列表信息
	        ICInfoUpCenterThread ICInfoThread = new ICInfoUpCenterThread("", bsData,smgUrlList_Setup2);
	        ICInfoThread.start();
	    }
	    
	    //删除小卡卡号信息
 	    private boolean delete_allchannelencrypt(){
 	    	boolean ret =false;
 	    	try
 			{
 				Statement statement = null;
 				Connection conn = DaoSupport.getJDBCConnection();
 				StringBuffer strBuff = new StringBuffer();
 				strBuff.append("delete from icinfochannelencrypt");
 				try {
 					statement = conn.createStatement();
 					statement.executeUpdate(strBuff.toString());
 				}
 				catch (Exception e)
 				{
 					log.info("删除小卡卡号信息："+e.getMessage());
 				}
 				finally {
 					DaoSupport.close(statement);
 				}
 				strBuff = null;
 				DaoSupport.close(conn);
 			}
 			catch(Exception ex)
 			{
 				ret=false;
 			}
 	    	return ret;
 	   }
 	    
	    //根据卡号下发指定的协议
	    @SuppressWarnings("unused")
		private String MakeSendXml(MSGHeadVO head,String cardno){
	    	 StringBuffer strBuf = new StringBuffer();
	    	 strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
		     strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
		     strBuf.append(head.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\"");
		     strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
		     strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" SrcURL=\""+head.getSrcURL()+"\" Priority=\"1\"> \r\n");
		     strBuf.append("<ICInfoChannelEncryptQuery>");
		     strBuf.append("<ICInfo  CardNO=\""+cardno+"\"/>");
		     //</ICInfoChannelEncryptQuery>
		     strBuf.append("</ICInfoChannelEncryptQuery>");
		     strBuf.append("</Msg>");
	    	 return strBuf.toString();
	    }
	    
//	    <ICInfoChannelEncryptQuery >
//	    <ICInfo  CardNO= “123456” />
//	    <ICInfo  CardNO= “234567” />
//	    </ICInfoChannelEncryptQuery >

	    //解析小卡卡号
	    @SuppressWarnings("unchecked")
		private List<String> parseCardNo(Document document){
			List<String> list=new ArrayList<String>();
			Element root = document.getRootElement();
			Element iCInfoChannelEncryptQuery=root.element("ICInfoChannelEncryptQuery");
			
			for(Iterator<Element> iter=iCInfoChannelEncryptQuery.elementIterator();iter.hasNext();){
				Element ICInfo = iter.next();
				String cardno="";
				try {
					cardno=ICInfo.attribute("CardNO").getValue();
				} catch (Exception ex) {
					log.error("小卡授权信息出错"+ex);
				}
				list.add(cardno);
			}
			return list;
		}
	    //根据小卡卡号获取板卡的URL
	    
	    private String getSmgUrlByCardNo(String cardno){
	    	String smgUrl="";
	    	String smgIp="";
	    	String returnStr="";
	    	if(cardno.equals("")) return returnStr;
	    	try
			{
				Statement statement = null;
				Connection conn = DaoSupport.getJDBCConnection();
				ResultSet rs = null;
				StringBuffer strBuff = new StringBuffer();
				strBuff.append("select *  from smg_card_info s where smgCamCard="+cardno);
				try {
					statement = conn.createStatement();
					rs = statement.executeQuery(strBuff.toString());
					while(rs.next()){
						smgUrl =rs.getString("smgUrl").toString();
						smgIp  =rs.getString("smgIp").toString();
						returnStr = smgUrl +","+smgIp;
						break;
					}
				}
				catch (Exception e)
				{
					log.info("获取小卡卡号信息："+e.getMessage());
				}
				finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
				}
				strBuff = null;
				DaoSupport.close(conn);
			}
			catch(Exception ex)
			{
			}
	    	return returnStr;
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
	        }
	        strBuf.append("</Msg>");
	        return strBuf.toString();
	    }
	    
	    //中心上报接口线程
	    public class ICInfoUpCenterThread extends Thread{
	    	 private List<String> checkListSetup2;
	    	 private MSGHeadVO bsData = new MSGHeadVO();
	    	 public ICInfoUpCenterThread(String centerDownStr, MSGHeadVO bsData,List<String> checkList) {
		    		this.bsData= bsData;
		    		this.checkListSetup2 = checkList;
		    	 }
	    	 public void run(){
	    		//延时等待50分钟
				try {
				   if(this.checkListSetup2.size()>0){
	    		    	try {
							Thread.sleep(CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    		    }
					Thread.sleep(CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//将板卡返回的小卡节目授权信息，从数据库中读取出来并发送给中心平台
				//打包协议上报给中心
    		  	String strICInfoChannelEncrypt=MakeUptoCenter(bsData,0);
    			String CenterURL=coreData.getSysVO().getCenterRoundChannelURL();
    	    	log.info("上发 "+ bsData.getStatusQueryType()+"小卡节目授权上报中心平台URL:"+CenterURL);
    	    	utilXML.SendUpXML(strICInfoChannelEncrypt, CenterURL);
	    	}
	    	
	    	@SuppressWarnings("unchecked")
			private String MakeUptoCenter(MSGHeadVO head,int value){
	    		 
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
	 	        strBuf.append("\r\n<ReturnInfo>");
	 	        strBuf.append("\r\n<ICInfoChannelEncryptQuery>");
	 	        
	 	        
	// 	        <ICInfo  CardNO="XXXX">
	// 	        <ChannelEncrypt  ChannelName= ””  Freq =”8000”  ServiceID=’’  ProgramID=”” Encrypt= ”1”/>
	// 	        <ChannelEncrypt  ChannelName= ””  Freq =”8000”  ServiceID=’’  ProgramID=”” Encrypt= ”0”/>
	// 	        </ICInfo >
 	        
	 	        List cardnolist = getCardNoFromDB();
	 	        for(int i=0;i<cardnolist.size();i++){
	 	        	String cardno =(String)cardnolist.get(i);
	 	        	strBuf.append("\r\n<ICInfo  CardNO="+cardno+">");
	 	        	List programlist = getICChannelInfo(cardno);
	 	        	for(int j=0;j<programlist.size();j++){
	 	        		ICInfoChannelEncryptObject obj = new ICInfoChannelEncryptObject();
	 	        		obj=(ICInfoChannelEncryptObject)programlist.get(j);
	 	        		
	 	        		strBuf.append("\r\n<ChannelEncrypt  ChannelName=\""+obj.getChannelname()+"\"");
	 	        		strBuf.append(" Freq=\""+obj.getFreq()+"\"");
	 	        		strBuf.append(" ServiceID=\""+obj.getServiceid()+"\"");
	 	        		strBuf.append(" ProgramID=\""+obj.getProgramID()+"\"");
	 	        		strBuf.append(" Encrypt=\""+obj.getEncrypt()+"\"");
	 	        		strBuf.append("/>");
	 	        	}
	 	        	strBuf.append("\r\n</ICInfo>");
	 	        }
	 	        strBuf.append("\r\n</ICInfoChannelEncryptQuery>");
	 	        strBuf.append("\r\n</ReturnInfo>\r\n");
	 	        strBuf.append("</Msg>\r\n");
	 	        System.out.println(strBuf.toString());
	 	        return strBuf.toString();	
	    	 }
	   	 	
	   	 	@SuppressWarnings({ "unused", "unchecked" })
			private List getCardNoFromDB(){
	 	    	List<String> cardnoList = new ArrayList<String>();
	 	    	try
	 			{
	 				Statement statement = null;
	 				Connection conn = DaoSupport.getJDBCConnection();
	 				ResultSet rs = null;
	 				StringBuffer strBuff = new StringBuffer();
	 				strBuff.append("select cardno from icinfochannelencrypt group by cardno");
	 				
	 				try {
	 					statement = conn.createStatement();
	 					rs = statement.executeQuery(strBuff.toString());
	 					while(rs.next()){
	 						String cardno =rs.getString("cardno").toString();
	 						cardnoList.add(cardno);
	 					}
	 				}
	 				catch (Exception e)
	 				{
	 					log.info("分组获取小卡卡号信息："+e.getMessage());
	 				}
	 				finally {
	 					DaoSupport.close(rs);
	 					DaoSupport.close(statement);
	 				}
	 				strBuff = null;
	 				DaoSupport.close(conn);
	 			}
	 			catch(Exception ex)
	 			{
	 			}
	 	    	return cardnoList;
	 	    }
	   	 	
	   	 		//根据小卡卡号获取节目的授权信息
		       @SuppressWarnings({ "unchecked", "unused" })
		       private List getICChannelInfo(String cardno){
		    	   
		    	   List<ICInfoChannelEncryptObject>  channelinfoList = new ArrayList<ICInfoChannelEncryptObject>();
		    	   try
		 			{
		 				Statement statement = null;
		 				Connection conn = DaoSupport.getJDBCConnection();
		 				ResultSet rs = null;
		 				StringBuffer strBuff = new StringBuffer();
		 				strBuff.append("select *  from icinfochannelencrypt s where cardno="+cardno);
		 				
		 				try {
		 					statement = conn.createStatement();
		 					rs = statement.executeQuery(strBuff.toString());
		 					while(rs.next()){
		 						ICInfoChannelEncryptObject  obj = new ICInfoChannelEncryptObject();
		 						
		 						obj.setCardno(rs.getString("cardno").toString());
		 						obj.setChannelname(rs.getString("channelname").toString());
		 						obj.setFreq(rs.getString("freq").toString());
		 						obj.setServiceid(rs.getString("serviceid").toString());
		 						obj.setProgramID(rs.getString("programid").toString());
		 						obj.setEncrypt(rs.getString("encryptstatus").toString());
		 						channelinfoList.add(obj);
		 					}
		 				}
		 				catch (Exception e)
		 				{
		 					log.info("获取小卡卡号授权信息："+e.getMessage());
		 				}
		 				finally {
		 					DaoSupport.close(rs);
		 					DaoSupport.close(statement);
		 				}
		 				strBuff = null;
		 				DaoSupport.close(conn);
		 			}
		 			catch(Exception ex)
		 			{
		 			}
		    	   
		    	   return channelinfoList;
		       }
	    }
	    
	    //小卡指令发送线程
	    public  class ICInfoChannelEncryptQueryThread extends Thread {
	    	
	    	 private String smgUrl;
	    	 private String downStr;
	    	 private MSGHeadVO bsData = new MSGHeadVO();
	    	 private List<String> checkListSetup2;
	    	 public ICInfoChannelEncryptQueryThread(String centerDownStr, MSGHeadVO bsData,List<String> checkList) {
	    		//给板卡发送不同的卡号命令,板卡返回的时候要带上此小卡卡号
             	String[] tmp=centerDownStr.split(",");
             	if(tmp.length>1){
             	 smgUrl=tmp[0];
             	 downStr=tmp[2];
             	}
	    		this.bsData= bsData;
	    		this.checkListSetup2= checkList;
	    	  }
	    	  public void run() {
	    		    
		    		if(this.checkListSetup2==null){
		    			  
		    		}
		    		else
		    		{
		    			//如果通道1和通道2同时查询小卡授权信息，那么需要等待通道1处理完
		    		    if(this.checkListSetup2.size()>0){
		    		    	try {
								Thread.sleep(CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		    		    }
		    		}
					// 小卡卡号授权信息下发 timeout 1000*30 三十秒
	    			Document document = null;
	    		  	try {
	    		  		String returnStr = utilXML.SendDownXML(downStr, smgUrl,CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
	    		  		log.info("小卡卡号节目授权信息查询指令下发完成:"+smgUrl);
	    		  		document = utilXML.StringToXML(returnStr);
			            document.setXMLEncoding("UTF-8");
					} catch (CommonException e) {
						System.out.println(e.getMessage());
					}
				
					//等待获取数据库节目授权信息    
			        try {
			            ICInfoChannelEncryptParase(document);
			        } catch (Exception e) {
			        	System.out.println(e.getMessage());
			        }
	    	  }	    
	    	  
	    
	    	 
	 	    /*
	 	     * Author:tqy
	 	     * Date :2012-10-18
	 	     * 小卡卡号授权信息相关接口
	 	     */
	 	    @SuppressWarnings("unchecked")
	 		private void ICInfoChannelEncryptParase(Document document)
	 	    {
	 	    	List<String> cardnoList = new ArrayList<String>();
	 	    	List<ICInfoChannelEncryptObject> channelencryptList = new ArrayList<ICInfoChannelEncryptObject>();
	 	    	//解析、	
	 	    	Element root = document.getRootElement();
	 	    	Element ele = null;
	 	          
	 	        for (Iterator iter = root.elementIterator(); iter.hasNext();) {
	 	              ele = (Element) iter.next();
	 	              //<Return Type="ICInfoChannelEncryptQuery" Value="0" Desc="成功"/>
	 	              for(Iterator<Element> ite=ele.elementIterator();ite.hasNext();){
	 	            	//<ICInfoChannelEncryptQuery >
	 	  				 Element ICInfoEncryptQuery = ite.next();
	 	  				 // <ICInfo  CardNO="20000001">
	 	  				 for(Iterator<Element> icinfo=ICInfoEncryptQuery.elementIterator();icinfo.hasNext();){
	 	  					 Element ICInfo = icinfo.next();
	 	  					 String cardno = ICInfo.attributeValue("CardNO");
	 	  					 cardnoList.add(cardno);
	 	  					 // <ChannelEncrypt  ChannelName="BTV-1"  Freq ="8000"  ServiceID="201"  ProgramID="101" Encrypt= "1"/>
	 	  					 for(Iterator<Element> channel=ICInfo.elementIterator();channel.hasNext();){
	 	  	  	  				Element ChannelEncrypt = channel.next();
	 	  	  	  				String channelname = ChannelEncrypt.attributeValue("ChannelName");
	 	  	  	  				String freq =ChannelEncrypt.attributeValue("Freq");
	 	  	  	  				String serviceid=ChannelEncrypt.attributeValue("ServiceID");
	 	  	  	  				String programID=ChannelEncrypt.attributeValue("ProgramID");
	 	  	  	  				String encrypt=ChannelEncrypt.attributeValue("Encrypt");
	 	  						
	 	  	  	  				//将小卡授权信息添加到队列中
	 	  	  	  				ICInfoChannelEncryptObject channelObj = new ICInfoChannelEncryptObject();
	 	  	  	  				
	 	  	  	  				channelObj.setCardno(cardno);
	 	  	  	  				channelObj.setChannelname(channelname);
	 	  	  	  				channelObj.setFreq(freq);
	 	  	  	  				channelObj.setServiceid(serviceid);
	 	  	  	  				channelObj.setProgramID(programID);
	 	  	  	  				channelObj.setEncrypt(encrypt);
	 	  	  	  				channelencryptList.add(channelObj);
	 	  					 }
	 	  				 }
	 	              }
	 	         }
	 	    	//入库
	 	    	//根据卡号查询，若有则删除记录，重新插入记录
	 	        for(int i=0;i<cardnoList.size();i++){
	 	        	String cardno = cardnoList.get(i);
	 	        	if(query_channelencrypt_cardno(cardno)){
	 	        		delete_channelencrypt_cardno(cardno);
	 	        	}
	 	        }
	 	        for(int k =0;k<channelencryptList.size();k++){
	 	        	ICInfoChannelEncryptObject encryptObj = new ICInfoChannelEncryptObject();
	 	        	encryptObj = (ICInfoChannelEncryptObject) channelencryptList.get(k);
	 	        	insert_channelencrypt(encryptObj);
	 	        }
	 	    	
	 	    }
	 	    
	 	    private void insert_channelencrypt(ICInfoChannelEncryptObject obj){
	 	    	try
	 			{
	 				Statement statement = null;
	 				Connection conn = DaoSupport.getJDBCConnection();
	 				ResultSet rs = null;
	 				StringBuffer strBuff = new StringBuffer();
	 				strBuff.append("insert into icinfochannelencrypt(cardno, channelname, freq,serviceid, programid,encryptstatus)");
	 				strBuff.append(" values(");
	 				strBuff.append(obj.getCardno() + ", '");
	 				strBuff.append(obj.getChannelname() + "', '");
	 				strBuff.append(obj.getFreq() + "', '");
	 				strBuff.append(obj.getServiceid() + "', '");
	 				strBuff.append(obj.getProgramID() + "', ");
	 				strBuff.append("'" + obj.getEncrypt() + "')");
	 				//System.out.println(strBuff.toString());
	 				try {
	 					statement = conn.createStatement();
	 					statement.executeUpdate(strBuff.toString());
	 				}
	 				catch (Exception e)
	 				{
	 					log.info("插入小卡授权信息："+e.getMessage());
	 				}
	 				finally {
	 					DaoSupport.close(statement);
	 				}
	 				strBuff = null;
	 				DaoSupport.close(conn);
	 			}
	 			catch(Exception ex)
	 			{
	 			}
	 	    	
	 	    }
	 	    //查询小卡信息
	 	    private boolean query_channelencrypt_cardno(String cardno){
	 	    	boolean ret =false;
	 	    	try
	 			{
	 				Statement statement = null;
	 				Connection conn = DaoSupport.getJDBCConnection();
	 				ResultSet rs = null;
	 				StringBuffer strBuff = new StringBuffer();
	 				strBuff.append("select *  from icinfochannelencrypt s where cardno="+cardno);
	 				try {
	 					statement = conn.createStatement();
	 					rs = statement.executeQuery(strBuff.toString());
	 					while(rs.next()){
	 						ret= true;
	 						break;
	 					}
	 				}
	 				catch (Exception e)
	 				{
	 					log.info("获取小卡卡号信息："+e.getMessage());
	 				}
	 				finally {
	 					DaoSupport.close(rs);
	 					DaoSupport.close(statement);
	 				}
	 				strBuff = null;
	 				DaoSupport.close(conn);
	 			}
	 			catch(Exception ex)
	 			{
	 				ret=false;
	 			}
	 	    	return ret;
	 	    }
	 	    //删除小卡卡号信息
	 	    private boolean delete_channelencrypt_cardno(String cardno){
	 	    	boolean ret =false;
	 	    	try
	 			{
	 				Statement statement = null;
	 				Connection conn = DaoSupport.getJDBCConnection();
	 				StringBuffer strBuff = new StringBuffer();
	 				strBuff.append("delete from icinfochannelencrypt  where cardno="+cardno);
	 				try {
	 					statement = conn.createStatement();
	 					statement.executeUpdate(strBuff.toString());
	 				}
	 				catch (Exception e)
	 				{
	 					log.info("删除小卡卡号信息："+e.getMessage());
	 				}
	 				finally {
	 					DaoSupport.close(statement);
	 				}
	 				strBuff = null;
	 				DaoSupport.close(conn);
	 			}
	 			catch(Exception ex)
	 			{
	 				ret=false;
	 			}
	 	    	return ret;
	 	   }
	   }	    
}
