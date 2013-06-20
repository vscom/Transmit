package com.bvcom.transmit.handle.index;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.index.GetNvrStatusParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.index.GetNvrStatusVO;

public class GetNvrStatusHandle {
	private static Logger log = Logger.getLogger(GetNvrStatusHandle.class
			.getSimpleName());

	private MSGHeadVO bsData = new MSGHeadVO();

	private String downString = new String();

	private UtilXML utilXML = new UtilXML();

	public GetNvrStatusHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}

	/**
	 * 通道状态查询
	 * @throws DaoException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void downXML() {
		String upString = "";

		List nvrslist = new ArrayList();
		
		Document document = null;
		try {
			document = utilXML.StringToXML(this.downString);
		} catch (CommonException e) {
			log.error("通道状态查询StringToXML Error: " + e.getMessage());
		}

		GetNvrStatusParse getnvrs = new GetNvrStatusParse();
//		GetNvrStatusVO nvrslist = getnvrs.getIndexByDownXml(document);
//		List<Integer> index = new ArrayList<Integer>();
//		for (int i = 0; i < nvrslist.getIndex().size(); i++) {
//			index.add(nvrslist.getIndex().get(i));
//		}

		// 查询数据库得到通道的状态
//		GetNvrStatusVO nvrstatus = new GetNvrStatusVO();
		this.upindexStat(nvrslist);

		// 上报回复的xml给中心,自己返回成功
		upString = getnvrs.ReturnXMLByURL(this.bsData, nvrslist);
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("通道状态回复失败: " + e.getMessage());
		}

		bsData = null;
		downString = null;
		utilXML = null;

	}

	public void upindexStat(List list) {

		StringBuffer strBuff = new StringBuffer();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn;
		String desc = "";
		try {
			conn = DaoSupport.getJDBCConnection();
		
			try {
				strBuff.append("select * from channelremapping group by devindex ");
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					GetNvrStatusVO vo = new GetNvrStatusVO();
					int channelindex = Integer.parseInt(rs.getString("DevIndex"));
					
					vo.setIndex(channelindex);
					
					vo.setFreq(Integer.parseInt(rs.getString("freq")));
					
					vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
					
					// '录像类型: 0：不录像，1:代表故障触发录制   2：24小时录像'
					switch(Integer.parseInt(rs.getString("RecordType")))
					{
					   case 0:desc = "正常状态";
					   		vo.setStatus(0);
						   	break;
					   case 2:desc = "正在自动录制";
					   		vo.setStatus(4);
						   	break;
					}
					vo.setDesc(desc);
						//list.remove(list.get(i));
					list.add(vo);
				}
				
			} catch (Exception e) {
				log.error("通道状态查询数据库错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		} catch (DaoException e1) {
			log.error("通道状态查询数据库错误: " + e1.getMessage());
			//e1.printStackTrace();
		}

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
