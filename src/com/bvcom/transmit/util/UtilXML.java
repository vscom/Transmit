package com.bvcom.transmit.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.stream.FileImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.handle.alarm.ReplyAlarmErrorTableHandle;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;



public class UtilXML {

    private static Logger log = Logger.getLogger(UtilXML.class.getSimpleName());
    
    private int lastExceptionError;

    public int getLastExceptionError() {
        return lastExceptionError;
    }

    public void setLastExceptionError(int error) {
        lastExceptionError = error;
    }
    
    /**
     * Parse String to a XML file
     *
     */
    public Document StringToXML(String str) throws CommonException{

        try {
        	if(str == null || str.equals("")) {
        		return null;
        	}
            Document document = DocumentHelper.parseText(str.trim());
            return document;
        } catch (DocumentException de) {
//            PersistentData pData = new PersistentData();
//            // ȡ�ô���XML��Ϣ���Ŀ¼
//            String errorFilePath = pData.getErrorFilePath();
//            // ��������XML��Ϣ���Ŀ¼
//            CommonUtility.CreateFolder(errorFilePath);
//            // ������Ϣ�����ļ�
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss"); 
//            Date desData = new Date();
//            String desDataStr = formatter.format(desData);
//            String fileName =  errorFilePath + "\\" + desDataStr + "_Error.xml";
//            
//            log.info("�����XML�ļ����·����" + fileName);
//            
//            CommonUtility.WriteFile(str, fileName);
            
            log.error("StringToXML:" + de.getMessage());
            throw new CommonException(de.getMessage());
        }
    }

