package com.bvcom.transmit.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
/**
 * ����Զ�¼����еĽ�Ŀ��Ϣ ͬʱ ������յĽ�Ŀ��Ϣ���ϱ����·���TSC
 * @author JI LONG  2011-5-13 
 *
 */
public class CleanChannelAndTSC {
	private static Logger log =Logger.getLogger(CleanChannelAndTSC.class.getSimpleName());
	private static MemCoreData coreData = MemCoreData.getInstance();
	@SuppressWarnings("unchecked")
	private static List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
	private String downString = new String();
	private UtilXML utilXML = new UtilXML();
	private MSGHeadVO bsData = new MSGHeadVO();
	
    public CleanChannelAndTSC( MSGHeadVO bsData) {
        this.bsData = bsData;
    }
	public void chean(){
		TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
		
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelremapping ;";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				int freq=rs.getInt("Freq");
				int serviceid=rs.getInt("ServiceID");
				if(freq!=0&&serviceid!=0){
					sqlStr="SELECT * FROM channelscanlist where freq = "+freq+" and serviceid = "+serviceid+";";
					statement = conn.createStatement();
					ResultSet r = statement.executeQuery(sqlStr);
					if(!r.next()){
						sqlStr="update channelremapping set DelFlag = 1 where freq = "+freq+" and serviceid = "+serviceid+";";
						statement = conn.createStatement();
						statement.executeUpdate(sqlStr);
					}
				}
			}
		}catch (Exception e) {
			log.error("�����Զ�¼���Ŀ��Ϣ����: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
		List<SetAutoRecordChannelVO> list=new ArrayList<SetAutoRecordChannelVO>(); 
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelremapping where DelFlag = 1 ;";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				SetAutoRecordChannelVO srcVo=new SetAutoRecordChannelVO();
				srcVo.setAction("Del");
				srcVo.setFreq(rs.getInt("Freq"));
				srcVo.setServiceID(rs.getInt("ServiceID"));
				srcVo.setQAM(rs.getInt("QAM"));
				srcVo.setSymbolRate(rs.getInt("SymbolRate"));
				srcVo.setDevIndex(rs.getInt("DevIndex"));
				srcVo.setTscIndex(rs.getInt("TscIndex"));
				srcVo.setVideoPID(rs.getInt("VideoPID"));
				srcVo.setAudioPID(rs.getInt("AudioPID"));
				srcVo.setRecordType(rs.getInt("RecordType"));
				list.add(srcVo);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
	        
		SetAutoRecordChannelParse recordString=new SetAutoRecordChannelParse();
		downString=recordString.createForDownXML(bsData, list, "Del", false);
		
		String url = "";
		String retString = "";
	    int isError = 0;
        for(int t=0;t<TSCSendList.size();t++){
            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);
            try {
            	if (tsc.getRecordType() != 1 && tsc.getRecordType() != 2) {
            		// ��������Զ�¼�����̬¼��ͽ�����һ��
            		continue;
            	}
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
		//����ɾ������ ��ɾ�����Ϊ1�� ��Ŀ��Ϣ���
		try {
			SetAutoRecordChannelHandle.delChannelRemappingByProgram();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
}
