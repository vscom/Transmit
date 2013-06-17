/**
 * controlAlarm (java转发)
 * 
 * ReadEPGandMHP.java    2007.7.25
 * 
 * Copyright 2007 Dautoit. All Rights Reserved.
 * 
 */

package com.bvcom.transmit.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.si.EPGQueryParse;
import com.bvcom.transmit.parse.si.TableParse;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;

/**
 *    接收一个string 类型的参数 同时判断String中的字段把给字符转化成xml。
 * 提取xml的制定字段来组成一个文件名字，根据改名字到制定位置读取文件。
 * 并把给文件以String形式返回给调用者。
 * @version  V1.0
 * @author Bian Jiang
 * 
 * content 对epg mhp xml文件操作的类
 */

public class ReadEPGandMHP {

    static Logger log = Logger.getLogger(ReadEPGandMHP.class.getSimpleName());
    
    MemCoreData coreData = MemCoreData.getInstance();
    
	public void downXML(String epgstr, MSGHeadVO bsData) throws CommonException {
		log.info("读取Table信息");
		
        SysInfoVO sysVO = coreData.getSysVO();
        UtilXML utilXML = new UtilXML();
		// 从XML文件中取得的时间信息
		String dataStr = null;
		
		// 当前的时间目录名
		String data = null;
		
		// 取得前一个小时的时间
		
		Document document = null;
		String filename = null;
		Element root = null;
		int isError = 0;
		
		document = StringToXML(epgstr);

		root = document.getRootElement();
		Element ele = null;

		log.info("读取Table信息");
		// PSI信息
		ele = root.element("table");
		dataStr = ele.attributeValue("QueryTime").trim();
		
		if ("".equals(dataStr)) {
			dataStr = CommonUtility.getDateTime();
		}
		
		// 频点信息
		String freqInfo = getFreqInfo(document);
		
		data = CommonUtility.getDateHour(dataStr);
		
		filename = CommonUtility.getTableFilePath(sysVO.getPSIInfoFilePath()+ "/" + CommonUtility.getDateHourPath(dataStr));
		
		File readFilePath = new File(filename);

		log.info("从 " + filename + " 目录读取Table文件信息");
		
		// 返回当前所有频点信息
		File file = new File(filename);
		
		String desPath = sysVO.getTomcatHome() + "/webapps/PSI/";

		CommonUtility.CreateFolder(desPath); //创建文件夹
		CommonUtility.CreateFolder(desPath + data + "/"); //创建文件夹

		desPath = desPath + data + "/" + "Table.zip";
		
		String sendString = "";
		
		if(freqInfo.equals("") || freqInfo.toUpperCase().equals("ALL")) {
			log.info("取得所有频点表信息");
			
			try {
				FileOutputStream out = new FileOutputStream(desPath);
				
				if (file.isDirectory()) {
					File[] fileDir = file.listFiles();

					ZipOutputStream zipOut = new ZipOutputStream(out);
					String[] fileListStr = file.list();
					
					try {
						boolean zipFlg = false;
						
						for (int i=0; i < fileDir.length; i++) {
							String ZipEntryName = fileListStr[i];
							Document fileHeaderDoc = null;
							log.info("读取Table文件：" + ZipEntryName);
							try {
					            fileHeaderDoc = readFromFile(fileDir[i]);	
							} catch (CommonException ce) {
								log.error("文件读取失败：" + fileListStr[i]);
								if (i != fileDir.length - 1) {
									continue;
								} else if(zipFlg){
									break;
								}
							}

				            // 取得文件名--频点信息
				            String freqStr = ZipEntryName.substring(0, ZipEntryName.indexOf(".xml"));
				            // 修改头部文件
				            utilXML.AmendXML(fileHeaderDoc, bsData, freqStr, "");
				            
							ZipEntry entry = new ZipEntry(ZipEntryName);
							zipOut.putNextEntry(entry);
							zipOut.write(fileHeaderDoc.asXML().getBytes());
							zipFlg = true;
							log.info("zip压缩结束");
						}
						
						if(fileDir.length == 0) {
							isError = 1;
						}
					} catch (CommonException ex) {
						log.error("Table文件读取失败：" + ex.getMessage());
						throw new CommonException("没有相应的表文件(Table)");
					} finally {
						if (zipOut != null) {
							zipOut.close();
						}
						if (out != null) {
							out.close();
						}
					}

					zipOut.close();
					out.close();
				} else {
					isError = 1;
				}

			} catch (FileNotFoundException e) {
				log.error("找不到文件：" + e.getMessage());
				throw new CommonException("没有找到相应数据");
			} catch (IOException ex) {
				log.error("I/O出错");
				throw new CommonException("I/O出错：" + ex.getMessage());
			}
			
			String redirect = "http://" + sysVO.getLocalRedirectIp() + ":"
				+ sysVO.getTomcatPort() + "/PSI/" + data + "/" + "Table.zip";
			
			if(isError == 1) {
				sendString = getReturnXML(bsData, 1, "", freqInfo);
			} else {
				sendString = getReturnXML(bsData, 0, redirect, freqInfo);	
			}
	        
			//xmlUtil.replaceXMLMsgHeader(document, bsData);
		} else {
			// 查询指定频点
			log.info("只取得 " + freqInfo + " 频点表信息");
			
			filename += "\\" + freqInfo + ".xml";
			
			log.info("Freq FilePath: " + filename);
		
			readFilePath = new File(filename);
			
            long fileSize = readFilePath.length(); // M
            
            log.info("Table 文件大小: " + fileSize);
            
            String fileStr = CommonUtility.readStringFormFile(readFilePath);
            
            if (fileSize < 1024 * 1024) {
            	// 
            	sendString = utilXML.replaceXMLMsgHeader(fileStr, bsData);
            	
	            StringBuffer newEPGStr = new StringBuffer();
	            
	            int start = sendString.indexOf("<Return");
	            
	            String body = sendString.substring(start);
	            newEPGStr.append(sendString.substring(0, start));
	            newEPGStr.append("\r\n<Return Type=\"table\" Value=\"0\" Desc=\"成功\" Redirect=\"\"/>\r\n");
	            newEPGStr.append(body);
	            
	            sendString = newEPGStr.toString();
            } else {

            	// File Size > 1M compress to zip
    			desPath = sysVO.getTomcatHome() + "/webapps/PSI/";

    			data = CommonUtility.getDateHour(dataStr);
    			CommonUtility.CreateFolder(desPath); //创建文件夹
    			CommonUtility.CreateFolder(desPath + data + "/"); //创建文件夹

    			desPath = desPath + data + "/" + "Table.zip";
    			
    			FileOutputStream out = null;
    			ZipOutputStream zipOut = null;
    			
				try {
					log.info("开始压缩Table为ZIP: "  + desPath);
					out = new FileOutputStream(desPath);
					
					zipOut = new ZipOutputStream(out);
					
					ZipEntry entry = new ZipEntry(freqInfo + ".xml");
					zipOut.putNextEntry(entry);
					zipOut.write(fileStr.getBytes());
					log.info("结束压缩Table为ZIP: "  + desPath);
				} catch (Exception e) {
					log.error("Table 信息打ZIP包出错：" + e.getMessage());
				}  finally {
					if (zipOut != null) {
						try {
							zipOut.close();
						} catch (IOException e) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
						}
					}
				}
				
				TableParse EPGQuery = new TableParse();
				
				sendString = EPGQuery.getTableReturnXML(bsData, desPath, 0);
            }
		}
		
