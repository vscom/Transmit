/**
 * controlAlarm (javaת��)
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
 *    ����һ��string ���͵Ĳ��� ͬʱ�ж�String�е��ֶΰѸ��ַ�ת����xml��
 * ��ȡxml���ƶ��ֶ������һ���ļ����֣����ݸ����ֵ��ƶ�λ�ö�ȡ�ļ���
 * ���Ѹ��ļ���String��ʽ���ظ������ߡ�
 * @version  V1.0
 * @author Bian Jiang
 * 
 * content ��epg mhp xml�ļ���������
 */

public class ReadEPGandMHP {

    static Logger log = Logger.getLogger(ReadEPGandMHP.class.getSimpleName());
    
    MemCoreData coreData = MemCoreData.getInstance();
    
	public void downXML(String epgstr, MSGHeadVO bsData) throws CommonException {
		log.info("��ȡTable��Ϣ");
		
        SysInfoVO sysVO = coreData.getSysVO();
        UtilXML utilXML = new UtilXML();
		// ��XML�ļ���ȡ�õ�ʱ����Ϣ
		String dataStr = null;
		
		// ��ǰ��ʱ��Ŀ¼��
		String data = null;
		
		// ȡ��ǰһ��Сʱ��ʱ��
		
		Document document = null;
		String filename = null;
		Element root = null;
		int isError = 0;
		
		document = StringToXML(epgstr);

		root = document.getRootElement();
		Element ele = null;

		log.info("��ȡTable��Ϣ");
		// PSI��Ϣ
		ele = root.element("table");
		dataStr = ele.attributeValue("QueryTime").trim();
		
		if ("".equals(dataStr)) {
			dataStr = CommonUtility.getDateTime();
		}
		
		// Ƶ����Ϣ
		String freqInfo = getFreqInfo(document);
		
		data = CommonUtility.getDateHour(dataStr);
		
		filename = CommonUtility.getTableFilePath(sysVO.getPSIInfoFilePath()+ "/" + CommonUtility.getDateHourPath(dataStr));
		
		File readFilePath = new File(filename);

		log.info("�� " + filename + " Ŀ¼��ȡTable�ļ���Ϣ");
		
		// ���ص�ǰ����Ƶ����Ϣ
		File file = new File(filename);
		
		String desPath = sysVO.getTomcatHome() + "/webapps/PSI/";

		CommonUtility.CreateFolder(desPath); //�����ļ���
		CommonUtility.CreateFolder(desPath + data + "/"); //�����ļ���

		desPath = desPath + data + "/" + "Table.zip";
		
		String sendString = "";
		
		if(freqInfo.equals("") || freqInfo.toUpperCase().equals("ALL")) {
			log.info("ȡ������Ƶ�����Ϣ");
			
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
							log.info("��ȡTable�ļ���" + ZipEntryName);
							try {
					            fileHeaderDoc = readFromFile(fileDir[i]);	
							} catch (CommonException ce) {
								log.error("�ļ���ȡʧ�ܣ�" + fileListStr[i]);
								if (i != fileDir.length - 1) {
									continue;
								} else if(zipFlg){
									break;
								}
							}

				            // ȡ���ļ���--Ƶ����Ϣ
				            String freqStr = ZipEntryName.substring(0, ZipEntryName.indexOf(".xml"));
				            // �޸�ͷ���ļ�
				            utilXML.AmendXML(fileHeaderDoc, bsData, freqStr, "");
				            
							ZipEntry entry = new ZipEntry(ZipEntryName);
							zipOut.putNextEntry(entry);
							zipOut.write(fileHeaderDoc.asXML().getBytes());
							zipFlg = true;
							log.info("zipѹ������");
						}
						
						if(fileDir.length == 0) {
							isError = 1;
						}
					} catch (CommonException ex) {
						log.error("Table�ļ���ȡʧ�ܣ�" + ex.getMessage());
						throw new CommonException("û����Ӧ�ı��ļ�(Table)");
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
				log.error("�Ҳ����ļ���" + e.getMessage());
				throw new CommonException("û���ҵ���Ӧ����");
			} catch (IOException ex) {
				log.error("I/O����");
				throw new CommonException("I/O����" + ex.getMessage());
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
			// ��ѯָ��Ƶ��
			log.info("ֻȡ�� " + freqInfo + " Ƶ�����Ϣ");
			
			filename += "\\" + freqInfo + ".xml";
			
			log.info("Freq FilePath: " + filename);
		
			readFilePath = new File(filename);
			
            long fileSize = readFilePath.length(); // M
            
            log.info("Table �ļ���С: " + fileSize);
            
            String fileStr = CommonUtility.readStringFormFile(readFilePath);
            
            if (fileSize < 1024 * 1024) {
            	// 
            	sendString = utilXML.replaceXMLMsgHeader(fileStr, bsData);
            	
	            StringBuffer newEPGStr = new StringBuffer();
	            
	            int start = sendString.indexOf("<Return");
	            
	            String body = sendString.substring(start);
	            newEPGStr.append(sendString.substring(0, start));
	            newEPGStr.append("\r\n<Return Type=\"table\" Value=\"0\" Desc=\"�ɹ�\" Redirect=\"\"/>\r\n");
	            newEPGStr.append(body);
	            
	            sendString = newEPGStr.toString();
            } else {

            	// File Size > 1M compress to zip
    			desPath = sysVO.getTomcatHome() + "/webapps/PSI/";

    			data = CommonUtility.getDateHour(dataStr);
    			CommonUtility.CreateFolder(desPath); //�����ļ���
    			CommonUtility.CreateFolder(desPath + data + "/"); //�����ļ���

    			desPath = desPath + data + "/" + "Table.zip";
    			
    			FileOutputStream out = null;
    			ZipOutputStream zipOut = null;
    			
				try {
					log.info("��ʼѹ��TableΪZIP: "  + desPath);
					out = new FileOutputStream(desPath);
					
					zipOut = new ZipOutputStream(out);
					
					ZipEntry entry = new ZipEntry(freqInfo + ".xml");
					zipOut.putNextEntry(entry);
					zipOut.write(fileStr.getBytes());
					log.info("����ѹ��TableΪZIP: "  + desPath);
				} catch (Exception e) {
					log.error("Table ��Ϣ��ZIP������" + e.getMessage());
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
		
		log.info("��ȡTable��Ϣ����");
		
        try {
        	if(sendString.equals("")) {
        		sendString = getReturnXML(bsData, 1, "", freqInfo);
        	}
            utilXML.SendUpXML(sendString, bsData);
        } catch (CommonException e) {
            log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
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
            xml += "<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Redirect=\"" + Redirec + "\" Freq = \"" + Freq + "\" Desc=\"�ɹ�\" />";
        }else if(1==value){
            xml += "<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\" />";
        }
        xml += "</Msg>";
        return xml;
    }
    
	/**
	 *  ����xml��ʽ��String
	 *  ���� Document
	 * @param str
	 * @return Document
	 */
	public Document StringToXML(String str) throws CommonException{
		try {
			Document document = DocumentHelper.parseText(str);
			return document;
		} catch (DocumentException de) {
			throw new CommonException("�ύ��xml��ʽ����" + de.getMessage());
		}
	}
	/**
	 * ���� Document
	 * ���� ����xml��ʽ��String
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
			throw new CommonException("document����" + e.getMessage());
		}
	}

	/**
	 * ���� String һ������xml��ʽ���ļ��ľ���·��
	 * ���� Documnet
	 * @param fileName
	 * @return Documnet
	 */
	public Document readXmlFromFile(String fileName) throws CommonException {
		try {
			log.info("��ȡ�ļ���" + fileName);
			FileReader fileReaderf = new FileReader(fileName);
			SAXReader sReader = new SAXReader();
			sReader.setEncoding("GB2312");
			Document document = sReader.read(fileReaderf);
			log.info("�ļ���ȡ����");
			return document;
		} catch (FileNotFoundException fe) {
		    throw new CommonException("û����Ӧ�����ļ���");
		} catch (DocumentException de) {
		    throw new CommonException("��ȡ�ļ����� " + de.getMessage());
		}
	}
	
	/**
	 * ���� String һ������xml��ʽ���ļ��ľ���·��
	 * ���� Documnet
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
			log.error("�ļ���ȡʧ�ܣ�" + fe.getMessage());
		    throw new CommonException("û���ҵ��ļ� " + fe.getMessage());
		} catch (DocumentException de) {
			log.error("��ȡ�ļ�ʧ�ܣ�" + de.getMessage());
		    throw new CommonException("��ȡ�ļ����� " + de.getMessage());
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
	 * ȡ��Ƶ����Ϣ
	 * @param doc
	 * @return String Ƶ��
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
