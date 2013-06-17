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
 * ����¼������
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
     * 1. �ֱ��·���SMG��TSC
     * 2. �·��ɹ������سɹ�
     * 3. �������ϱ��ɹ���Ϣ
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
        
        List SMGList = new ArrayList();
//        List TSCList = coreData.getTSCList();//tsc���б���Ϣ
//        List TSCList = new ArrayList();//tsc���б���Ϣ
        List channelSendList = new ArrayList();// SMG downChannelList Index List
        
    	this.downString = this.downString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
    	this.downString = this.downString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
    	
        String smgDownString = this.downString;
        String tscDownString = this.downString;
        // �ж��Ƿ���䵽channel
        boolean isNoMoreChannel = false;
        
        SysInfoVO sysInfoVO = coreData.getSysVO();
        List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
        
        SetAutoRecordChannelHandle setAutoRecordChannelHandle = new SetAutoRecordChannelHandle();
        SetAutoRecordChannelParse setAutoRecordChannel = new SetAutoRecordChannelParse();
       
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("����¼��StringToXML Error: " + e.getMessage());
        }
       
        ProvisionalRecordTaskSetParse RecordTaskSet = new ProvisionalRecordTaskSetParse();
        List<ProvisionalRecordTaskSetVO> RecordTaskSetList = RecordTaskSet.getIndexByDownXml(document);
        
        int index = 0;
        int indexOrg = 0;
        // ȡ���·�SMG URL�б���Ϣ
        SetAutoRecordChannelVO recordVO = null;
        
    	int channelIndex = 0;
        int channel = 0;
        
        for(int i= 0; i<RecordTaskSetList.size(); i++) {
            ProvisionalRecordTaskSetVO vo = RecordTaskSetList.get(i);
        	
	       	if(vo.getAction().equals("Set")) {
	       		// ����¼������
				indexOrg = vo.getIndex();
				try {
					vo = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
					index = vo.getIndex();
					channelIndex = vo.getDevIndex();
				} catch (DaoException e1) {
				
				}
				//����һ��һ�Ľ�Ŀ
				if(index == 0) {
					// �����ݿ����Զ���һ��û���ù���ͨ����
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
						//������һ��һ�忨�з���忨��Դ
						setAutoRecordChannelHandle.GetChannelRemappingbyFreq(recordVO);
						if(recordVO.getIndex() == 0) {
							isNoMoreChannel = true;
							log.error("����¼��ͨ������, û�з��䵽��ص�ͨ����");
							break;
						}
						// ����¼�� 0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��) 3: ����¼��
						recordVO.setRecordType(3);

	    				 // ȡ��TSCIndexͨ���� By: Bian Jiang 2011.4.8
						recordVO.setTscIndex(SetAutoRecordChannelHandle.getTSCIndex(recordVO.getHDFlag(),recordVO.getFreq()));
	    				 
						//����¼���Ŀ����һ��һӳ�������RecordType =3 
						setAutoRecordChannelHandle.upChannelRemappingIndex(recordVO);
						
						vo.setIndex(recordVO.getIndex());
						vo.setDevIndex(recordVO.getDevIndex());
					 	channelIndex = vo.getDevIndex();
					} catch (DaoException e) {
						log.error("ȡ������¼��ͨ������: " + e.getMessage());
					}
				}
				
				tscDownString = tscDownString.replaceAll("Index=\"" + indexOrg + "\"", "Index=\"" + vo.getIndex() + "\"");
	       	} else {
	       		// ����¼��ɾ��
	       		recordVO = new SetAutoRecordChannelVO();
	       		try {
	       			recordVO.setFreq(vo.getFreq());
	       			recordVO.setServiceID(vo.getServiceID());
	       			//���һ��һӳ�������
	       			recordVO = setAutoRecordChannelHandle.delRecordTaskIndex(recordVO);
	       			channelIndex = recordVO.getDevIndex();
	       			//�����������¼���
					updateRecordTaskIndex(vo);
					log.info("����¼��ɾ���ɹ�: Freq:" + vo.getFreq() + " ServiceID:" + vo.getServiceID());
				} catch (DaoException e) {
					log.error("����¼��ɾ������: " + e.getMessage());
				}
	       	}
        	
	       	
	       	//��ȡ����¼��Э�����ж��ٸ��忨ͨ��
			if (channel != channelIndex && channelIndex != 0) {
				channelSendList.add(channelIndex);
				channel = channelIndex;
			} else {
				channel = channelIndex;
			}
			//��ȡͨ����Ӧ��SMG�忨������Ϣ
            CommonUtility.checkSMGChannelIndex(vo.getDevIndex(), SMGList);