		log.info("读取Table信息结束");
		
        try {
        	if(sendString.equals("")) {
        		sendString = getReturnXML(bsData, 1, "", freqInfo);
        	}
            utilXML.SendUpXML(sendString, bsData);
        } catch (CommonException e) {
            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
        }
		
        sysVO = null;

        utilXML = null;
	}

    public String getReturnXML(MSGHeadVO head, int value, String Redirec, String Freq) {
        
        String xml = null;
        xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
        xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
                + head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
                + "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
        xml += "<table Freq=\"\" QueryTime=\"" + CommonUtility.getDateTime() + "\" />";
        if(0==value){
            xml += "<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Redirect=\"" + Redirec + "\" Freq = \"" + Freq + "\" Desc=\"成功\" />";
        }else if(1==value){
            xml += "<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\" />";
        }
        xml += "</Msg>";
        return xml;
    }
    
	/**
	 *  接收xml格式的String
	 *  返回 Document
	 * @param str
	 * @return Document
	 */
	public Document StringToXML(String str) throws CommonException{
		try {
			Document document = DocumentHelper.parseText(str);
			return document;
		} catch (DocumentException de) {
			throw new CommonException("提交的xml格式错误" + de.getMessage());
		}
	}
	/**
	 * 接收 Document
	 * 返回 符合xml格式的String
	 * 
	 * @param document
	 * @return String
	 */
	public static String XMLToString(Document document) throws CommonException {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			StringWriter stringWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(stringWriter, format);
			String XMLData = null;

			writer.write(document);
			XMLData = stringWriter.toString();
			writer.flush();
			writer.close();
			return XMLData;
		} catch (IOException e) {
			throw new CommonException("document错误" + e.getMessage());
		}
	}

	/**
	 * 接收 String 一个符合xml格式的文件的绝对路径
	 * 返回 Documnet
	 * @param fileName
	 * @return Documnet
	 */
	public Document readXmlFromFile(String fileName) throws CommonException {
		try {
			log.info("读取文件：" + fileName);
			FileReader fileReaderf = new FileReader(fileName);
			SAXReader sReader = new SAXReader();
			sReader.setEncoding("GB2312");
			Document document = sReader.read(fileReaderf);
			log.info("文件读取结束");
			return document;
		} catch (FileNotFoundException fe) {
		    throw new CommonException("没有相应数据文件。");
		} catch (DocumentException de) {
		    throw new CommonException("读取文件出错 " + de.getMessage());
		}
	}
	
	/**
	 * 接收 String 一个符合xml格式的文件的绝对路径
	 * 返回 Documnet
	 * @param fileName
	 * @return Documnet
	 */
	public Document readFromFile(File file) throws CommonException {
		FileReader fileReaderf = null;
		try {
			fileReaderf = new FileReader(file);
			SAXReader sReader = new SAXReader();
			sReader.setEncoding("GB2312");
			Document document = sReader.read(fileReaderf);
			return document;
		} catch (FileNotFoundException fe) {
			log.error("文件读取失败：" + fe.getMessage());
		    throw new CommonException("没有找到文件 " + fe.getMessage());
		} catch (DocumentException de) {
			log.error("读取文件失败：" + de.getMessage());
		    throw new CommonException("读取文件出错 " + de.getMessage());
		} finally {
			if (fileReaderf != null) {
				try {
					fileReaderf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 取得频点信息
	 * @param doc
	 * @return String 频点
	 */
	public String getFreqInfo(Document doc) {
		String freqStr = "";
		try {
			Element root = doc.getRootElement();
			Element tableElem = root.element("table");
			Attribute tableType = tableElem.attribute("Freq");
			freqStr = tableType.getValue();
		} catch (Exception ex) {
			freqStr = "";
		}

		return freqStr;
	}

}
