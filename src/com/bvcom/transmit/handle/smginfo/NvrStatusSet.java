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
     * 1. ����ͨ������Э��
     * 2. �޸������ļ������ݿ�
     * @throws DaoException 
     */
    @SuppressWarnings("unchecked")
	public void downXML() throws DaoException {
    	Document document=null;
		try {
			document = utilXML.StringToXML(downString);
		} catch (CommonException e) {
			log.info("�ַ���ת��xml����"+e.getMessage());
		}
		//����Э�����ݲ�����
    	List<String> strList=parse(document);
    	for(int i=0;i<strList.size();i++){
    		String[] arrStr=strList.get(i).split(",");
    		int Index=Integer.parseInt(arrStr[0].trim());
    		int IndexType=Integer.parseInt(arrStr[1].trim());
    		//0 ���� ͣ�ã�1 ����ʵʱ��Ƶ��2 �ֲ�������3 ��ѭ������4 ¼��5����
    		//1����ʼ��ͨ��״̬Ϊ�����ļ��е�״̬��Ϣ
    		//2:ͨ��ƽ̨�޸Ĵ�����
    		//3:ƽ̨����ͨ��״̬
    		//4:��ȡ״̬��Ϣ
    		
    		//�������ݿ�ҵ������
    		updateSmgCardInfo(Index,IndexType);
    	}
    	//�ϱ���ƽ̨���óɹ���Ϣ
    	String returnstr="";
    	//��װMemCoreData��������� ����TransmitConfig.xml
    	//isErr=saveMemCoreDataToTransmitConfig(coreData);
    	
    	returnstr = getReturnXML(this.bsData, 0);
        try {
            utilXML.SendUpXML(returnstr, bsData);
        } catch (CommonException e) {
            log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
        }
        
        //����SMG�������ļ���Ϣ
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
              // ȡ��ͨ����Ϣ
              smgCardInfo.setIndex(smginfo.getIndex());
              smgCardInfo.setURL(smginfo.getURL());
              smgCardInfo.setIP(smginfo.getIP());
              smgCardInfo.setHDFlag(0);
              smgCardInfo.setHDURL("http://192.168.100.101:8080/Setup/");
              //ͨ����ѯ��ҵ�����ͣ�IndexType��0 ���� ͣ�ã�1 ����ʵʱ��Ƶ��2 �ֲ�������3 ��ѭ������4 �Զ�¼�� ��5����¼�� ��6���ݲɼ���7���У�
              //ͨ������ҵ�����ͣ�0 ���� ͣ�ã�1 ����ʵʱ��Ƶ��2 �ֲ�������3 ��ѭ������4¼�� ��5����
              // ͨ������(1:ChangeProgramQuery(�ֶ�ѡ̨, Ƶ��ɨ�� ��ָ��) 2:GetIndexSet(ָ���ѯ)  AutoRecord
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
                  // ȡ��ͨ����Ϣ
                  smgCardInfo.setIndex(smginfo.getIndex());
                  smgCardInfo.setURL(smginfo.getURL());
                  smgCardInfo.setIP(smginfo.getIP());
                  smgCardInfo.setIndexType("GetIndexSet");
                  SMGCardList.add(smgCardInfo);
              }
              //�ֶ�ѡ̨��ʵʱ��Ƶ
              if(inputtype==1){
            	  //����һ��һʵʱ��Ƶ��StatusFlag=3��SMGURL
            	  try {
					upChangeProgramTable(smgCardInfo);
				} catch (DaoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
              }
              //ͣ��һ��һ���е�ͨ��
              if(inputtype==0){
            	  delChannelFromChannelMapping(smginfo.getIndex());
              }
              else{
            	  recoverChannelFromChannelMapping(smginfo.getIndex());
              }
              // ����ת����
              //smgCardInfo.setHDFlag(Integer.valueOf(SMGCardInfo.attribute("HDFlag").getValue()));
              // ����ת��URL
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
             log.error("һ��һ��Ŀ��������ݿ����: " + e.getMessage());
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
             log.error("һ��һ��Ŀ��������ݿ����: " + e.getMessage());
         } finally {
             DaoSupport.close(rs);
             DaoSupport.close(statement);
             DaoSupport.close(conn);
         }
    }
    /**
     * �������һ��һ��
     * @throws DaoException 
     */
    private static void upChangeProgramTable(SMGCardInfoVO vo) throws DaoException {
        StringBuffer strBuff = new StringBuffer();
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = DaoSupport.getJDBCConnection();
        
		strBuff.append("update monitorprogramquery c set ");
		// statusFlag: 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ�
		strBuff.append("statusFlag = 3, ");
		// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
		strBuff.append(" RunType = 1, ");
		strBuff.append(" smgURL = '"+vo.getURL()+"'");
		strBuff.append(" ,lastDatatime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where statusFlag = 3 ");
        
        try {
            statement = conn.createStatement();
            
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("�ֶ�ѡ̨�������ݿ����: " + e.getMessage());
        } finally {
            DaoSupport.close(rs);
            DaoSupport.close(statement);
            DaoSupport.close(conn);
        }
        //log.info("�ֶ�ѡ̨�������ݿ�ɹ�!");
    }
    
  //����ͨ���Ÿ���ҵ��������Ϣ�������жϻ��ƣ�����ж��ʵʱ��Ƶ�������ж����ѯ������������--ƽ̨Ӧ�������ƣ�ǰ��ֻ������״̬����
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
			strBuff2.append("ҵ�����ͱ��' where smgIndex=");
			strBuff2.append(index);
			System.out.println(strBuff2.toString());
			conn.setAutoCommit(false);
			try {
				statement=conn.createStatement();
				statement.executeUpdate(strBuff2.toString());
			} catch (Exception e) {
				//log.info("����ͨ��״̬��ʧ�ܣ�"+e.getMessage());
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
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
            strBuf.append("<ErrReport>\r\n");
            //<NvrStatusSetRecord  Index="0"  IndexType = ��0�� Comment="�ڲ�����"/>
            strBuf.append("<RebootSetRecord Comment=\"�ڲ�����\"/>\r\n");
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
