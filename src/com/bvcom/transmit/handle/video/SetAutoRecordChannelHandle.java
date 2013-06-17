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
 * �Զ�¼��
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
    
    private static List SMGSendList = new ArrayList();//SMG���б���Ϣ
    private static List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
    private static List IPMSendList = coreData.getIPMList();//IPM���б���Ϣ
    
    public SetAutoRecordChannelHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    public SetAutoRecordChannelHandle() {
    }
    
    /**
     * 1. ����xml�õ��Զ�¼���ȫ����Ϣ
     * 2. ת����TSC��SMG
     * 3. �·��ɹ��󷵻سɹ���
     *
     */
    @SuppressWarnings("unchecked")
	public void downXML(){
    	 // ��������
		@SuppressWarnings("unused")
		String upString = "";
		String smgDownString = "";

        List channelSendList = new ArrayList();//Channel Index List
        
        int channel = 0;
        TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
        IPMSendList = coreData.getIPMList();//IPM���б���Ϣ
        SMGSendList = new ArrayList();
        
        String action = "Del";
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("�Զ�¼��StringToXML Error: " + e.getMessage());
        }
        SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
        List<SetAutoRecordChannelVO> AutoRecordlist = setAutoRecordChannel.getDownXml(document);
        
        // �ж��Ƿ��Զ�¼���Ƿ񳬹������ļ��е����ֵ(MaxAutoRecordNum)
        int count = 0;
        for(int i=0; i< AutoRecordlist.size(); i++)  {//�õ��������xml�Ķ��������е�һ������
        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
        	if ((vo.getRecordType() == 1 || vo.getRecordType() == 2) && vo.getAction().equals("Set")) {
        		try {
					if(isHaveProgramInRemapping(vo) != 1) {
						vo = getHDFlagByProgram(vo);						
						//�ж��Ƿ����  �ǵĻ���5  ���ǵĻ���1
						if(vo.getHDFlag()==0){
							count++;
						}else{
							count+=5;
						}
					}
				} catch (DaoException e) {
					log.error("�ж��Ƿ��и������Դʧ��: " + e.getMessage());
				}
        		
        	}
        }
        
        try {
        	count += getAutoRecordNumbers();
		} catch (DaoException e2) {
		}
        
		//TODO  �������� �Ҹ� �Ҹ� �Ҹĸĸ� 
		
		if (count > sysInfoVO.getMaxAutoRecordNum()) {
			// �Զ�¼�񳬹������ļ��е����ֵ(MaxAutoRecordNum)
	        try {
	        	int temp=sysInfoVO.getMaxAutoRecordNum()-getAutoRecordNumbers();//ʣ����Դ
	        	upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 1,temp);
	        	log.warn("### �Զ�¼��7*24Сʱ¼��-û�и������Դ����,ʣ����ԴΪ��"+temp+" ### ");
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("�Զ�¼��ظ�ʧ��: " + e.getMessage());
	        }
			return;
		}
        
        for(int i=0; i< AutoRecordlist.size(); i++)  {//�õ��������xml�Ķ��������е�һ������

        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
        	
        	int channelIndex = (int)vo.getIndex();
        	
        	try {
        		
        		 // �����ǲ����Ǹ��汾��xmlЭ���Զ�¼����ת���Զ������Ŀ By Bian Jiang 2010.9.6
    			 if(vo.getAction().equals("Set")) {
    				 
    				 // Get Program HDFlag
    				 vo = getHDFlagByProgram(vo);
    				 
    				 if (vo.getDownIndex() != 0) {
    					 // �����·���ͨ���ŵĴ���
    					 GetChannelRemappingbyIndex(vo);
    				 }
    				 /** ����ȥ�������ж�
					 // �����·�û��ͨ���ŵĴ���
    				 if (vo.getHDFlag() == 1) {
    					 getHDFlagProgramIndex(vo);
    				 } else {
    					 GetChannelRemappingbyFreq(vo);
    				 }
    				 */
					 
    				 GetChannelRemappingbyFreq(vo);
    				 
    				 // ȡ��TSCIndexͨ���� By: Bian Jiang 2011.4.8
    				 
    				 //�����ж������¼��֮ǰ�Ѿ����ڲ���״̬Ϊ¼�� �򻹰���ԭ��ͨ���·�
    				 Statement statement = null;
    				 ResultSet rs = null;
    				 Connection conn = null;
    				 try {
    					 conn=DaoSupport.getJDBCConnection();
	    				 String sql="SELECT * FROM channelremapping where Freq = "+vo.getFreq()+" and ServiceId = "+vo.getServiceID()+" and RecordType = 2 and StatusFlag = 1;";
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
	    					vo.setTscIndex(getTSCIndex(vo.getHDFlag(),vo.getFreq()));
	    					
	    					//ias ͨ���� ƽ������  һ���������5������  Ji Long 
	    					vo.setIpmIndex(getIASIndex(vo.getHDFlag(),vo.getFreq()));
	    				 }
	    				 
	    				 channelIndex = vo.getDevIndex();
	    				 //statusflag =1 ������Ч�Ľ�Ŀ��Ϣ
	    				 upChannelRemappingIndex(vo);
	    				 action = vo.getAction();
    				 } catch (Exception e) {
    					 e.printStackTrace();
					 }finally{
						DaoSupport.close(conn);
					 }
    			 } else {
    				 // ɾ��ͨ��ʹ�ñ�� TODO ɾ������ͨ��������
    				 GetIndexByProgram(vo, true);
    				 channelIndex = vo.getDevIndex();
    				 updateDelFlagChannelRemappingByProgram(vo);
    			 }
        			 
        		
    			 //���忨�Ŵ���channelSendList�У��ұ�֤channelSendList���ظ���ͨ����
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

//                  *************�·�smg(��Ҫ����Щ�忨����Ϣ)**************
        		CommonUtility.checkSMGChannelIndex(channelIndex, SMGSendList);
        		
			} catch (DaoException e) {
				log.info("�Զ�¼��������ݿ����:"+e.getMessage());
				//e.printStackTrace();
			}
        }
        
        
        if (channelSendList.size() != 0) {
	        	
	        // ���ݼ��2.0�汾 ͨ��ͨ��ӳ��
        	// �����ǲ����Ǹ��汾��xmlЭ���Զ�¼����ת���Զ������Ŀ By Bian Jiang 2010.9.6
	        //if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_0)) {
	        	try {
	        		List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = GetProgramInfoByIndex(channelSendList, true);
	        		List<SetAutoRecordChannelVO> AutoRecordlistNew = GetProgramInfoByDownIndex(channelSendList, true);	        		
	    	       
	    	        
		        	// ͨ��ͨ����ȡ�����н�Ŀ��Ϣ
		        	if (action.equals("Del")) {
		        		//by tqy 2012-05-23 :ֻ�·�ɾ���Ľ�Ŀ��Ϣ
		        		
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
					log.error("ͨ��ͨ��ȡ�ý�Ŀ��Ϣ����: " + e.getMessage());
				}
	        //}
	        
	        
	        String url = "";	        
	        String retString = "";
	        
	        int isError = 0;
	        //TODO TSC�·�
	        for(int t=0;t<TSCSendList.size();t++){
	            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);
	            try {
	            	if (tsc.getRecordType() != 1 && tsc.getRecordType() != 2) {
	            		// Add By Bian Jiang 2011.1.28
	            		// ��������Զ�¼�����̬¼��ͽ�����һ��
	            		continue;
	            	}
	            	//ԭ��䲻�ܱ�֤һ���忨ֻ��һ�Σ�BY TQY 
	                if (!url.equals(tsc.getURL().trim())) {
	                    // �Զ�¼���·�
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
	                log.error("�·��Զ�¼��TSC����" + tsc.getURL());
	            }
	        }
	        
	        /* IPM ���÷��� */
	        for(int t=0;t<IPMSendList.size();t++){
	        	IPMInfoVO ipm = (IPMInfoVO) IPMSendList.get(t);
	            try {
	            	if(ipm.getRecordType()==2){
	            		if (!url.equals(ipm.getURL().trim())) {
	            			// �Զ�¼���·�
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
	                log.error("�·��Զ�¼��IPM����" + ipm.getURL());
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
	            log.error("�Զ�¼��ظ�ʧ��: " + e.getMessage());
	        }
	       
	        //1��û�и�TSC��IPM���ͳɹ�ʱ���ڸ��忨����Ϣ
	        if(AutoRecordlist.size() > 0 && isError==0) {
	        
		        for(int l=0;l<SMGSendList.size();l++)
		        {
		            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(l);
		            try {
		                if (!url.equals(smg.getURL().trim())) {
		                smgDownString = smgDownString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
		            	smgDownString = smgDownString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
		                // �Զ�¼���·� timeout 1000*30 ��ʮ��
		                utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(smgDownString), smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
		                url = smg.getURL().trim();
		                }
		                // �����������
		                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
		                	// ����ת���·�
		                	utilXML.SendDownNoneReturn(setAutoRecordChannel.replaceString(smgDownString), smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
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
		            	updateSMGURLByDevIndex(smg.getIndex(), smg.getURL());
		            } catch (Exception ex) {
		            	
		            }
		        }
	            
	        } 
	        else	
	        {
	        	//����ʧ�ܺ�������ݿ�delflag=1�����Ѿ�ɾ��
	            for(int i=0; i< AutoRecordlist.size(); i++)  {//�õ��������xml�Ķ��������е�һ������

	            	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
	            	try {
						updateDelFlagChannelRemappingByProgram(vo);
					} catch (DaoException e) {
						
					}
	            }
	        }
		     
	        //�����õ�������Ϣ���лָ� delflag =1  ���ã��Ѿ����Ľ�Ŀ��Ϣ
		    try {
		    	// �������ݿ�
			   delChannelRemappingByProgram();
			} catch (DaoException e1) {
				
			}
        } else {
        	//BY TQY 2012-04-12
        	//ϵͳĬ��Ϊɾ�����н�Ŀ��ϢЭ�鴦��
        	//1:ƽ̨����������ϱ���ƽ̨,������Ҫ�ȴ�ʱ��Ƚϳ�
        	//2:���·������е�SMG�忨����ɾ������
        	//3:ͬ��ҲҪ��TSC�·�ɾ�������Զ�¼����Ϣ��JAVAת��δ����
        	if(action.equals("Del")) {
	    		upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 0,-1);
        	} else {
        		upString = setAutoRecordChannel.ReturnXMLByURL(this.bsData, 1,-1);
        	}
	        try {
	            utilXML.SendUpXML(upString, bsData);
	        } catch (CommonException e) {
	            log.error("�Զ�¼��ظ�ʧ��: " + e.getMessage());
	        }
        	
        }
        
        if(AutoRecordlist.size() == 0) {
        	// �Զ���Э�飬 ����¼��ɾ��
        	log.info("�Զ���Э�飬 ����¼��ɾ�� Start");
            MemCoreData coreData = MemCoreData.getInstance();
            // ȡ��SMG�����ļ���Ϣ
            List SMGCardList = coreData.getSMGCardList();
            
            for (int i=0; i< SMGCardList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
                try {
                    // �Զ���Э�飬 ����¼��ɾ��
                    utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                } catch (CommonException e) {
                    log.error("�Զ���Э��, ����¼��ɾ����SMG�·�����¼�����" + smg.getURL());
                }
            } // SMG �·�ָ�� END
            log.info("�Զ���Э�飬 ����¼��ɾ�� End");
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
		strBuff.append("indexstatus = 2 "); // Ĭ���Զ�¼��
		strBuff.append("where channelindex = " + vo.getIndex());
		//log.info("�Զ�¼��������ݿ⣺" + strBuff.toString());

		try {
			statement = conn.createStatement();
			statement.executeUpdate(strBuff.toString());

		} catch (Exception e) {
			log.error("�Զ�¼��������ݿ����: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		log.info("�Զ�¼��������ݿ�ɹ�!");
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
			log.error("�Զ�¼�����ͨ�����ݿ����: " + e.getMessage());
			log.error("�Զ�¼�����ͨ�����ݿ���� SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		//log.info("�Զ�¼�����ͨ�����ݿ�ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		DaoSupport.close(conn);
	}
    
    /**
     * ���Զ�¼��ɾ����ʱ, ɾ����ؽ�Ŀ��Ϣ
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
			log.error("�Զ�¼��ɾ����Ŀ��Ϣ����: " + e.getMessage());
			log.error("�Զ�¼��ɾ����Ŀ��Ϣ���� SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		//log.info("�Զ�¼��ɾ����Ŀ��Ϣ�ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
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
			log.error("�Զ�¼�����ͨ�����ݿ����: " + e.getMessage());
			log.error("�Զ�¼�����ͨ�����ݿ���� SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		strBuff = null;
		//log.info("�Զ�¼�����ͨ�����ݿ�ɹ�!");
		DaoSupport.close(conn);
	}
    
    private  List<SetAutoRecordChannelVO> GetProgramInfoByDownIndex(List downChannelSendList, boolean isChannelRemapping) throws DaoException {

		List<SetAutoRecordChannelVO> voList = new ArrayList();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		for(int i=0; i<downChannelSendList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			// ȡ����ؽ�ĿƵ����Ϣ
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
				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
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
			// ȡ����ؽ�ĿƵ����Ϣ
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
				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
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
			// ȡ����ؽ�ĿƵ����Ϣ
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
				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
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
		
		// ȡ����ؽ�ĿƵ����Ϣ
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
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("�Զ�¼�� ȡ�ý�Ŀ���ͨ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		
		return;
	}
    
    public static int GetIndexByFreq(SetAutoRecordChannelVO vo) throws DaoException {

		int index = 0;
		int indexChannel = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// �����������
		//strBuff.append("select channelindex,channelFlag  from channelprogramstatus where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		
		// ͳһƵ��ͬʱ�����׽�Ŀ
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
					// TODO Index �и����ֵ��Ҫ�������ļ�������
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
//				log.info("����Զ�¼����");
//			}

		} catch (Exception e) {
			log.error("�Զ�¼���ѯͨ�����ݿ����: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("�Զ�¼���ѯͨ�����ݿ�ɹ�!");
		
		vo.setIndex(index);
		return index;
	}
    
    /**
     * ͨ�����亯����ͨ���ϲ㷢������Ϣ�Զ��������ͨ��
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
		//�Ƿ�Ϊһ��һ�Ľ�Ŀ��Ϣ
		strBuff.append("select DevIndex, channelindex from channelremapping where delflag =0 and freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		
		System.out.println("�Ƿ�Ϊһ��һ�Ľ�Ŀ��Ϣ{freq+serviceid}:"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				//by tqy :2012-05-15 ��ǰͨ��ͣ�ã���ȡ��һ��ͨ����Ϣ
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
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isreturn) {
				DaoSupport.close(conn);
			}
		}
		
		// ͨ��Freq �� StatusFlag ��ȷ����ص�Ƶ���Ƿ����
		strBuff = new StringBuffer();
		// select * from channelremapping where devindex in (select DevIndex from channelremapping where freq = 259000) and statusflag = 0
		// ȡ����ؽ�ĿƵ����Ϣ
		//strBuff.append("select DevIndex, channelindex from channelremapping where freq = " + vo.getFreq() + " and statusflag = 0");
		
		//�Ƿ�Ϊδʹ�õ�Ƶ��ͨ����Ϣ
		strBuff.append("select * from channelremapping where devindex in (select DevIndex from channelremapping where delflag =0 and freq = " + vo.getFreq() + ") and delflag =0 and statusflag = 0");
		System.out.println("�Ƿ�Ϊδʹ�õ�Ƶ��ͨ����Ϣ{freq}"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				//by tqy :2012-05-15 ��ǰͨ��ͣ�ã���ȡ��һ��ͨ����Ϣ
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
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������2: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isreturn) {
				DaoSupport.close(conn);
			}
		}
		
		// û��ȡ����ص�ͨ������Ҫ���·���һ���µ�SMG�忨
		strBuff = new StringBuffer();
//		List<Integer> devIndexList = new ArrayList();
		// ȡ����ؽ�ĿƵ����Ϣ
		// SELECT * FROM channelremapping c where statusflag = 0 and Devindex not in (select devindex from channelremapping where statusflag = 1) group by devindex
		strBuff.append("SELECT * FROM channelremapping c where delflag =0 and statusflag = 0 and Devindex not in (select devindex from channelremapping where delflag =0 and statusflag = 1) group by devindex");
		System.out.println("û��ȡ����ص�ͨ������Ҫ���·���һ���µ�SMG�忨:"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ȡ��û�б������SMG�豸
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				//by tqy :2012-05-15 ��ǰͨ��ͣ�ã���ȡ��һ��ͨ����Ϣ
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
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������3: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������3 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isreturn) {
				DaoSupport.close(conn);
			}
		}
		
		
//		for(int i=0; i<devIndexList.size(); i++) {
//			// û��ȡ����ص�ͨ������Ҫ���·���һ���µ�SMG�忨
//			strBuff = new StringBuffer();
//			// ȡ����ؽ�ĿƵ����Ϣ
//			// SELECT count(DevIndex) FROM channelremapping c where statusflag = 0 and devindex = 1
//			strBuff.append("SELECT count(DevIndex), channelindex FROM channelremapping c where statusflag = 0 and devindex = " + devIndexList.get(i) + " order by channelindex");
//			
//			try {
//				statement = conn.createStatement();
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				// ȡ��û�б������SMG�豸
//				while(rs.next()){
//					// ��ʾһ��Ƶ��ͬʱ���׽�Ŀ����Ҫ���������ļ�
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
//				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������4: " + e.getMessage());
//				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������4 SQL: " + strBuff.toString());
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
		
		//log.info("�Զ�¼�� ȡ�ý�Ŀ���ͨ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
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
		
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select DevIndex, channelindex, Freq from channelremapping where DownIndex = " + vo.getDownIndex());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				freq = Integer.parseInt(rs.getString("Freq"));
				
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				
				if(vo.getFreq() == freq) {
					// �����ͬһ��Ƶ����µ�ǰ��Ŀ��Ϣ
					this.upChannelRemappingIndex(vo);
				} else {
					// ����ԭʼ��ĿstatusFlagΪ0
					delChannelRemappingIndex(vo);
				}
				
				return;
			}
		} catch (Exception e) {
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		
		//log.info("�Զ�¼�� ȡ�ý�Ŀ���ͨ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return;
	}
    
    /**
     * �Ƿ���ڵ�ǰ��Ŀ
     * @param vo
     * @return 1:���� 0:������
     * @throws DaoException
     */
    public static int isHaveProgramInRemapping(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		int flag = 0;
		ResultSet rs = null;
		
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select * from channelremapping where StatusFlag != 0 and freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				vo.setUdp(rs.getString("udp"));
				vo.setPort(Integer.parseInt(rs.getString("port")));
				flag = 1;
			}
		} catch (Exception e) {
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1 SQL: " + strBuff.toString());
			flag = 0;
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		
		
		//log.info("�Զ�¼�� ȡ�ý�Ŀ���ͨ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return flag;
	}
    
    private static void upChannelRemappingToSameFreq(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
		strBuff.append("update transmit.channelremapping c set freq= " + vo.getFreq() + ", ");
		strBuff.append("StatusFlag = 0, ");
		
		strBuff.append("lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append("where devIndex = " + vo.getDevIndex());
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
			log.error("�Զ�¼�� ����ͨ��ӳ������SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public void upChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
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
		System.out.println("����һ��һ�����ݣ�"+strBuff.toString());
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
			log.error("�Զ�¼�� ����ͨ��ӳ������SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public static void delChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
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
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		//log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    /**
     * ɾ����Ŀӳ�����ص���Ϣ
     * @param vo
     * @throws DaoException
     */
    public static SetAutoRecordChannelVO delRecordTaskIndex(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		
		// ȡ����ؽ�ĿƵ����Ϣ
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
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		
		//log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return vo;
	}
    
    
    /**
     * ɾ����Ŀӳ�����ص���Ϣ
     * @param vo
     * @throws DaoException
     */
    public static void updateRecordTaskIndex() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, ");
		strBuff.append(" DownIndex = 0, RecordType = 0, Action='', delflag = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
		strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where RecordType = 3 ");
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
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

		// ȡ����ؽ�ĿƵ����Ϣ
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" delflag = 1 ");
		strBuff.append("where ");
		strBuff.append(" freq = " + vo.getFreq());
		strBuff.append(" and ServiceID = " + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
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
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		//log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
	}
    
    public static void delChannelRemappingByProgram() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
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
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
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
		
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select DevIndex, channelindex from channelremapping where HDFlag = 1 and freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());		
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("ȡ�ø����Ŀ���ͨ������2: " + e.getMessage());
			log.error("ȡ�ø����Ŀ���ͨ������2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
	
		strBuff = new StringBuffer();
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select DevIndex, channelindex from channelremapping where statusflag = 0 and HDFlag = 1");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("ȡ�ø����Ŀ���ͨ������2: " + e.getMessage());
			log.error("ȡ�ø����Ŀ���ͨ������2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
		strBuff = null;
		
		strBuff = new StringBuffer();
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select DevIndex, channelindex from channelremapping where HDFlag = 1 and freq = " + vo.getFreq());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("ȡ�ø����Ŀ���ͨ������2: " + e.getMessage());
			log.error("ȡ�ø����Ŀ���ͨ������2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
		strBuff = null;
		
		strBuff = new StringBuffer();
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select DevIndex, channelindex from channelremapping where HDFlag = 1 order by lasttime");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				index = Integer.parseInt(rs.getString("channelindex"));
				vo.setIndex(index);
				isReturn = true;
				return;
			}
		} catch (Exception e) {
			log.error("ȡ�ø����Ŀ���ͨ������2: " + e.getMessage());
			log.error("ȡ�ø����Ŀ���ͨ������2 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			if(isReturn) {
				DaoSupport.close(conn);
			}
		}
		strBuff = null;
		DaoSupport.close(conn);
		
		//log.info("ȡ�ø����Ŀ���ͨ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return;
	}
    
    public static SetAutoRecordChannelVO getHDFlagByProgram(SetAutoRecordChannelVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		//LastFlag = 1���һ�θ��µ�Ƶ�������ݡ� ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select * from channelscanlist where  Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID() + " and LastFlag = 1");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				
				vo.setHDFlag(Integer.parseInt(rs.getString("HDTV")));
				
				vo.setProgramName(rs.getString("Program"));

				return vo;
			}
		} catch (Exception e) {
			log.error("ȡ�ý�Ŀ�����Ǵ���1: " + e.getMessage());
			log.error("ȡ�ý�Ŀ�����Ǵ���1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		//log.info("ȡ�ý�Ŀ�����ǳɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return vo;
	}
    
    /**
     * ȡ��7*24Сʱ�Զ�¼��ĸ���
     * @return
     * @throws DaoException
     */
    public static int getAutoRecordNumbers() throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		int count = 0;
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// ȡ����ؽ�ĿƵ����Ϣ
		//strBuff.append("SELECT count(*) FROM channelremapping c where (recordType = 2 or recordType = 1) and statusFlag = 1 ");
		
		//��Դ�жϸ���   JI LONG
		strBuff.append("SELECT HDFlag FROM channelremapping c where (recordType = 2 or recordType = 1) and statusFlag = 1 ");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
			while(rs.next()){
				//����ǰ
				//count = Integer.parseInt(rs.getString("count(*)"));
				//���ĺ�
				int isHD=Integer.parseInt(rs.getString("HDFlag"));
				if(isHD==0){
					count++;
				}else{
					count+=5;
				}
				return count;
			}
		} catch (Exception e) {
			log.error("ȡ��7*24Сʱ�Զ�¼��ĸ���: " + e.getMessage());
			log.error("ȡ��7*24Сʱ�Զ�¼��ĸ��� SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		//log.info("ȡ�ý�Ŀ�����ǳɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return count;
	}
    
    /**
     * ����ӳ���SMG����URL��ַ�����ڷ������ⶨλ
     * @param devIndex SMG�豸ͨ����
     * @param smgURL  SMG���յ�URL
     * @throws DaoException
     */
    public static void updateSMGURLByDevIndex(int devIndex, String smgURL) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
		strBuff.append("update transmit.channelremapping c set ");
		strBuff.append(" smgURL = '" + smgURL + "' ");
		strBuff.append(" where StatusFlag != 0 and DevIndex = " + devIndex);
		
		try {
			statement = conn.createStatement();
			
			statement.execute(strBuff.toString());
			
		} catch (Exception e) {
			log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
	}
    
    /**
     * ���ݽ�ĿƵ�� ������ ��ȡ��� ias ͨ���ŷ���  Ji Long 
     * @param isHD  ������
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
				// �����Ŀ����
				IPMInfoVO ipm = getBestIpmInfo();
				count = ipm.getIndexMin();
			}else{
				//��ȡ��Ƶ�������Ǹ�ipm ���ҷ��ظ�ipmʹ�õ������ͨ����
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
     * ȡ�ø�Ƶ���ias ͨ����
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
			log.error("��ѯ���ݿ�iasͨ������:"+e.getMessage());
			log.error("��ѯ���ݿ�iasͨ��sql:"+sql);
		}finally{
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ���Դ����:"+e.getMessage());
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
				// �����Ŀ����
				TSCInfoVO tsc = getBestTscInfo();
				count = tsc.getIndexMin();
			}else{
				//��ȡ��Ƶ�������Ǹ�tsc ���ҷ��ظ�tscʹ�õ������ͨ����
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
     * ȡ��û�з����TSCIndexͨ����
     * @param int isHDProgram ��Ҫ������Ƿ�Ϊ�����Ŀ 1:���� 0:����
     * @return
     
    public static int getTSCIndex(int isHDProgram) {
    	int tscIndex = 0;
    	String str="";
    	
    	try {
			List tscIndexList = getTSCIndexList();
			boolean isGetIndex = false;
			int count = 1;
			if(isHDProgram == 1) {
				// �����Ŀ����
				TSCInfoVO tsc = getBestTscInfo();
				count = tsc.getIndexMin();
				str="����";
			}else{
				TSCInfoVO tsc = getTsc();
				count = tsc.getIndexMin();
				str="����";
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
    	
    	log.info("����ͨ�����������"+tscIndex+str);
    	return tscIndex;
    }
    */
    
    /**
     * ��ѯ�������Ƿ����ĳ��Ԫ��
     * ���� true
     * ������ false
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
     * �¼ӷ��������Զ�¼���ĿΪ���� Ҳƽ������
     * ȡ����ѷ�������Ŀ��IPM
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
    		
    		// ȡ��TSCͨ�����б�
    		while(rs.next()){
    			String str=rs.getString("max(IpmIndex)");
    			if(str!=null){
    				ipmIndex=Integer.parseInt(rs.getString("max(IpmIndex)"));
    			}
    		}
    	} catch (Exception e) {
    		log.error("��ȡ����1�����Ŀipmͨ������: " + e.getMessage());
    		log.error("��ȡ����1�����Ŀipmͨ�� SQL: " + strBuff.toString());
    	} finally {
    		try {
    			DaoSupport.close(rs);
    			DaoSupport.close(statement);
    			DaoSupport.close(conn);
    		} catch (DaoException e) {
    			log.error("�رջ�ȡ�����Ŀipmͨ����Դ����: " + e.getMessage());
    		}
    	}
    	strBuff = null;
    	return ipmIndex;
    }
    /**
     * �¼ӷ��������Զ�¼���ĿΪ���� Ҳƽ������
     * ȡ����ѷ�������Ŀ��TSC
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
			
			// ȡ��TSCͨ�����б�
			while(rs.next()){
				String str=rs.getString("max(TscIndex)");
				if(str!=null){
					tscIndex=Integer.parseInt(rs.getString("max(TscIndex)"));
				}
			}
		} catch (Exception e) {
			log.error("��ȡ����1�����Ŀtscͨ������: " + e.getMessage());
			log.error("��ȡ����1�����Ŀtscͨ�� SQL: " + strBuff.toString());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�رջ�ȡ�����Ŀtscͨ����Դ����: " + e.getMessage());
			}
		}
		strBuff = null;
		return tscIndex;
	}
    
    /**
     * ȡ����ѷ�������Ŀ��TSC
     * @return
     */
    private static TSCInfoVO getBestTscInfo() {
    	TSCInfoVO minvo = null;
		// ��TSC�豸�б�����ȡ��, ��Ӧ�÷�����豸
		try {
			// ȡ��ÿ��TSC�豸��ǰ�ĸ����Ŀ��
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
		//log.info("ȡ����ѷ�������Ŀ IndexMin: " + minvo.getIndexMin() + " IndexMax: " + minvo.getIndexMax() + " HDCount: " + minvo.getHDNums());
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
     * ȡ����ѷ�������Ŀ��IPM
     * @return
     */
    private static IPMInfoVO getBestIpmInfo() {
    	IPMInfoVO minvo = null;
    	// ��IPM�豸�б�����ȡ��, ��Ӧ�÷�����豸
    	try {
    		// ȡ��ÿ��IPM�豸��ǰ�ĸ����Ŀ��
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
    	//log.info("ȡ����ѷ�������Ŀ IndexMin: " + minvo.getIndexMin() + " IndexMax: " + minvo.getIndexMax() + " HDCount: " + minvo.getHDNums());
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
     * ���ظ�ipm���ϸý�Ŀ���Ƿ񳬹����¼����Դ
     * isHD �ý�Ŀ�Ƿ���壨һ��������Ϊ5�����壩
     *  ���ز���flag ,true���� falseδ����
     *  Ji Long  2011-08-03 
     */
    private static  boolean isExceedIpmMaxRecordNum(int isHD,IPMInfoVO ipm){
    	boolean flag=false;
    	Statement statement = null;
    	Connection conn=null;
    	ResultSet rs = null;
    	/*
    	//����ǰ�����¼������Ŀ��  һ����������������
    	int MaxAutoRecordNum=coreData.getSysVO().getMaxAutoRecordNum();
    	//����ǰ��ipm�ĸ���  ֻҪһ��һ����
    	int IpmNum=0;
    	List IPMList=coreData.getIPMList();
    	for (int i = 0; i < IPMList.size(); i++) {
    		IPMInfoVO ip=(IPMInfoVO)IPMList.get(i);
    		if(ip.getRecordType()==2){
    			IpmNum++;
    		}
		}
    	//ÿ��ipm���¼�ƶ����ױ���  һ����������������
    	int ipmMaxRecordNum=MaxAutoRecordNum/IpmNum;
    	*/
    	//TODO ���¼��� ÿ��ipm ����һ��һ�ĸ���
    	
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
    		//���ϱ��η���Ľ�Ŀ  �����5  �����1
    		if(isHD==1){
    			count+=5;
    		}else{
    			count+=1;
    		}
    	} catch (Exception e) {
    		log.info("��ѯipm¼���������"+e.getMessage());
    		log.info("��ѯipm¼�����SQL��"+sql);
    	} finally {
    		try {
    			DaoSupport.close(rs);
    			DaoSupport.close(statement);
    			DaoSupport.close(conn);
    		} catch (Exception e2) {
    			log.info("�رղ�ѯtsc¼�������Դ����"+e2.getMessage());
    		}
    	}
    	//������η���� ��Ŀ�� �ӿ��еĽ�Ŀ��  ���� tsc¼�Ƶ�����Ŀ��
    	if(count > ipmMaxRecordNum ){
    		flag=true;
    	}
    	return flag;
    }
    /**
     * ���ظ�tsc���ϸý�Ŀ���Ƿ񳬹����¼����Դ
     * isHD �ý�Ŀ�Ƿ���壨һ��������Ϊ5�����壩
     *  ���ز���flag ,true���� falseδ����
     *  Ji Long  2011-08-03 
     */
    private static  boolean isExceedTscMaxRecordNum(int isHD,TSCInfoVO tsc){
    	boolean flag=false;
    	Statement statement = null;
		Connection conn=null;
		ResultSet rs = null;
		/*
		
    	//����ǰ�����¼������Ŀ��  һ����������������
    	int MaxAutoRecordNum=coreData.getSysVO().getMaxAutoRecordNum();
    	//����ǰ��TSC�ĸ���
    	int TscNum=coreData.getTSCList().size();
    	//ÿ��tsc���¼�ƶ����ױ���  һ����������������
    	int tscMaxRecordNum=MaxAutoRecordNum/TscNum;
		 */
    	
    	//TODO ���¼���ÿ̨tac�� ¼�����
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
    		//���ϱ��η���Ľ�Ŀ  �����5  �����1
    		if(isHD==1){
    			count+=5;
    		}else{
    			count+=1;
    		}
		} catch (Exception e) {
			log.info("��ѯtsc¼���������"+e.getMessage());
			log.info("��ѯtsc¼�����SQL��"+sql);
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (Exception e2) {
				log.info("�رղ�ѯtsc¼�������Դ����"+e2.getMessage());
			}
		}
		//������η���� ��Ŀ�� �ӿ��еĽ�Ŀ��  ���� tsc¼�Ƶ�����Ŀ��
		if(count > tscMaxRecordNum ){
			flag=true;
		}
    	return flag;
    }
    
    /**
     * ȡ��TSCͨ�����б�
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
			
			// ȡ��TSCͨ�����б�
			while(rs.next()){
				tscIndexList.add(Integer.parseInt(rs.getString("TscIndex")));
			}
		} catch (Exception e) {
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		strBuff = null;
		
		return tscIndexList;
	}
    
    /**
     * ȡ��IPMͨ�����б�
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
    		
    		// ȡ��TSCͨ�����б�
    		while(rs.next()){
    			IpmIndexList.add(Integer.parseInt(rs.getString("IpmIndex")));
    		}
    	} catch (Exception e) {
    		log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1: " + e.getMessage());
    		log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������1 SQL: " + strBuff.toString());
    	} finally {
    		DaoSupport.close(rs);
    		DaoSupport.close(statement);
    		DaoSupport.close(conn);
    	}
    	
    	strBuff = null;
    	
    	return IpmIndexList;
    }
    
    /**
     * ȡ��TSC�豸�б��е�ǰ�����Ŀ��
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

				// ȡ��TSCͨ�����б�
				while (rs.next()) {
					tsc.setTVNums(Integer.parseInt(rs.getString("count(*)")));
					//log.info("getTscTVNums IndexMin: " + tsc.getIndexMin() + " IndexMax: " + tsc.getIndexMax() + " HDCount: " + tsc.getTVNums());
					break;
				}
			} catch (Exception e) {
				log.error("ȡ��TSC�豸��ǰ�����Ŀ������1: " + e.getMessage());
				log.error("ȡ��TSC�豸��ǰ�����Ŀ������1 SQL: " + strBuff.toString());
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
     * ȡ��IPM�豸�б��е�ǰ�����Ŀ��
     * @return List
     * @throws DaoException
     */
    private static List getIpmHDNums(List IPMSendList) throws DaoException {
    	
    	Statement statement = null;
    	Connection conn = DaoSupport.getJDBCConnection();
    	
    	for (int t = 0; t < IPMSendList.size(); t++) {
    		IPMInfoVO ipm = (IPMInfoVO) IPMSendList.get(t);
    		//ֻ����һ��һ����ʱ �� ����ƽ������
    		if(ipm.getRecordType()==2){
	    		StringBuffer strBuff = new StringBuffer();
	    		
	    		ResultSet rs = null;
	    		
	    		strBuff.append("select count(*) from channelremapping where StatusFlag != 0 and IpmIndex >= " + ipm.getIndexMin());
	    		strBuff.append(" and IpmIndex <= " + ipm.getIndexMax() + " and HDFlag =1");
	    		
	    		try {
	    			statement = conn.createStatement();
	    			
	    			rs = statement.executeQuery(strBuff.toString());
	    			
	    			// ȡ��TSCͨ�����б�
	    			while (rs.next()) {
	    				ipm.setHDNums(Integer.parseInt(rs.getString("count(*)")));
	    				//log.info("getTscHDNums IndexMin: " + tsc.getIndexMin() + " IndexMax: " + tsc.getIndexMax() + " HDCount: " + tsc.getHDNums());
	    				break;
	    			}
	    		} catch (Exception e) {
	    			log.error("ȡ��Ipm�豸��ǰ�����Ŀ������1: " + e.getMessage());
	    			log.error("ȡ��Ipm�豸��ǰ�����Ŀ������1 SQL: " + strBuff.toString());
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
     * ȡ��TSC�豸�б��е�ǰ�����Ŀ��
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

				// ȡ��TSCͨ�����б�
				while (rs.next()) {
					tsc.setHDNums(Integer.parseInt(rs.getString("count(*)")));
					//log.info("getTscHDNums IndexMin: " + tsc.getIndexMin() + " IndexMax: " + tsc.getIndexMax() + " HDCount: " + tsc.getHDNums());
					break;
				}
			} catch (Exception e) {
				log.error("ȡ��TSC�豸��ǰ�����Ŀ������1: " + e.getMessage());
				log.error("ȡ��TSC�豸��ǰ�����Ŀ������1 SQL: " + strBuff.toString());
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