//            CommonUtility.checkTSCChannelIndex(vo.getIndex(), TSCList);
        }
        
        if (channelSendList.size() != 0 && !isNoMoreChannel) {
        	try {
        		SetAutoRecordChannelHandle SetAutoRecordChannel = new SetAutoRecordChannelHandle();
        		List<SetAutoRecordChannelVO> AutoRecordlistSMGNew = SetAutoRecordChannel.GetProgramInfoByIndex(channelSendList, true);
        		
	        	smgDownString = setAutoRecordChannel.createForDownXML(this.bsData, AutoRecordlistSMGNew, "Set", true);
        		
			} catch (Exception e) {
				log.error("ͨ��ͨ��ȡ�ý�Ŀ��Ϣ����: " + e.getMessage());
			}
        }
        
        if(!isNoMoreChannel) {
            // SMG �·�ָ��
            for (int i=0; i< SMGList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGList.get(i);
                try {
                    // ����¼����Ϣ�·� timeout 1000*30 ��ʮ��

                    utilXML.SendDownNoneReturn(smgDownString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                } catch (CommonException e) {
                    log.error("����¼����SMG�·�����¼�����" + smg.getURL());
                }
                try {
                	SetAutoRecordChannelHandle.updateSMGURLByDevIndex(smg.getIndex(), smg.getURL());
                } catch (Exception ex) {
                	
                }
            } // SMG �·�ָ�� END
            
            // TSC �·�ָ������е�TSC���·�����¼��
            for (int i=0; i< TSCSendList.size(); i++) {
                TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(i);
                try {
            		// ����¼����Ϣ�·� timeout 1000*30 ��ʮ��
                    utilXML.SendDownNoneReturn(tscDownString, tsc.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                } catch (CommonException e) {
                    log.error("����¼����TSC�·�����¼�����" + tsc.getURL());
                }
            } // TSC �·�ָ�� END
            upString = RecordTaskSet.ReturnXMLByURL(bsData, 0);
        } 
        //û�п�ʹ�õİ忨��Դ
        else 
        {
        	upString = RecordTaskSet.ReturnXMLByURL(bsData, 1);
        }

        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("����¼��ѡ̨��Ϣʧ��: " + e.getMessage());
        }
        
        //��������¼����Ϣ
        if(!isNoMoreChannel) {
        	this.insertNewTaskRecordTable(RecordTaskSetList);	
        }
        
        
        //BY TQY 2012-05-20  1�� ��������¼���Ƿ���� 2���ڳ�������ʱͬ��Ҳ��Ҫ��������¼����Ϣ
        // ����¼����, ɾ���Ѿ����ڵ�����¼��
        //RecordTaskThread RecordTaskProcess = new RecordTaskThread();
        //RecordTaskProcess.start();
        
        bsData = null;
        this.downString = null;
        SMGList = null;
        utilXML = null;
        RecordTaskSet = null;
        
    }
    
    /**
     * ����¼���ü�¼���(Ŀǰ�����������)
     * ����¼��״̬���
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
                
                //TODO ���URL���Լ����ɵ�
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
        	log.error("����SQL: " + sqlBuff.toString());
        	log.error("����¼��������ݿ����: " + e.getMessage());
        } finally {
            try {
                DaoSupport.close(statement);
                DaoSupport.close(conn);
            } catch (DaoException e) {
            }
        }
        log.info("����¼��������ݿ�ɹ�!");
    }
    
    /**
     * ɾ������¼�����ص���Ϣ
     * @param vo
     * @throws DaoException
     */
    public static void updateRecordTaskIndex(ProvisionalRecordTaskSetVO vo) throws DaoException {

		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		// ȡ����ؽ�ĿƵ����Ϣ
		
		strBuff.append("update transmit.taskrecord c set ");
		strBuff.append(" statusFlag = 0 ");
		strBuff.append(" where Taskid = " + vo.getTaskID());
		
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
		log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
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
				log.error("����¼���ѯ���ݿ����: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		} catch (DaoException e1) {
			log.error("����¼���ѯ���ݿ����: " + e1.getMessage());
		}
    	
    	return recordTaskList;
    }
    
}
