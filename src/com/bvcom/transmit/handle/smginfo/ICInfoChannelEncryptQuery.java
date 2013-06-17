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
	     * 1. ����ƽ̨�·���С�����ţ��·����忨����С����Ӧ��ϵ���ҵ��忨URL�����ȴ��忨������Ȩ��Ϣ��Ȼ��ϲ���Ϣ���ϱ������ģ��ϱ��ĵ�ַΪ�������ֲ�URL
	     */
	    public void downXML() {
	    	//String CenterURL=coreData.getSysVO().getCenterRoundChannelURL();
	    	//log.error("�Ϸ� "+ bsData.getStatusQueryType()+"�ֲ��ϱ�URL:"+CenterURL);
	    	
	    	//����С�����Ų��Ұ忨URL
	    	List<String> cardlist=new ArrayList<String>();
	    	Document document;
	  		try {
	  			document = utilXML.StringToXML(downString);
	  			cardlist= parseCardNo(document);
	  		} catch (CommonException e1) {
	  			e1.printStackTrace();
	  		}
	  		
	    	//�·�Э������еİ忨
	  		List<String> smgUrlList_Setup1=new ArrayList<String>();
	  		List<String> smgUrlList_tmp=new ArrayList<String>();
	  		for(int i=0;i<cardlist.size();i++){
	  			String cardno= cardlist.get(i);
	  			String url =getSmgUrlByCardNo(cardno);
	  			if(url.equals("")) continue;
	  			String sendXml = MakeSendXml(this.bsData,cardno);
	  			//�ȶԽ���ͬ������Ϣ�ŵ�
	  			if(url.contains("Setup1")){
	  				smgUrlList_Setup1.add(url+","+sendXml);	
	  			}
	  			else {
	  				//��ʱ�����Ϊ�ȶ���
	  				smgUrlList_tmp.add(url+","+sendXml);
	  			}
	  		}
	  		
	  		//�ȶ�
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
	  		
	  		//====�Ƿ�Ҫɾ����Ŀ��Ȩ���е����н�Ŀ��Ϣ===
	  		delete_allchannelencrypt();
	  		
	  		//ͨ��1��С����Ȩ��Ϣ
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
	                log.error("С�����ŷ���ʧ�ܣ�" + smgStr);
	            }
	        } 
	        //ͨ��2��С����Ȩ��Ϣ
	        
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
	                log.error("С�����ŷ���ʧ�ܣ�" + smgStr);
	            }
	        } 
	    	//�ϱ�����ƽ̨������Ϣ
	    	String returnstr="";
	        try {
	        	returnstr = getReturnXML(this.bsData, 0);
	            utilXML.SendUpXML(returnstr, bsData);
	        } catch (CommonException e) {
	            log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
	        }
	        //��������ϱ��̣߳����� ͨ��2���б���Ϣ
	        ICInfoUpCenterThread ICInfoThread = new ICInfoUpCenterThread("", bsData,smgUrlList_Setup2);
	        ICInfoThread.start();
	    }
	    
	    //ɾ��С��������Ϣ
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
 					log.info("ɾ��С��������Ϣ��"+e.getMessage());
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
 	    
	    //���ݿ����·�ָ����Э��
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
//	    <ICInfo  CardNO= ��123456�� />
//	    <ICInfo  CardNO= ��234567�� />
//	    </ICInfoChannelEncryptQuery >

	    //����С������
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
					log.error("С����Ȩ��Ϣ����"+ex);
				}
				list.add(cardno);
			}
			return list;
		}
	    //����С�����Ż�ȡ�忨��URL
	    
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
					log.info("��ȡС��������Ϣ��"+e.getMessage());
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
	            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
	        }else if(1==value){
	            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
	        }
	        strBuf.append("</Msg>");
	        return strBuf.toString();
	    }
	    
	    //�����ϱ��ӿ��߳�
	    public class ICInfoUpCenterThread extends Thread{
	    	 private List<String> checkListSetup2;
	    	 private MSGHeadVO bsData = new MSGHeadVO();
	    	 public ICInfoUpCenterThread(String centerDownStr, MSGHeadVO bsData,List<String> checkList) {
		    		this.bsData= bsData;
		    		this.checkListSetup2 = checkList;
		    	 }
	    	 public void run(){
	    		//��ʱ�ȴ�50����
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
				//���忨���ص�С����Ŀ��Ȩ��Ϣ�������ݿ��ж�ȡ���������͸�����ƽ̨
				//���Э���ϱ�������
    		  	String strICInfoChannelEncrypt=MakeUptoCenter(bsData,0);
    			String CenterURL=coreData.getSysVO().getCenterRoundChannelURL();
    	    	log.info("�Ϸ� "+ bsData.getStatusQueryType()+"С����Ŀ��Ȩ�ϱ�����ƽ̨URL:"+CenterURL);
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
	 	            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
	 	        }else if(1==value){
	 	            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
	 	        }
	 	        strBuf.append("\r\n<ReturnInfo>");
	 	        strBuf.append("\r\n<ICInfoChannelEncryptQuery>");
	 	        
	 	        
	// 	        <ICInfo  CardNO="XXXX">
	// 	        <ChannelEncrypt  ChannelName= ����  Freq =��8000��  ServiceID=����  ProgramID=���� Encrypt= ��1��/>
	// 	        <ChannelEncrypt  ChannelName= ����  Freq =��8000��  ServiceID=����  ProgramID=���� Encrypt= ��0��/>
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
	 					log.info("�����ȡС��������Ϣ��"+e.getMessage());
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
	   	 	
	   	 		//����С�����Ż�ȡ��Ŀ����Ȩ��Ϣ
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
		 					log.info("��ȡС��������Ȩ��Ϣ��"+e.getMessage());
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
	    
	    //С��ָ����߳�
	    public  class ICInfoChannelEncryptQueryThread extends Thread {
	    	
	    	 private String smgUrl;
	    	 private String downStr;
	    	 private MSGHeadVO bsData = new MSGHeadVO();
	    	 private List<String> checkListSetup2;
	    	 public ICInfoChannelEncryptQueryThread(String centerDownStr, MSGHeadVO bsData,List<String> checkList) {
	    		//���忨���Ͳ�ͬ�Ŀ�������,�忨���ص�ʱ��Ҫ���ϴ�С������
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
		    			//���ͨ��1��ͨ��2ͬʱ��ѯС����Ȩ��Ϣ����ô��Ҫ�ȴ�ͨ��1������
		    		    if(this.checkListSetup2.size()>0){
		    		    	try {
								Thread.sleep(CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
		    		    }
		    		}
					// С��������Ȩ��Ϣ�·� timeout 1000*30 ��ʮ��
	    			Document document = null;
	    		  	try {
	    		  		String returnStr = utilXML.SendDownXML(downStr, smgUrl,CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
	    		  		log.info("С�����Ž�Ŀ��Ȩ��Ϣ��ѯָ���·����:"+smgUrl);
	    		  		document = utilXML.StringToXML(returnStr);
			            document.setXMLEncoding("UTF-8");
					} catch (CommonException e) {
						System.out.println(e.getMessage());
					}
				
					//�ȴ���ȡ���ݿ��Ŀ��Ȩ��Ϣ    
			        try {
			            ICInfoChannelEncryptParase(document);
			        } catch (Exception e) {
			        	System.out.println(e.getMessage());
			        }
	    	  }	    
	    	  
	    
	    	 
	 	    /*
	 	     * Author:tqy
	 	     * Date :2012-10-18
	 	     * С��������Ȩ��Ϣ��ؽӿ�
	 	     */
	 	    @SuppressWarnings("unchecked")
	 		private void ICInfoChannelEncryptParase(Document document)
	 	    {
	 	    	List<String> cardnoList = new ArrayList<String>();
	 	    	List<ICInfoChannelEncryptObject> channelencryptList = new ArrayList<ICInfoChannelEncryptObject>();
	 	    	//������	
	 	    	Element root = document.getRootElement();
	 	    	Element ele = null;
	 	          
	 	        for (Iterator iter = root.elementIterator(); iter.hasNext();) {
	 	              ele = (Element) iter.next();
	 	              //<Return Type="ICInfoChannelEncryptQuery" Value="0" Desc="�ɹ�"/>
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
	 	  						
	 	  	  	  				//��С����Ȩ��Ϣ��ӵ�������
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
	 	    	//���
	 	    	//���ݿ��Ų�ѯ��������ɾ����¼�����²����¼
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
	 					log.info("����С����Ȩ��Ϣ��"+e.getMessage());
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
	 	    //��ѯС����Ϣ
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
	 					log.info("��ȡС��������Ϣ��"+e.getMessage());
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
	 	    //ɾ��С��������Ϣ
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
	 					log.info("ɾ��С��������Ϣ��"+e.getMessage());
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
