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
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.rec.RecordMbpsFlag;
import com.bvcom.transmit.parse.video.RecordParamSetExParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class RecordParamSetExHandle {
    private static Logger log = Logger.getLogger(RecordParamSetExHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    private static MemCoreData coreData = MemCoreData.getInstance();
    
    private static SysInfoVO sysInfoVO = coreData.getSysVO();
    
    public RecordParamSetExHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    public void downXML() {
        // 返回数据
        String upString = "";
      //ji long 2011-5-13
        RecordParamSetExParse rpsp=new RecordParamSetExParse();
        List<RecordMbpsFlag> list=new ArrayList<RecordMbpsFlag>();
        Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
        Document document;
		try {
			document = utilXML.StringToXML(downString);
			list= rpsp.parse(document);
		} catch (CommonException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int count =0;
		
		//遍历协议中增加码率的 节目个数 
		//ji long 2011-5-13
		for(int i=0;i<list.size();i++){
			RecordMbpsFlag rmf=list.get(i);
			if(rmf.getHeight()>288||rmf.getWidth()>352){
				count++;
			}
		}
		
		
		
		//查找数据库中码率状态个数
		//并且加上平台协议中增加码率 节目的个数
		//ji long 2011-5-13
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT count(*) FROM channelremapping where MbpsFlag = 1";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				count+=rs.getInt(1);
			}
		}catch (Exception e) {
			log.error("查找码率状态错误: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		
		//如果库中现有个数 加协议中的 个数超过 配置文件中最大数 则发送资源不足协议给平台 
		//同时结束该方法 不给TSC发送协议
		//ji long 2011-5-13
		if(count>sysInfoVO.getMaxRecordMbpsFlag()){
			upString = rpsp.ReturnXMLByURL(this.bsData, 1);
			try {
				log.warn("### 自动录像码率设置-没有更多的资源可用 ### ");
				utilXML.SendUpXML(upString, bsData);
			} catch (CommonException e) {
				log.error("码率设置回复失败: " + e.getMessage());
			}
			return ;
		}

		//对比数据库码率设置状态 并更新
		//ji long 2011-5-13
		for(int i=0;i<list.size();i++){
			RecordMbpsFlag rmf=list.get(i);
			//入库查询
			try {
				conn = DaoSupport.getJDBCConnection();
				statement = conn.createStatement();
				String sqlStr = "SELECT MbpsFlag FROM channelremapping where freq = "+rmf.getFreq()+" and serviceid = "+rmf.getServiceID()+";";
				rs = statement.executeQuery(sqlStr);
				while (rs.next()) {
					int MbpsFlag=rs.getInt("MbpsFlag");
					if(MbpsFlag==0&&(rmf.getHeight()>288||rmf.getWidth()>352)){
						sqlStr="update channelremapping set MbpsFlag = 1 where freq = "+rmf.getFreq()+" and serviceid = "+rmf.getServiceID()+";";
						statement = conn.createStatement();
						statement.executeUpdate(sqlStr);
					}
					if(MbpsFlag==1&&(rmf.getHeight()<=288||rmf.getWidth()<=352)){
						sqlStr="update channelremapping set MbpsFlag = 0 where freq = "+rmf.getFreq()+" and serviceid = "+rmf.getServiceID()+";";
						statement = conn.createStatement();
						statement.executeUpdate(sqlStr);
					}
				}
			}catch (Exception e) {
				log.error("查找码率状态错误: " + e.getMessage());
			} finally {
				try {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
					DaoSupport.close(conn);
				} catch (DaoException e) {
					log.error("关闭数据库失败: " + e.getMessage());
				}
			}
		}
		
        MemCoreData coreData = MemCoreData.getInstance();
        // 取得TSC配置文件信息
        List TSCList = coreData.getTSCList();
        
        // TODO 当Index>0时，对轮循监测组进行码率设置
        String url = "";
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            try {
                if(!url.equals(tsc.getURL())) {
                    // 任务录像信息下发 timeout 1000*30 三十秒
                    utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
                    url = tsc.getURL();
                }
            } catch (CommonException e) {
                log.error("高清录像相关设置向TSC下发任务录像出错：" + tsc.getURL());
                upString = "";
            }
        } // TSC 下发指令 END
        
        try {
    		upString = utilXML.getReturnXML(bsData, 0);
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("高清录像相关设置信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        TSCList = null;
        utilXML = null;
        
    }
    
    
    
}
