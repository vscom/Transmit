package com.bvcom.transmit.handle.index;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.system.IMonitorService;
import com.bvcom.transmit.core.system.MonitorInfoBean;
import com.bvcom.transmit.core.system.MonitorServiceImpl;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.index.GetIndexESetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.index.GetIndexESetVO;

public class GetIndexESetHandle {
	private static Logger log = Logger.getLogger(GetIndexESetHandle.class
			.getSimpleName());

	private MSGHeadVO bsData = new MSGHeadVO();

	private String downString = new String();

	private UtilXML utilXML = new UtilXML();

	/**
	 * ���л���ָ���ѯ
	 * ֱ�ӷ��ص�ǰ��������Ϣ.
	 */
	@SuppressWarnings("unchecked")
	public void downXML() {
		GetIndexESetParse indexEsetp = new GetIndexESetParse();
		List<GetIndexESetVO> volist = new ArrayList();
		volist.add(this.getTEMPERing());//�¶�
		volist.add(this.getETing());//��ѹ
		volist.add(this.getAQUOSITYing());//ʪ��
		volist.add(this.getSTATing());//״̬
		volist.add(this.getCPUing());//cpu
		volist.add(this.getHDing());//Ӳ��
		volist.add(this.getEMSing());//�ڴ�
		
		String upString = indexEsetp.ReturnXMLByURL(this.bsData, volist, 0);
		
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("���л���ָ���ѯ�ظ�ʧ��: " + e.getMessage());
		}
		
		//������
		
	}
	//���õ������л�������list���
	public void insterEtable(List<GetIndexESetVO> volist){
		StringBuffer strBuff = new StringBuffer();
        
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
	        conn = DaoSupport.getJDBCConnection();
	        strBuff.append("insert into systemstatus (datetime ,temperature,voltage,humidity,status,cpu,harddisk,mem,) values (");
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
	public GetIndexESetVO getCPUing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(54);
		vo.setDesc("CPU");
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
	public GetIndexESetVO getHDing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(55);
		vo.setDesc("Ӳ��");
		File[] roots = File.listRoots();
        double constm = 1024 * 1024 * 1024 ;
        double total = 1d;
        double using = 1d;
        for (File _file : roots) {
//            System.out.println(_file.getPath());
//            System.out.println("ʣ��ռ� = " + doubleFormat(_file.getFreeSpace()/constm)+" G");
//            System.out.println("��ʹ�ÿռ� = " + doubleFormat(_file.getUsableSpace()/constm)+" G");
//            System.out.println(_file.getPath()+"���ܴ�С = " + doubleFormat(_file.getTotalSpace()/constm)+" G");
//            System.out.println();
        	
//            total+=_file.getTotalSpace();
//            using+=_file.getUsableSpace();
        }
        int value = this.getint1To100();
        value = (int)(using/total*100);
		vo.setValue(value);
		return vo;
	}
	public GetIndexESetVO getSTATing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(53);
		vo.setDesc("״̬");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public GetIndexESetVO getTEMPERing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(50);
		vo.setDesc("�¶�");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public GetIndexESetVO getAQUOSITYing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(52);
		vo.setDesc("ʪ��");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public GetIndexESetVO getEMSing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(56);
		vo.setDesc("�ڴ�");
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
	public GetIndexESetVO getETing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(51);
		vo.setDesc("��ѹ");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public static String doubleFormat(double d){   
        //DecimalFormat df = new DecimalFormat("0.##");   //С�������λ
        DecimalFormat df = new DecimalFormat("0");   //ȡ����
        return df.format(d);                   
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

	public GetIndexESetHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}

}