    public String XMLToString(Document document) throws CommonException {

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("GB2312");
            StringWriter strWriter = new StringWriter();
            XMLWriter writer = new XMLWriter(strWriter, format);
            String XMLData = null;

            writer.write(document);
            XMLData = strWriter.toString();
            writer.flush();
            writer.close();

            return XMLData;
        } catch (IOException ioe) {
            log.error("XMLToString:" + ioe.getMessage());
            throw new CommonException(ioe.getMessage());
        }
    }
    /**
     * Read a XML file from a file
     *
     */
    public Document ReadFromFile(String filename) {

        try {
            FileReader freader = null;
            freader = new FileReader(filename);

            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding("GB2312");
            Document document = saxReader.read(freader);

            return document;
        } catch (FileNotFoundException fnfe) {
            log.error("ReadFromFile:" + fnfe.getMessage());
            log.error(" --> û���ҵ��ļ��������Ƿ�����ļ� " + filename);
            CommonUtility.printErrorTrace(fnfe);
            setLastExceptionError(CommonUtility.FileNotFoundExceptionError);
            return null;
        } catch (DocumentException de) {
            log.error("ReadFromFile:" + de.getMessage());
            log.error(" --> ��ȡ�ļ����� " + filename + " �����ļ�����");
            CommonUtility.printErrorTrace(de);
            setLastExceptionError(CommonUtility.DocumentExceptionError);
            return null;
        }
    }
    /**
     * Read a XML file from a file
     *
     */
    public String ReadFile(String filename) {
    	String str="";
        try {
            //DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(new File(filename))));
//			InputStream in=new FileInputStream(new File(filename));
        	DocumentBuilderFactory dbf   =   DocumentBuilderFactory.newInstance(); 
        	DocumentBuilder   db   =   null;   
        	Document   doc   =   null;
        	db   =   dbf.newDocumentBuilder();   
            org.dom4j.io.DOMReader   xmlReader   =   new   org.dom4j.io.DOMReader();   
            doc=xmlReader.read(db.parse(new File(filename))); 
        	str=doc.asXML();
        	
            return str;
        } catch (Exception fnfe) {
        	fnfe.printStackTrace();
            return null;
        } 
    } 
    /**
     * ���·���Xml��������ȡͷ����Ϣ
     * @author �� ��
     * @param document
     * @param bsData
     * @param pData
     * @return boolean �ɹ���true   ʧ�ܣ�false
     */
    public boolean getInfoFromDownXml(Document reqDocument, MSGHeadVO bsData) throws CommonException {
        
        boolean retFlg = false;
        Element root = reqDocument.getRootElement();
        
        if (root.attributeValue("Type").compareTo("MonDown") == 0 
                || root.attributeValue("Type").compareTo("AD988MonDown") == 0
                || root.attributeValue("Type").compareTo("AD988MonUp") == 0
                || root.attributeValue("Type").compareTo("MonUp") == 0) {
            
            if (root.attributeValue("Type").compareTo("AD988MonUp") == 0 ||
                    root.attributeValue("Type").compareTo("MonUp") == 0) {
                bsData.setSystemType("MonUp");
            } else {
                bsData.setSystemType("MonDown");
            }
            
            Attribute attr = null;

            for (Iterator i = root.attributeIterator(); i.hasNext();) {
                attr = (Attribute) i.next();
                if (attr.getName().compareTo("MsgID") == 0) {
                    bsData.setCenterMsgID(attr.getText());
                } else if (attr.getName().compareTo("SrcCode") == 0) {
                    bsData.setSrcCode(attr.getValue());
                } else if (attr.getName().compareTo("DstCode") == 0) {
                    bsData.setDstCode(attr.getValue());
                } else if (attr.getName().compareTo("SrcURL") == 0) {
                        bsData.setSrcURL(attr.getValue());
                } else if (attr.getName().compareTo("Priority") == 0) {
                    // �������ȼ�
                    bsData.setPriority(attr.getValue());
                } else if (attr.getName().compareTo("Version") == 0) {
                    // �������ȼ�
//                	if(attr.getValue().equals("2.5"))
//                		bsData.setVersion("2.3");
//                	else
                		bsData.setVersion(attr.getValue());
                } else if (attr.getName().compareTo("DateTime") == 0) {
                    // ʱ��
                    bsData.setDateTime(attr.getValue());
                }
            }
            try {
            // Remove SrcURL
            root.remove(root.attribute("SrcURL"));
            } catch (Exception ex) {
                //log.error("XML SrcURL: " + ex.getMessage());
            }
            
            Element ele = null;
            
            for (Iterator i = root.elementIterator(); i.hasNext();) {
                ele = (Element) i.next();
                bsData.setStatusQueryType(ele.getName());
                //V2.5���ڴ���ICInfoChannelEncryptQuery����Ϣ
                try{
                	String str=ele.attributeValue("Type");
                	bsData.setReturn_Type(str);
                }
                catch(Exception ex){
                	
                }
                
                break;
            }
            
            retFlg = true;
        } else {
            log.info("getInfoFromDownXml �ɼ�ָ�����Ҳ���MonDown�ֶ� �쳣");
            retFlg = false;
            throw new CommonException("�ɼ�ָ�����Ҳ���MonDown�ֶ� �쳣");
        }
        
        return retFlg;
    }

    /**
     * �·���SMG, IPM, TSC�Ĵ��룬�����Ҫ�ȴ����ء�
     * @param sendString
     * @param sendURL
     * @param readTimeOut
     * @param bsData
     * @throws CommonException
     */
    public void SendDownNoneReturn(String sendString, String sendURL, int readTimeOut, MSGHeadVO bsData) throws CommonException {
        
        URL urlCenter = null;
        URLConnection conn = null;
        if(sendString == null || sendString.equals("")) {
        	return;
        }
//        BufferedReader rd = null;
        try {
//            urlCenter = new URL(sendURL);
//            conn = urlCenter.openConnection();
//            conn.setReadTimeout(readTimeOut);
//            conn.setDoOutput(true);

            urlCenter = new URL(sendURL);
            conn = urlCenter.openConnection();
            
            conn.setRequestProperty("Content-Type", "text/xml");

            // ����ǿ��ת��
            HttpURLConnection connection = (HttpURLConnection) conn;
            // ���������ʽ"POST"
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(readTimeOut);
            connection.setRequestMethod("POST");

            
            log.info(bsData.getStatusQueryType() + " �·���ַ:" + sendURL);
            log.info(bsData.getStatusQueryType() + " ���ֶ�ѡ̨���·����ݣ�\n" + sendString);
            
//            // �ϱ���Ϣ�����ļ�
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
//            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd"); 
//            Date desData = new Date();
//            // Ŀ¼
//            String desDataStr = formatDate.format(desData);
//            // �ļ���
//            String desDataTimeStr = formatter.format(desData);
//            
//            // ȡ���ϱ���Ϣ���Ŀ¼
//            String sendFilePath = "D:\\Loging\\SendDownFile";
//            // �����ϱ���Ϣ���Ŀ¼
//            CommonUtility.CreateFolder(sendFilePath);
//            
//            String fileFlod = sendFilePath + "\\" + desDataStr;
//            // ����Ŀ¼
//            CommonUtility.CreateFolder(fileFlod);
//            
//            String fileName = fileFlod + "\\" + desDataTimeStr + "_" + bsData.getStatusQueryType()+ "_Send.xml";
//            log.info("Send XML�ļ����·����" + fileName);
//            
//            // ���������ļ�
//            CommonUtility.WriteFile(sendString, fileName);
            
            OutputStream out = connection.getOutputStream();
            OutputStreamWriter wout = new OutputStreamWriter(out, "UTF-8");
            //OutputStreamWriter wout = new OutputStreamWriter(out, "GB2312");
            
            Document requestDoc = StringToXML(sendString.trim());
            
            requestDoc.setXMLEncoding("UTF-8");
            
            // �������ͨ����??����
            wout.write(requestDoc.asXML());
            wout.flush();
            
//            CenterWriter = new OutputStreamWriter(conn
//                    .getOutputStream(), "GB2312");
//            CenterWriter.write(sendString);
//            CenterWriter.flush();
            // �������ļ�
            try {
                InputStream inReader = connection.getInputStream();
                if (inReader != null) {
                    inReader.close();
                }
                
                // Get the response
//                rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
//                String line;
//                log.info(" ------------- �ֶ�������Ϣ " + sendURL);
//                String recString = "";
//                while ((line = rd.readLine()) != null) {
//                    log.info(line);
//                    recString +=line;
//                }
//                log.info(" ------------- �ֶ�������Ϣ " + sendURL);
//                System.out.println("��������: " + recString);
                //log.info("----XXXXXXXXXXX--- �����������: \n" + utf8Togb2312(recString));
                log.info(bsData.getStatusQueryType() + "�·�XML��Ϣ�ɹ�..." + sendURL);
            } catch (Exception ex) {
                log.error(bsData.getStatusQueryType() + "�·�����: " + ex.getMessage());
                log.error(bsData.getStatusQueryType() + "����URL: " + sendURL);
            } finally {

                if (out != null) {
                	out.close();
                }
                if (wout != null) {
                	wout.close();
                }
            }
            
        } catch (IOException ioe) {
            log.error(bsData.getStatusQueryType() + "�·����� " + sendURL);
            throw new CommonException("������Ϣ��" + ioe.getMessage());
            //CommonUtility.printErrorTrace(ioe);
        }
    }
    
    /**
     * �·���SMG, IPM, TSC�Ĵ��룬�����Ҫ�ȴ����ء�
     * @param sendString
     * @param sendURL
     * @param readTimeOut
     * @param bsData
     * @throws CommonException
     */
    public String SendDownXML(String sendString, String sendURL, int readTimeOut, MSGHeadVO bsData) throws CommonException {
        
        URL urlCenter = null;
        URLConnection conn = null;
        OutputStreamWriter CenterWriter = null;
        BufferedReader rd = null;
        String retString = "";
        Document requestDoc = null;
        try {
//            urlCenter = new URL(sendURL);
//            conn = urlCenter.openConnection();
//            conn.setReadTimeout(readTimeOut);
//            conn.setDoOutput(true);

            urlCenter = new URL(sendURL);
            conn = urlCenter.openConnection();
            
            conn.setRequestProperty("Content-Type", "text/xml");

            // ����ǿ��ת��
            HttpURLConnection connection = (HttpURLConnection) conn;
            // ���������ʽ"POST"
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(readTimeOut);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");

            
            log.info(bsData.getStatusQueryType() + "�·���ַ:" + sendURL);
            log.info(bsData.getStatusQueryType() + "�·�����: \n" + sendString);
            
            OutputStream out = connection.getOutputStream();
            OutputStreamWriter wout = new OutputStreamWriter(out, "UTF-8");
            
            requestDoc = StringToXML(sendString.trim());
            
            requestDoc.setXMLEncoding("UTF-8");
            
            // �������ͨ����??����
            wout.write(requestDoc.asXML());
            wout.flush();
            wout.close();
            
            // �������ļ�
            try {
            	try {
            		
            		rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            	} catch (Exception x) {
            		log.error(bsData.getStatusQueryType() + " Read Input Stream Error");
            		/**���������Ϣ���ļ�*/
            		PrintWriter pw;
        			try {
        				pw = new PrintWriter(new File("d:/Exception.log"));
        				x.printStackTrace(pw);
        				pw.flush();
        				pw.close();
        			} catch (Exception e1) {
        			}
        			/******/
            	}
                String line;
                while ((line = rd.readLine()) != null) {
                    retString += line;
                }
                log.info(bsData.getStatusQueryType() + " �·�XML��Ϣ�ɹ� URL: " + sendURL);

                if (retString != null && !retString.equals("")) {
                    retString = retString.replaceAll(">", ">\r\n");
                    // ת��UTF-8 to GB2312 ͳһXML �ַ����
                    requestDoc = this.StringToXML(retString.trim());
                    
                    requestDoc.setXMLEncoding("GB2312");
                    
                    retString = requestDoc.asXML();
                }
                
                log.info(bsData.getStatusQueryType() + " ----------> ���շ�����Ϣ: \n" + retString);
                
            } catch (Exception ex) {
                
            } finally {
                if (rd != null ) {
                    rd.close();
                }
                if (out != null) {
                	out.close();
                }
                if (CenterWriter != null) {
                    CenterWriter.close();
                }
            }
            


        } catch (IOException ioe) {
            log.error(bsData.getStatusQueryType() + "�·����� " + sendURL);
            throw new CommonException("������Ϣ��" + ioe.getMessage());
            //CommonUtility.printErrorTrace(ioe);
        }
        return retString;
    }
    
    /**
     * �ϱ����������
     * @param sendString
     * @param bsData
     * @return flag �ϱ��Ƿ�ɹ�״̬  
     * @throws CommonException
     */
    public boolean SendUpXML(String sendString, String url) {
        
        URL urlCenter = null;
        URLConnection conn = null;
        BufferedReader rd = null;
        
        if (sendString == null || sendString.equals("")) {
        	log.error("������ϢΪ��");
        	return false;
        }
        //���� �Ƿ�ɹ�״̬
        boolean flag=false;
        OutputStreamWriter wout = null;
        try{
        	//urlCenter = new URL("http://192.168.2.177:8088/Web2.0/servlet/SDVAlarmReceServlet");
        	//urlCenter = new URL("http://localhost:8080/transmit/servlet/AlarmRec");
        	urlCenter = new URL(url);
        	conn = urlCenter.openConnection();
        	
        	conn.setRequestProperty("Content-Type", "text/xml");
        	
        	// ����ǿ��ת��
        	HttpURLConnection connection = (HttpURLConnection) conn;
        	// ���������ʽ"POST"
        	connection.setReadTimeout(5);
        	connection.setDoOutput(true);
        	connection.setDoInput(true);
        	connection.setRequestMethod("POST");
        	
        	Document requestDoc = StringToXML(sendString.trim());
        	
        	requestDoc.setXMLEncoding("GB2312");
        	
        	OutputStream out = connection.getOutputStream();
        	wout = new OutputStreamWriter(out, "GB2312");
        	
        	// �������ͨ����??����
        	wout.write(requestDoc.asXML());
        	wout.flush();
        	wout.close();
        	// �������ļ�
        	try {
        		  BufferedReader br = new BufferedReader(new InputStreamReader(connection
                          .getInputStream()));
                  String line = "";
                  for (line = br.readLine(); line != null; line = br.readLine()) {
                      System.out.println(line);
                  }
				
			} catch (Exception e) {
				// TODO: handle exception
				//e.printStackTrace();
			}
        	if (out != null ) {
        		out.close();
        	}

        	if (rd != null ) {
        		rd.close();
        	}
        	flag=true;
        }catch (Exception e) {
        	log.info("���ʹ�����Ϣ��"+e.getMessage());
        	log.info("���ʹ������ݣ�"+sendString);
        	log.info("���ʹ����ַ��"+url);
		} finally {
        	if (wout != null ) {
        		try {
					wout.close();
				} catch (IOException e) {
				}
        	}
		}
        return flag;
    }
    
    /**
     * �ϱ����������
     * @param sendString
     * @param bsData
     * @return flag ���ӱ����ϱ��Ƿ�ɹ�״̬ Ji Long 2011-06-26 
     * @throws CommonException
     */
    public boolean SendUpXML(String sendString, MSGHeadVO bsData) throws CommonException {
        
        URL urlCenter = null;
        URLConnection conn = null;
        BufferedReader rd = null;
        Document requestDoc = null;
        if (sendString == null || sendString.equals("")) {
        	log.error("������ϢΪ��");
        	return false;
        }
        boolean flag =false;
        // ȡ���ϱ���Ϣ���Ŀ¼
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        
        try {
        	log.info("�ϱ����ı�����ַ:" + bsData.getSrcURL()+"\n");
            urlCenter = new URL(bsData.getSrcURL());
            conn = urlCenter.openConnection();
            
            conn.setRequestProperty("Content-Type", "text/xml");

            // ����ǿ��ת��
            HttpURLConnection connection = (HttpURLConnection) conn;
            // ���������ʽ"POST"
            connection.setReadTimeout(5);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            
            //log.info("�ϱ���ַ:" + bsData.getSrcURL());
            //log.info("��ʼ�Ϸ�����������ݣ�\n" + XMLToString(document));
            
            // �ϱ���Ϣ�����ļ�
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd"); 
            Date desData = new Date();
            // Ŀ¼
            String desDataStr = formatDate.format(desData);
            // �ļ���
            String desDataTimeStr = formatter.format(desData);
            

            
            String sendFilePath = sysVO.getSendFilePath();
            
            if (bsData.getStatusQueryType().equals("ReturnInfo") || bsData.getStatusQueryType().equals("Return")
            		|| bsData.getStatusQueryType().equals("AlarmSearchPSet") || bsData.getStatusQueryType().equals("AlarmSearchFSet")) {
            	sendFilePath = sysVO.getAlarmFilePath();
            }
            
            // �����ϱ���Ϣ���Ŀ¼
            CommonUtility.CreateFolder(sendFilePath);
            
            String fileFlod = sendFilePath + "\\";
            
            String[] dataFold = desDataStr.split("/");
            
            String fileName = "";
            
            if (sysVO.getIsAlarmLogEnable() == 1 || (!bsData.getStatusQueryType().equals("ReturnInfo") && !bsData.getStatusQueryType().equals("Return")
            		&& !bsData.getStatusQueryType().equals("AlarmSearchPSet") && !bsData.getStatusQueryType().equals("AlarmSearchFSet"))) {
                // ����Ŀ¼
                for(int i=0; i<dataFold.length; i++) {
                	fileFlod += dataFold[i] + "/";
                	CommonUtility.CreateFolder(fileFlod);
                }
                
                fileName = fileFlod + "\\" + desDataTimeStr + "_" + bsData.getStatusQueryType()+ "_Send.xml";
            	log.info("Send " + bsData.getStatusQueryType() + " XML�ļ����·����" + fileName);
            	CommonUtility.WriteFile(sendString, fileName);
            	if (bsData.getStatusQueryType().equals("EPGQuery") && sendString.length() >= 1024 * 100) {
            		log.info("���������ϱ���Ϣ: EPGQuery size:" + sendString.length());
            	} else {
            		log.info("���������ϱ���Ϣ: " + sendString);
            	}
            	
            }
            
            OutputStream out = connection.getOutputStream();
            OutputStreamWriter wout = new OutputStreamWriter(out, "GB2312");

            if(bsData.getStatusQueryType().equals("MonitorProgramQuery") || 
            		bsData.getStatusQueryType().equals("StreamRoundInfoQuery") ||
            		bsData.getStatusQueryType().equals("ChangeProgramQuery")) {
            	log.info("Message: " + sendString);
            }
            
            // �������ͨ����??����
            if (!bsData.getStatusQueryType().equals("EPGQuery")) {
	            requestDoc = StringToXML(sendString.trim());
	            requestDoc.setXMLEncoding("GB2312");
	            wout.write(requestDoc.asXML());
            } else {
            	wout.write(sendString);
            }
            
            wout.flush();
            int responseCode = 0;
            // �������ļ�
            try {
            	responseCode = connection.getResponseCode();
                InputStream inReader = connection.getInputStream();
                inReader.close();
                if (!bsData.getStatusQueryType().equals("ReturnInfo") && !bsData.getStatusQueryType().equals("Return")
                		&& !bsData.getStatusQueryType().equals("AlarmSearchPSet") && !bsData.getStatusQueryType().equals("AlarmSearchFSet")) {
                	log.info(bsData.getStatusQueryType() + " Response: " + responseCode + " �ϱ�XML��Ϣ�ɹ�... " + bsData.getSrcURL());
                }
                
            } catch (Exception ex) {
            	
            	if (bsData.getStatusQueryType().equals("ReturnInfo") || bsData.getStatusQueryType().equals("Return")
            			|| bsData.getStatusQueryType().equals("AlarmSearchPSet") || bsData.getStatusQueryType().equals("AlarmSearchFSet")) {
            		
                	if (sysVO.getIsAutoAlarmReply() != 0) {
	            		ReplyAlarmErrorTableHandle replyAlarmErrorTableHandle = new ReplyAlarmErrorTableHandle();
	            		try {
	            			replyAlarmErrorTableHandle.upReplyAlarmErrorTable(requestDoc.asXML(), ex.getMessage());
	            		} catch (Exception e) {
	            			log.error("ReplyAlarmErrorTable�����ϱ������������ʧ��"+ e.getMessage());
	            		}
                	}
            		
            		if (sysVO.getIsAlarmLogEnable() == 0) {
            			return false;
            		}
            	}
            	
            	fileFlod = sysVO.getSendErrorFilePath() + "\\";
            	
                // �����ϱ���Ϣ���Ŀ¼
                CommonUtility.CreateFolder(fileFlod);
                
                dataFold = desDataStr.split("/");
                // ����Ŀ¼
                for(int i=0; i<dataFold.length; i++) {
                	fileFlod += dataFold[i] + "/";
                	CommonUtility.CreateFolder(fileFlod);
                }
                
                fileName = fileFlod + "\\" + desDataTimeStr + "_" + bsData.getStatusQueryType()+ "_Send.xml";
                
                CommonUtility.WriteFile(sendString, fileName);
                
            } finally {
            	if (out != null ) {
            		out.close();
            	}
               	if (wout != null ) {
            		wout.close();
            	}
                if (rd != null ) {
                    rd.close();
                }
            }
            flag =true;
        } catch (IOException ioe) {
        	flag=false;
            log.error(bsData.getStatusQueryType() + " �ϱ����� " + bsData.getSrcURL());
            log.error(bsData.getStatusQueryType() + " �ϱ�������Ϣ��" + ioe.getMessage());
            //���޷���������ʱ��Ҫ������Ϣ BY TQY
        	//if (!bsData.getStatusQueryType().equals("ICInfoQuery")) {
            	if (sysVO.getIsAutoAlarmReply() != 0) {
	        		ReplyAlarmErrorTableHandle replyAlarmErrorTableHandle = new ReplyAlarmErrorTableHandle();
	        		try {
	        			replyAlarmErrorTableHandle.upReplyAlarmErrorTable(sendString, ioe.getMessage());
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        			log.error("ReplyAlarmErrorTable�����ϱ������������ʧ��"+ e.getMessage());
	        		}
            	}
        	//} 
        }
        
        return flag; 
    }
    
    /**
     * ȡ�÷��ص�XML��Ϣ
     * @param head XML���ݶ��� 
     * @param value 0:�ɹ� 1:ʧ��
     * @return XML�ı���Ϣ
     */
    public String getReturnXML(MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
        }
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
    
    /**
     * ȡ�÷�����Ϣ��Value
     * @param retrunStr
     * @return int value 0: �ɹ� 1:ʧ��
     */
    public int getReturnValue(String retrunStr) {
    	
    	int value = 1;
    	
        Document document = null;
        
        if(retrunStr == null || retrunStr.trim().equals("")) {
        	 return value;
        }
        
        try {
            document = this.StringToXML(retrunStr);
        } catch (CommonException e) {
            log.error("���շ�����ϢStringToXML Error: " + e.getMessage());
            return value;
        }
        
		Element root = document.getRootElement();
		
		Element Return = root.element("Return");
		
		String type = Return.attribute("Type").getValue();
		value = Integer.parseInt(Return.attribute("Value").getValue());
		
    	return value;
    }
    

    /**
     * ȡ�÷�������
     * @param retrunStr
     * @return �������� String
     */
    public String getReturnType(String retrunStr) {
    	
    	String value = "";
    	
        Document document = null;
        
        if(retrunStr == null || retrunStr.trim().equals("")) {
        	 return value;
        }
        
        try {
            document = this.StringToXML(retrunStr);
        } catch (CommonException e) {
            log.error("���շ�����ϢStringToXML Error: " + e.getMessage());
            return value;
        }
        
		Element root = document.getRootElement();
		
		Element Return = root.element("Return");
		
		value = Return.attribute("Type").getValue();
		
    	return value;
    }
    
    public String replaceXMLMsgHeader(String xmlInfo, MSGHeadVO bsData) {
    	if (xmlInfo == null || xmlInfo.equals("")) {
    		return null;
    	}
        StringBuffer strBuff = new StringBuffer();
        
        //MemCoreData coreData = MemCoreData.getInstance();
        //SysInfoVO sysVO = coreData.getSysVO();
        
        int start = xmlInfo.indexOf("<Return");
        
        String body = xmlInfo.substring(start);
        
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getDstCode()
                + "\" DstCode=\"" + bsData.getSrcCode() + "\" ReplyID=\"" + bsData.getCenterMsgID() + "\"> \r\n");
        
        strBuff.append(body);
        return strBuff.toString();
    }
    
    public String replaceAlarmXMLMsgHeader(String xmlInfo, MSGHeadVO bsData, int isManualRecord) {
        StringBuffer strBuff = new StringBuffer();
        
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        
        int start = xmlInfo.indexOf("<Return");
        
        String body = xmlInfo.substring(start);
        
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + sysVO.getSrcCode()
                + "\" DstCode=\"" + sysVO.getDstCode() + "\" ReplyID=\"" + ((isManualRecord==1)?bsData.getCenterMsgID():bsData.getReplyID()) + "\"> \r\n");
        
        strBuff.append(body);
        return strBuff.toString();
    }
   /* 
    public String replaceDownXMLMsgHeader(String xmlInfo, MSGHeadVO bsData) {
        StringBuffer strBuff = new StringBuffer();
        
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        
        int start = xmlInfo.indexOf("Priority=");
        
        String body = xmlInfo.substring(start);
        
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"http://10.1.2.111\" ");
        
        strBuff.append(body);
        return strBuff.toString();
    }
    */
    public void replaceXMLMsgHeader(Document headerDoc, MSGHeadVO bsData) {
        Element root = headerDoc.getRootElement();
        
        Attribute attr = null;
        Element ele = null, eleTo = null;
        
        List list = root.attributes();
        
        // get Name Spaces List
        List listUrl = root.declaredNamespaces();
        
        for (int i=0; i < listUrl.size(); i++) {
            Namespace nameSpace = (Namespace)listUrl.get(i);
            // remove name space
            root.remove(nameSpace);
        }
        
        List listRemoveAttr = new ArrayList();
        
        // ����ͷ����Ϣ����
        for (int i=0; i < list.size(); i++) {
            attr = (Attribute)list.get(i);
            listRemoveAttr.add(attr);
        }
        
        // ɾ������ͷ����Ϣ������
        for (int i=0; i < listRemoveAttr.size(); i++) {
            root.remove((Attribute)listRemoveAttr.get(i));
        }
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        // ����ͷ����Ϣ����
        root.addAttribute("Version", bsData.getVersion());
        root.addAttribute("MsgID", bsData.getCenterMsgID());
        root.addAttribute("Type", "MonUp");
        root.addAttribute("DateTime", CommonUtility.getDateTime());
        root.addAttribute("SrcCode", sysVO.getSrcCode() );
        root.addAttribute("DstCode", sysVO.getDstCode());
        root.addAttribute("ReplyID", bsData.getCenterMsgID());
        
        root.asXML();
    }
    
    /**
     * ��document��MSG element�޸ĳ�MonUpҪ�� ����Retrun Element,Ҳʹ������MonUpҪ��
     * @param headerDoc
     * @param bsData
     * @param freqStr
     * @param redirect
     * @return
     * @throws CommonException
     */
    public boolean AmendXML(Document headerDoc, MSGHeadVO bsData,
            String freqStr, String redirect) throws CommonException{
        log.info("�޸�MonUp Documentͷ���ļ�");
        boolean retFlg = false;
        
        if (headerDoc != null) {
            Element root = headerDoc.getRootElement();

            Attribute attr = null;
            Element ele = null, eleTo = null;
            
            List list = root.attributes();
            // get Name Spaces List
            List listUrl = root.declaredNamespaces();
            
            for (int i=0; i < listUrl.size(); i++) {
                Namespace nameSpace = (Namespace)listUrl.get(i);
                // remove name space
                root.remove(nameSpace);
            }
            
            List listRemoveAttr = new ArrayList();
            
            // ����ͷ����Ϣ����
            for (int i=0; i < list.size(); i++) {
                attr = (Attribute)list.get(i);
                listRemoveAttr.add(attr);
            }
            
            // ɾ������ͷ����Ϣ������
            for (int i=0; i < listRemoveAttr.size(); i++) {
                root.remove((Attribute)listRemoveAttr.get(i));
            }
            
            // ����ͷ����Ϣ����
            root.addAttribute("Version", bsData.getVersion());
            root.addAttribute("MsgID", bsData.getCenterMsgID());
            root.addAttribute("Type", "MonUp");
            root.addAttribute("DateTime", CommonUtility.getDateTime());
            root.addAttribute("SrcCode", bsData.getDstCode());
            root.addAttribute("DstCode", bsData.getSrcCode());
            root.addAttribute("ReplyID", bsData.getCenterMsgID());
                    
            int findReturn = 0;
            int findReturnInfo = 0;

            for (Iterator i = root.elementIterator(); i.hasNext();) {
                ele = (Element) i.next();
                if (ele.getName().compareTo("Return") == 0) {

                    findReturn = 1;
//                  for (Iterator k = ele.attributeIterator(); k.hasNext();) {
//                      attr = (Attribute) k.next();
////                        if (attr.getName().compareTo("Type") == 0) {
////                            attr.setValue(bsData.getStatusQueryType());
////                        }
//                      if (attr.getName().compareTo("Value") == 0) {
//                          attr.setValue("0");
//                      }
//                      if (attr.getName().compareTo("Desc") == 0) {
//                          attr.setValue("�ɹ�");
//                      }
//                      if (attr.getName().compareTo("Redirect") == 0) {
//                          attr.setValue(redirect);
//                      }
//                      if (attr.getName().compareTo("Freq") == 0) {
//                          attr.setValue(freqStr);
//                      }
//                  }
                }
                if (ele.getName().compareTo("ReturnInfo") == 0) {
                    findReturnInfo = 1;
                }
            }
            if (findReturn == 0) {
                eleTo = root.addElement("Return");
                eleTo.addAttribute("Type", bsData.getStatusQueryType());
                eleTo.addAttribute("Value", "0");
                eleTo.addAttribute("Desc", "�ɹ�");
                eleTo.addAttribute("Redirect", redirect);
                eleTo.addAttribute("Freq", freqStr);
            }
            
            if (findReturnInfo == 0) {
                root.addElement("ReturnInfo");
            }

            
            retFlg = true;
        } else {
            log.error("�޸�Documentͷ���ļ�ʧ�� Doc�ļ�Ϊnull");
            throw new CommonException("�޸�Documentͷ���ļ�ʧ�� Doc�ļ�Ϊnull");
        }
        return retFlg;
    }
    
    public String utf8Togb2312(String str){
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
               case '+':
                   sb.append(' ');
               break;
               case '%':
                   try {
                        sb.append((char)Integer.parseInt(
                        str.substring(i+1,i+3),16));
                   }
                   catch (NumberFormatException e) {
                       throw new IllegalArgumentException();
                  }
                  i += 2;
                  break;
               default:
                  sb.append(c);
                  break;
             }
        }
        // Undo conversion to external encoding
        String result = sb.toString();
        String res=null;
        try{
            byte[] inputBytes = result.getBytes("8859_1");
            res= new String(inputBytes,"UTF-8");
        }
        catch(Exception e){}
        return res;
  }
    
}
