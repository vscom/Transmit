package com.bvcom.transmit.handle.si;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.si.EPGQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.si.EPGQueryVo;

public class EPGQueryHandle {
	
	private static Logger log = Logger.getLogger(EPGQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public EPGQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    public EPGQueryHandle() {
    }
    
	public void downXML(){
		log.info("读取EPG信息");
		// MHP信息 
        Document document = null;

        SysInfoVO sysVO = coreData.getSysVO();
        UtilXML utilXML = new UtilXML();
        EPGQueryParse epgQueryParse = new EPGQueryParse();
        
		// 从XML文件中取得的时间信息
		String dataStr = null;
		String EPGXMLStr = null;
		
		// 当前的时间目录名
		
		// 取得前一个小时的时间
		String filename = null;
		Element root = null;
		List epgQueryList = null;
		
		try {
			document = utilXML.StringToXML(downString);
		} catch (CommonException e1) {
			log.error("EPG XML 解析出错: " + e1.getMessage());
		}

		root = document.getRootElement();
		Element ele = null;
		
		// EPG信息
		ele = root.element("EPGQuery");
		dataStr = ele.attributeValue("ScanTime").trim();

		if ("".equals(dataStr)) {
			dataStr = CommonUtility.getDateTime();
		}
		
	    /**
	     * EPG信息是否从数据库取得 0:不从数据库取得数据 1:从数据库取得
	     */
		if(sysVO.getIsEPGFromDataBase() == 0) {
			String data = CommonUtility.getDateHourPath(dataStr);
			
			filename = CommonUtility.getEPGFilePath(sysVO.getEPGInfoFilePath()+ "/" + data);
				    
			log.info("从 " + filename + " 读取文件");
			//docreturn = readXmlFromFile(filename);
			File readFilePath = new File(filename);
			
			try {
				EPGXMLStr = CommonUtility.readStringFormFile(readFilePath);
			} catch (CommonException e) {
			}
			
	        if (readFilePath.length() < 1024 * 1024 || sysVO.getIsEPGZip() == 0 ) {
	        	// 
	        	EPGXMLStr = utilXML.replaceXMLMsgHeader(EPGXMLStr, bsData);
	            StringBuffer newEPGStr = new StringBuffer();
	            
	            int start = EPGXMLStr.indexOf("<Return");
	            
	            String body = EPGXMLStr.substring(start);
	            newEPGStr.append(EPGXMLStr.substring(0, start));
	            newEPGStr.append("\r\n<Return Type=\"EPGQuery\" Value=\"0\" Desc=\"成功\" Redirect=\"\"/>\r\n");
	            newEPGStr.append(body);
	            
	            EPGXMLStr = newEPGStr.toString();
	        }
			
		} else {
			try {
				// Get EPG Infomation Form Database By ScanTime
				epgQueryList = getEPGInfoByScanTime(dataStr);
			} catch (Exception ex) {
				log.error("Get EPGInfoFromDB Error " + ex.getMessage());
			}
			
			// 转换EPG信息为XML文件
			EPGXMLStr = epgQueryParse.getEPGInfoXMLByList(this.bsData, epgQueryList, dataStr);
		}
		

		
		String desPath = sysVO.getTomcatHome() + "/webapps/PSI/";

		String dataFlod = CommonUtility.mkDateTimeFold(desPath, dataStr); //创建文件夹

		String desXMLPath = desPath + "/" + dataFlod +"/EPG.xml";
		
		// 保存EPG信息到XML文件
		CommonUtility.WriteFile(EPGXMLStr, desXMLPath);
			
        try {
            long fileSize = EPGXMLStr.length(); // M
            
            log.info("EPG 文件大小: " + fileSize);
            
            if (fileSize > 1024 * 1024 && sysVO.getIsEPGZip() != 0 ) {

            	// File Size > 1M compress to zip
            	String desZIPPath = desPath + "/" + dataFlod +"/EPG.zip";
    			
    			FileOutputStream out = null;
    			ZipOutputStream zipOut = null;
    			
				try {
					log.info("开始压缩EPG为ZIP: "  + desZIPPath);
					out = new FileOutputStream(desZIPPath);
					
					zipOut = new ZipOutputStream(out);
					
					ZipEntry entry = new ZipEntry("epg.xml");
					zipOut.putNextEntry(entry);
					zipOut.write(EPGXMLStr.getBytes());
					log.info("结束压缩EPG为ZIP: "  + desZIPPath);
				} catch (Exception e) {
					log.error("EPG 信息打ZIP包出错：" + e.getMessage());
				}  finally {
					if (zipOut != null) {
						zipOut.close();
					}
					if (out != null) {
						out.close();
					}
				}
				
				EPGQueryParse EPGQuery = new EPGQueryParse();
				
				String redirect = "http://" + sysVO.getLocalRedirectIp() + ":"
				+ sysVO.getTomcatPort() + "/PSI/" + dataFlod + "/" + "EPG.zip";
				
				EPGXMLStr = EPGQuery.getEPGReturnXML(bsData, redirect, 0);
            }
            
        } catch (Exception e) {
        	log.error("EPG读取文件失败: " + filename);
        	EPGXMLStr = utilXML.getReturnXML(bsData, 1);
        }

        try {
        	if(EPGXMLStr.equals("")) {
        		EPGXMLStr = utilXML.getReturnXML(bsData, 1);
        	}
            utilXML.SendUpXML(EPGXMLStr, bsData);
        } catch (CommonException e) {
            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
        }
	}
	
	/**
	 * 更新入库EPG信息表
	 * @param 需要更新的XML数据
	 * @throws DaoException 
	 */
	public static void upEPGTable(List<EPGQueryVo> EPGQueryVoList) throws DaoException {

		Statement statement = null;
		Connection conn = null;
		log.info("EPG 信息开始入库"); 
		try {
			conn = DaoSupport.getJDBCConnection();
			conn.setAutoCommit(false);
			for (int i = 0; i < EPGQueryVoList.size(); i++) {

				StringBuffer strBuff = new StringBuffer();
				EPGQueryVo vo = EPGQueryVoList.get(i);
				// insert into epginfo(ScanTime, Freq, ProgramID, Program,
				// ProgramType, StartTime, ProgramLen, State, Encryption,
				// Lastdatetime)
				// values ('2000-01-01 08:54:55', 482000, 101, 'Hello', '娱乐',
				// '2002-09-01 10:00:00', '120', '播放', 0, '2000-01-01 08:54:55')
				strBuff.append("insert into epginfo(ScanTime, Freq, ProgramID, Program, ProgramType, StartTime, ProgramLen, State, Encryption, Lastdatetime) ");
				strBuff.append(" values(");
				strBuff.append("'" + vo.getScanTime() + "', ");
				strBuff.append(vo.getFreq() + ", ");
				strBuff.append(vo.getProgramID() + ", ");
				strBuff.append("'" + vo.getProgram() + "', ");
				strBuff.append("'" + vo.getProgramType() + "', ");
				strBuff.append("'" + vo.getStartTime() + "', ");
				strBuff.append("'" + vo.getProgramLen() + "', ");
				strBuff.append("'" + vo.getState() + "', ");
				strBuff.append(vo.getEncryption() + ", ");
				strBuff.append("'" + CommonUtility.getDateTime() + "')");

				try {
					statement = conn.createStatement();

					statement.executeUpdate(strBuff.toString());

				} catch (Exception e) {
					log.error("EPG信息更新数据库错误: " + e.getMessage());
					log.error("EPG信息更新数据库错误 SQL：\n" + strBuff.toString());
				} finally {
					DaoSupport.close(statement);
				}
				if(i % 5000 == 0) {
					log.info("EPG 信息已经入库: " + i + " 条数据");
				}
			}
			 conn.commit();
		} catch (Exception e) {
			log.error("EPG信息更新数据库错误: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			DaoSupport.close(conn);
		}
		log.info("EPG信息更新数据库成功! 总共入库: " + EPGQueryVoList.size() + " 条数据");
	}
	
	/**
	 * 数据库取得EPG信息, 默认取得3天的数据
	 * @param EPG时间
	 * @throws DaoException 
	 */
	public static List<EPGQueryVo> getEPGInfoByScanTime(String scanTime) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		StringBuffer strBuff = new StringBuffer();
		
		// 取得三天的EPG信息
		String newDate = CommonUtility.getDayBefOrAftNMonth(scanTime, 3);
		
		List<EPGQueryVo> EPGQueryVoList = new ArrayList();
		// SELECT * FROM epginfo where StartTime >= '2002-09-01 10:00:00' and StartTime <= '2002-09-01 10:00:00'  order by StartTime
		strBuff.append("SELECT * FROM epginfo where StartTime >= ");
		strBuff.append(" '" + scanTime + "' and StartTime <= '" );
		strBuff.append(newDate + "'" );
		strBuff.append(" order by StartTime ");
		
		ResultSet rs = null;
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			while(rs.next()){
				EPGQueryVo vo = new EPGQueryVo();
				
				vo.setScanTime(rs.getString("ScanTime"));
				vo.setFreq(rs.getString("Freq"));
				vo.setProgramID(rs.getString("ProgramID"));
				vo.setProgram(rs.getString("Program"));
				vo.setProgramType(rs.getString("ProgramType"));
				vo.setStartTime(rs.getString("StartTime"));
				vo.setProgramLen(rs.getString("ProgramLen"));
				vo.setState(rs.getString("State"));
				vo.setEncryption(rs.getString("Encryption"));
				//TODO  添加垃圾率判断 
				EPGQueryVoList.add(vo);
			}
		} catch (Exception e) {
			log.error("数据库取得EPG信息错误: " + e.getMessage());
			log.error("数据库取得EPG信息错误 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		log.info("数据库取得EPG信息成功!");
		return EPGQueryVoList;
	}
    
}
