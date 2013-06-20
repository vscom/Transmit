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
	 * 运行环境指标查询
	 * 直接返回当前机器的信息.
	 */
	@SuppressWarnings("unchecked")
	public void downXML() {
		GetIndexESetParse indexEsetp = new GetIndexESetParse();
		List<GetIndexESetVO> volist = new ArrayList();
		volist.add(this.getTEMPERing());//温度
		volist.add(this.getETing());//电压
		volist.add(this.getAQUOSITYing());//湿度
		volist.add(this.getSTATing());//状态
		volist.add(this.getCPUing());//cpu
		volist.add(this.getHDing());//硬盘
		volist.add(this.getEMSing());//内存
		
		String upString = indexEsetp.ReturnXMLByURL(this.bsData, volist, 0);
		
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("运行环境指标查询回复失败: " + e.getMessage());
		}
		
		//入库操作
		
	}
	//将得到的运行环境数据list入库
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
	        log.info("运行环境数据库：" + strBuff.toString());
	        
            statement = conn.createStatement();
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("运行环境数据库错误: " + e.getMessage());
        } finally {
            try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("运行环境数据库关闭错误:"+e.getMessage());
				e.printStackTrace();
			}
	    }
        log.info("运行环境数据库成功!");
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
		vo.setDesc("硬盘");
		File[] roots = File.listRoots();
        double constm = 1024 * 1024 * 1024 ;
        double total = 1d;
        double using = 1d;
        for (File _file : roots) {
//            System.out.println(_file.getPath());
//            System.out.println("剩余空间 = " + doubleFormat(_file.getFreeSpace()/constm)+" G");
//            System.out.println("已使用空间 = " + doubleFormat(_file.getUsableSpace()/constm)+" G");
//            System.out.println(_file.getPath()+"盘总大小 = " + doubleFormat(_file.getTotalSpace()/constm)+" G");
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
		vo.setDesc("状态");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public GetIndexESetVO getTEMPERing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(50);
		vo.setDesc("温度");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public GetIndexESetVO getAQUOSITYing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(52);
		vo.setDesc("湿度");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public GetIndexESetVO getEMSing(){
		GetIndexESetVO vo = new GetIndexESetVO();
		vo.setType(56);
		vo.setDesc("内存");
		IMonitorService service = new MonitorServiceImpl();   
        try {
			MonitorInfoBean monitorInfo = service.getMonitorInfoBean();
			long cout = monitorInfo.getTotalMemorySize();//总物理内存
			long emsusing = cout-monitorInfo.getFreeMemory();//使用的物理内存
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
		vo.setDesc("电压");
		vo.setValue(this.getint1To100());
		return vo;
	}
	public static String doubleFormat(double d){   
        //DecimalFormat df = new DecimalFormat("0.##");   //小数点后两位
        DecimalFormat df = new DecimalFormat("0");   //取整数
        return df.format(d);                   
    }
	//获取随机数
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
