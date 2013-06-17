package com.bvcom.transmit.handle.alarm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.system.IMonitorService;
import com.bvcom.transmit.core.system.MonitorInfoBean;
import com.bvcom.transmit.core.system.MonitorServiceImpl;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.alarm.AlarmSearchESetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchESetVO;

public class AlarmSearchESetHandle {
	private static Logger log = Logger.getLogger(AlarmSearchESetHandle.class
			.getSimpleName());

	private MSGHeadVO bsData = new MSGHeadVO();

	private String downString = new String();

	private UtilXML utilXML = new UtilXML();
	
	public AlarmSearchESetHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}

	/**
	 * �����ϱ�ָ���ѯ
	 * ֱ�ӷ��ص�ǰ��������Ϣ.
	 */
	@SuppressWarnings("unchecked")
	public void downXML() {
		AlarmSearchESetParse alarmsearchp = new AlarmSearchESetParse();
		List<AlarmSearchESetVO> volist = new ArrayList();
		volist.add(this.getTEMPERing());//�¶�
		volist.add(this.getETing());//��ѹ
		volist.add(this.getAQUOSITYing());//ʪ��
		volist.add(this.getSTATing());//״̬
		volist.add(this.getCPUing());//cpu
		//volist.add(this.getHDing());//Ӳ��
		volist.add(this.getEMSing());//�ڴ�
		String upString = alarmsearchp.ReturnXMLByURL(this.bsData, volist, 0);
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("�����ϱ�ָ���ѯ�ظ�ʧ��: " + e.getMessage());
		}
	}
	//���õ��Ļ�������list���
	public void insterEtable(List<AlarmSearchESetVO> volist){
		StringBuffer strBuff = new StringBuffer();
        
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
	        conn = DaoSupport.getJDBCConnection();
	        strBuff.append("insert into systemstatus (datetime ,temperature,voltage,humidity,status,cpu,mem,) values (");
	        strBuff.append("'" + CommonUtility.getDateTime() + "', ");
	        for(int i=0;i<volist.size();i++)
	        {
	             strBuff.append("'" + volist.get(i).getValue()+ "'");
	             if(i!=volist.size()-1)
	            	 strBuff.append("','");
	        }
	        strBuff.append(")");
	        log.info("���л������ݿ⣺" + strBuff.toString());
	        
            statement = conn.createStatement();
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("���л������ݿ����: " + e.getMessage());
        } finally {
            try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("���л������ݿ�رմ���:"+e.getMessage());
				e.printStackTrace();
			}
	    }
        log.info("���л������ݿ�ɹ�!");
	}
	public AlarmSearchESetVO getCPUing(){
		AlarmSearchESetVO vo = new AlarmSearchESetVO();
		vo.setType(54);
		vo.setDesc("CPU");
		vo.setTime(CommonUtility.getDateTime());
		IMonitorService service = new MonitorServiceImpl();   
        try {
			MonitorInfoBean monitorInfo = service.getMonitorInfoBean();
			vo.setValue((int)monitorInfo.getCpuRatio());
		} catch (Exception e) {
			vo.setValue(60);
			e.printStackTrace();
		} 
		return vo;
	}

	public AlarmSearchESetVO getSTATing(){
		AlarmSearchESetVO vo = new AlarmSearchESetVO();
		vo.setType(53);
		vo.setDesc("״̬");
		vo.setTime(CommonUtility.getDateTime());
		vo.setValue(this.getint1To100());
		return vo;
	}
	public AlarmSearchESetVO getTEMPERing(){
		AlarmSearchESetVO vo = new AlarmSearchESetVO();
		vo.setType(50);
		vo.setDesc("�¶�");
		vo.setTime(CommonUtility.getDateTime());
		vo.setValue(this.getint1To100());
		return vo;
	}
	public AlarmSearchESetVO getAQUOSITYing(){
		AlarmSearchESetVO vo = new AlarmSearchESetVO();
		vo.setType(52);
		vo.setDesc("ʪ��");
		vo.setTime(CommonUtility.getDateTime());
		vo.setValue(this.getint1To100());
		return vo;
	}
	public AlarmSearchESetVO getEMSing(){
		AlarmSearchESetVO vo = new AlarmSearchESetVO();
		vo.setType(55);
		vo.setDesc("�ڴ�");
		vo.setTime(CommonUtility.getDateTime());
		IMonitorService service = new MonitorServiceImpl();   
        try {
			MonitorInfoBean monitorInfo = service.getMonitorInfoBean();
			long cout = monitorInfo.getTotalMemorySize();//�������ڴ�
			long emsusing = cout-monitorInfo.getFreeMemory();//ʹ�õ������ڴ�
			vo.setValue((int)(emsusing/cout));
		} catch (Exception e) {
			vo.setValue(73);
			e.printStackTrace();
		} 
		return vo;
	}
	public AlarmSearchESetVO getETing(){
		AlarmSearchESetVO vo = new AlarmSearchESetVO();
		vo.setType(51);
		vo.setDesc("��ѹ");
		vo.setTime(CommonUtility.getDateTime());
		vo.setValue(this.getint1To100());
		return vo;
	}
	//��ȡ�����
	public int getint1To100(){
		return (int) (Math.random() * 100);
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
