/**
 * controlAlarm (javaת��)
 * 
 * CommonUtility.java    2007.7.26
 * 
 * Copyright 2007 Dautoit. All Rights Reserved.
 * 
 */

package com.bvcom.transmit.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;

/**
 * ������ ��Ҫ�ǻ��ϵͳʱ�䣬��֤ʱ���ʽ����ȡxml�ȵĻ���������
 * @version  V1.0
 * @author YYZ
 *
 */
public class CommonUtility {
	
    static Logger log = Logger.getLogger(CommonUtility.class.getSimpleName());
    
    public static final String VERSION = "transmit v3.0 - 2012.7.20";
    public static boolean AlarmSwitch=false;
	public static final int NoExceptionError = 0;
	public static final int MalformedURLExceptionError = 1;
	public static final int SocketTimeoutExceptionError = 2;
	public static final int NetIOExceptionError = 3;
	public static final int FileIOExceptionError = 4;
	public static final int StringIOExceptionError = 5;
	public static final int DocumentExceptionError = 6;
	public static final int FileNotFoundExceptionError = 7;
	public static final int SQLExceptionError = 8;
	public static final int NoRecordExceptionError = 9;
	public static final int SecurityExceptionError = 10;
	public static final int InterruptedExceptionError = 11;
	
	private static int MSG_CENTER_ID = 1000;
	
	private static int	lastExceptionError = NoExceptionError;
    
	public static final int ALARM_NEW_CHANNEL_SCAN_TASK_TIME = (1000*60*60*2);
	
    // 50����
    public static final int CHANNEL_SCAN_WAIT_TIMEOUT = (1000*60*50);
    // 5����
    public static final int CHANNEL_SCAN_WAIT_TIMEOUT2 = (1000*60*5);
	// 3��=1000*3
    public static final int CONN_WAIT_TIMEOUT = (100*3);
    // 10��ChangeProgramQueryHandle
    public static final int CHANGE_PROGRAM_QUERY = (1000*10);
    // 20��
    public static final int TASK_WAIT_TIMEOUT = (1000*20);
    
    public static final int HISTORY_DOWN_WAIT_TIMEOUT = (1000*10);
    // 1��
    public static final int VIDEO_RETURN_WAIT_TIME = (1000);
    
    // 1Сʱ
    public static final int RECORD_TASK_WAIT_TIME = (1000*60*60);
    
    public static final int REPLY_ALARM_ERROR_TIME = (1000*60);
    
    // ��ͣ3�룬���ڽ�Ŀ�Ľ����RTV���������
    public static final int VIDEO_CHANGE_SLEEPTIME = (500);
    
    public static final int SMG_NVR_SLEEPTIME = (1000*1);
    
    public static final String XML_VERSION_2_0 = "4";
    
    public static final String CHANNEL_SCAN_PATH_2_0 = "D:\\Loging\\ChannelScanQueryV2.3.xml";
    
    public static final String XML_VERSION_2_3 = "2.3";
    
    public static final String CHANNEL_SCAN_PATH_2_3 = "D:\\Loging\\ChannelScanQueryV2.3.xml";
    
   public static final String XML_VERSION_2_5 = "2.5";
    
    public static final String CHANNEL_SCAN_PATH_2_5 = "D:\\Loging\\ChannelScanQueryV2.5.xml";
    
    public static final String XML_MSG_END = "</Msg>";
    
    /**
     * ��������
     */
    public static int AlarmDatebaseDumpCount = 0; 
    
    /**
     * ��������
     * @return
     */
	public static int getAlarmDatebaseDumpCount() {
		AlarmDatebaseDumpCount += 1;
		if (AlarmDatebaseDumpCount == Integer.MAX_VALUE) {
			AlarmDatebaseDumpCount = 0;
		}
		return AlarmDatebaseDumpCount;
	}
	
	public static int getLastExceptionError() {
		return lastExceptionError;
	}
	public static void setLastExceptionError(int error) {
		lastExceptionError = error;
	}
	
	public static Boolean ValidateDateTime(String dateTime) {
		if (dateTime.compareTo("") == 0) {
			return false;
		} else if (dateTime.length() != 19) {
			return false;
		} else if (dateTime.charAt(4) != '-' && dateTime.charAt(7) != '-') {
			return false;
		} else if (dateTime.charAt(13) != ':' && dateTime.charAt(16) != ':') {
			return false;
		}
		
		return true;
	}
	public static Boolean ValidateDate(String date) {
		if (date.compareTo("") == 0) {
			return false;
		} else if (date.length() != 10) {
			return false;
		} else if (date.charAt(4) != '-' && date.charAt(7) != '-') {
			return false;
		} 
		
		return true;
	}
	public static Boolean ValidateDeviceCode(String code) {
		if (code.compareTo("") == 0) {
			return false;
		} else if (code.length() != 9) {
			return false;
		}
		
		return true;
	}
	public static Boolean ValidateBooleanString(String str) {
		if ((str.compareTo("0") == 0) || (str.compareTo("1") == 0)) {
			return true;
		} 
		
		return false;
	}
	public static String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
	    Date currentTime_1 = new Date();
	    String str_date = formatter.format(currentTime_1); 
	    StringTokenizer token = new StringTokenizer(str_date,"-"); 
	    String year = token.nextToken();
	    String month= token.nextToken();
	    String day = token.nextToken();
	    String date = year + month + day;
	    return date;
	}     
	
	/**
	 * YYYY-MM-DD HH:MM:SS
	 * @return YYYY-MM-DD HH:MM:SS
	 */
	public static String getDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 
	    Date currentTime_1 = new Date();
	    String str_date = formatter.format(currentTime_1); 
	    StringTokenizer token = new StringTokenizer(str_date,"-"); 
	    String year = token.nextToken();
	    String month= token.nextToken();
	    String day = token.nextToken(); 
	    String hh = token.nextToken(); 
	    String mm = token.nextToken(); 
	    String ss = token.nextToken();
	    String DateTime = year + "-" + month + "-" + day +
	    				  " " + hh + ":" + mm + ":" + ss;
	    return DateTime;
	}
	
    /**
     * ��ʽ��ʱ��Ϊyyyy-MM-dd-HH�ĸ�ʽ
     * @param dateTime
     * @return String ��ʽ�����yyyy-MM-dd-HH�ַ���
     */
	public static String getDateHour (String dateTime) {
		
		String year = dateTime.substring(0, 4);
		String month = dateTime.substring(5, 7);
		String day = dateTime.substring(8, 10);
		String hour = dateTime.substring(11, 13);
        
		dateTime = year + "-" + month + "-" + day + "-" + hour;
		return dateTime;
	}

	/**
	 * ����ʱ�䴴���ļ�·��
	 * @param receFilePath
	 * @param dataTimeStr
	 * @return dataFlod
	 */
	public static String mkDateTimeFold(String receFilePath, String dataTimeStr) {
		
		String dataFlod = "";
		String desData = getDateHour(dataTimeStr);
        // ����Ŀ¼
        String fileFlod = receFilePath + "\\";
        CommonUtility.CreateFolder(fileFlod);
        String[] dataFold = desData.split("-");
        // ����Ŀ¼
        for(int i=0; i<dataFold.length-1; i++) {
        	fileFlod += dataFold[i] + "/";
        	dataFlod += dataFold[i] + "/";
        	CommonUtility.CreateFolder(fileFlod);
        }
        return dataFlod;
	}
	
	public static String getEPGFilePath (String filePath) {
		
		File file = new File(filePath);
		
		if (file.isDirectory()) {
			File[] fileDir = file.listFiles();

			String[] fileListStr = file.list();
			
			try {
				for (int i=fileDir.length-1; i >0 ; i--) {
					String ZipEntryName = fileListStr[i];
					System.out.println(ZipEntryName + "\t" + fileDir[i].getAbsolutePath());
					if(fileDir[i].isDirectory()) {
						File[] fileSubDir = fileDir[i].listFiles();
						for(int j=fileSubDir.length-1; j>=0; j--) {
							System.out.println(fileSubDir[j].getAbsolutePath());
							return fileSubDir[j].getAbsolutePath();
						}
					}
				}
			} catch (Exception ex) {
				
			}
		}
		
		return filePath;
	}
	
	public static String getTableFilePath(String filePath) {
		
		File file = new File(filePath);
		
		if (file.isDirectory()) {
			File[] fileDir = file.listFiles();

			String[] fileListStr = file.list();
			
			try {
				for (int i=fileDir.length-1; i >0 ; i--) {
					String ZipEntryName = fileListStr[i];
					System.out.println(ZipEntryName + "\t" + fileDir[i].getAbsolutePath());
					if(fileDir[i].isDirectory()) {
						File[] fileSubDir = fileDir[i].listFiles();
						for(int j=fileSubDir.length-1; j>=0; j--) {
							System.out.println(fileSubDir[j].getAbsolutePath());
							return fileDir[i].getAbsolutePath();
						}
					}
				}
			} catch (Exception ex) {
				
			}
		}
		
		return filePath;
	}
	
    /**
     * ��ʽ��ʱ��Ϊyyyy-MM-dd-HH�ĸ�ʽ
     * @param dateTime
     * @return String ��ʽ�����yyyy-MM-dd-HH�ַ���
     */
	public static String getDateHourPath (String dateTime) {
		
		String year = dateTime.substring(0, 4);
		String month = dateTime.substring(5, 7);
		String day = dateTime.substring(8, 10);
		String hour = dateTime.substring(11, 13);
        
		dateTime = year + "\\" + month + "\\" + day;
		
		return dateTime;
	}
	
	/**
	 * YYYY-MM-DD HH:MM:SS --> YYYYMMDDHHMMSS
	 * @param dateTime YYYY-MM-DD HH:MM:SS
	 * @return String YYYYMMDDHHMMSS
	 */
	public static String getDateTimeFormat (String dateTime) {
		
		dateTime = dateTime.replaceAll("-", "");
		dateTime = dateTime.replaceAll(" ", "");
		dateTime = dateTime.replaceAll(":", "");
		return dateTime;
	}
	
	private static int power(int val, int numOfPower) {
		for (int i = 0; i < numOfPower; i++) {
			val *= 10;
		}
		return val;
	}
	public static int atoi(String str) {
		int retVal = 0;
		for (int i = 0; i < str.length(); i++) {
			retVal += (str.charAt(i)-'0')*power(1, str.length()-i-1);
		}
		return retVal;
	}
	
	public static int getContentLength(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection urlcon = url.openConnection();

			return urlcon.getContentLength();
		} catch (MalformedURLException mue) {
			log.error("getContentLength:" + "MalformedURLException: "+mue.getMessage());
			lastExceptionError = MalformedURLExceptionError;
			return 0;
    	} catch (IOException ioe) {
            log.error("getContentLength:" + "IOException: "+ioe.getMessage());
			lastExceptionError = NetIOExceptionError;
			return 0;
		}
	}
	
	/**
	 * ����Post�е�������,SMSUP�ͷ���StoreFilePath��.Down�����ַ���getString��
	 * ���ض����������ַ�������ļ�·��
	 * @param Reader
	 * @return �����������ַ�������ļ�·��
	 */
	public static String readStringFromURL(Reader rd) throws IOException {
		BufferedReader reader = null;
		try {
			String getString = "";
			String smallString = "";
			reader = new BufferedReader(rd);
			if(reader!=null){
				while ((smallString = reader.readLine()) != null) {
					getString += smallString;
				}
				reader.close();
			}
			return getString;
		} catch (IOException ioe) {
			log.info("IOException: " + ioe.getMessage());
			/**���������Ϣ���ļ�*/
    		PrintWriter pw;
			try {
				pw = new PrintWriter(new File("D:/IOException.log"));
				ioe.printStackTrace(pw);
				pw.flush();
				pw.close();
			} catch (Exception e1) {
			}
			/******/
			if (reader != null) {
				reader.close();
			}
			throw ioe;
		}
	}
	
	public static String readStringFromReader(Reader rd, int length) {
		
		try {
			int len = 1024;
    		char[] cString = new char[len];
    		int retVal = 0;
    		String getString = new String();
    		while ((retVal = rd.read(cString, 0, len)) != -1) {
    			getString += String.valueOf(cString, 0, retVal);
    		}
    		
    		return getString;
    	} catch (IOException ioe) {
            log.error("readStringFromReader:" + "IOException: "+ioe.getMessage());
			lastExceptionError = FileIOExceptionError;
			return null;
		}
	}
	
	static int in =1;
//	public static String readStringFromReaderBeta(Reader rd, int length) {
//		
//		try {
//			int len = 1024;
//    		char[] cString = new char[len];
//    		int retVal = 0;
//    		String getString = "";
//    		String smallString = null;
//    		BufferedReader reader = new BufferedReader(rd);
//    		PersistentData data = new PersistentData();
//    		in++;
//    		if(in==50)
//    			in = 1;
//    
//    		boolean FindSMSUp = false;
//    		if(length > data.getNeedBeRedirectedFileLength())
//    		{
//    			while((smallString=reader.readLine())!=null)
//    			{
//    				if(smallString.indexOf("SMSUp")== -1)
//    					getString += smallString;
//    				else
//    					{
//    					FindSMSUp = true;
//    					break;
//    					}
//    					
//    			}
//    		
//    		if(FindSMSUp){
//    			String filename = new String("D:/FromSMSUp_Beta" + in +".xml");
//    			File file = new File(filename);
//    			FileOutputStream out = new FileOutputStream(file);
//    			out.write(getString.getBytes());
//    			out.write(new String("\n").getBytes());
//    			out.write(smallString.getBytes());
//    			out.write(new String("\n").getBytes());
//    			while((smallString=reader.readLine())!= null){
//    				out.write(smallString.getBytes());
//    				out.write(new String("\n").getBytes());             //�����ȡ����smallStringÿ��һ�е�ʱ���Ѿ������ո�,������Ҫȥ��.
//    			}
//    			out.flush();
//    			out.close();
//    			getString = filename;
//    		}
//    		}
//    		else{    		
//    			while ((retVal = rd.read(cString, 0, len)) != -1) {
//    				getString += String.valueOf(cString, 0, retVal);
//    		}
//    		}
//    		return getString;
//    	} 
//    		catch (IOException ioe) {
//                log.error("readStringFromReaderBeta:" + "IOException: "+ioe.getMessage());
//                lastExceptionError = FileIOExceptionError;
//                return null;
//		}
//	}
	
	
	
	
	
	
	static int num = 1;
	public static String readStringFromReaderByLi(DataInputStream dis, int length) {       //��ȡzip�ļ�ʱ�����
		
		try {
			int len = 1024;
    		byte[] cString = new byte[len];
    		int retVal = 0;
    		
    		String strFile = new String("D:/downToUnzip"+num+".txt");
    		num++;
    		if(num==1000){
    			num = 1;
    		}
    		File unzipFile = new File(strFile);
    		FileOutputStream fos = new FileOutputStream(unzipFile);
    		while ((retVal = dis.read(cString, 0, len)) != -1) {
    			
    			fos.write(cString, 0, retVal);
    		}
    		
    		return strFile;
    	} catch (IOException ioe) {
            log.error("readStringFromReaderByLi:" + "IOException: "+ioe.getMessage());
			lastExceptionError = FileIOExceptionError;
			return null;
		}
	}
	
	public static String readStringFromURLToFile(String urlString, int readTimeout, FileWriter fw)
	{
		
		
		try {
    		URL url = new URL(urlString);
        	URLConnection urlcon = url.openConnection();
        	urlcon.setReadTimeout(readTimeout);
        	log.info("Read from "+urlString);
    		InputStreamReader inReader = 
    			new InputStreamReader(urlcon.getInputStream(), "GB2312"); 
    
    		
    		// int len = (getContentLength(urlString)>100000) ? 100000 : getContentLength(urlString);
    		int len = 1024;
    		log.info("The Package is "+len);
    		char[] cString = new char[len];

    		String getString = new String();
    	
    		int retVal = 0;
    		try {
	    		while ((retVal = inReader.read(cString, 0, len)) != -1) {
	    		 getString += String.valueOf(cString, 0, retVal);
	    		}
    		}
    		catch (Exception eee) {
    			log.error("readStringFromURLToFile:" + eee.getMessage());
    		}
    		inReader.close();
    		
    		return getString;
    	} catch (MalformedURLException mue) {
            log.error("readStringFromURLToFile:" + "MalformedURLException: "+mue.getMessage());
			lastExceptionError = MalformedURLExceptionError;
			return null;
    	} catch (SocketTimeoutException ste) {
    		log.error("Read from "+urlString);
			log.error("����ʱ"+urlString);
            log.error("readStringFromURLToFile:" + "SocketTimeoutException: "+ste.getMessage());
			lastExceptionError = SocketTimeoutExceptionError;
			return null;
		} catch (IOException ioe) {
            log.error("readStringFromURLToFile:" + "IOException: "+ioe.getMessage());
			lastExceptionError = NetIOExceptionError;
			return null;
		}
		

	}
	static int count =1;
	public static String readStringFromURL(String urlString, int readTimeout) {		   //��ȡ���ļ���ʱ�����
		try {
			log.info("Begin Read From URL");
    		URL url = new URL(urlString);
        	URLConnection urlcon = url.openConnection();
        	urlcon.setReadTimeout(readTimeout);
        	log.info("Read from "+urlString);

        	DataInputStream inReader = 
    			new DataInputStream(urlcon.getInputStream());
    		
    		// int len = (CommonUtility.getContentLength(urlString)>100000)? 100000 : CommonUtility.getContentLength(urlString);
    		int len = 1024;
    		log.info("The Package is"+len);
    		byte[] cString = new byte[len];
    		int retVal=0;
    		String filename = new String("D:/down"+count+".txt");
    		count++;
    		if(count == 50)
    			count =1;
    		
    		File file = new File(filename);
    		FileOutputStream out =  new FileOutputStream(file);
    		try {
    		while ((retVal = inReader.read(cString, 0, len)) != -1) {
    			out.write(cString, 0, retVal);
    		}
    		}
    		catch (Exception eee) {
    			log.error("readStringFromURL:" + eee.getMessage());
    		}
    		inReader.close();
    	
    		return filename;
    		
    	} catch (MalformedURLException mue) {
            log.error("readStringFromURL:" + "MalformedURLException: "+mue.getMessage());
			lastExceptionError = CommonUtility.MalformedURLExceptionError;
			return null;
    	} catch (SocketTimeoutException ste) {
    		log.error("Read from "+urlString+" end "+CommonUtility.getDateTime());
			log.error("����ʱ"+urlString);
            log.error("readStringFromURL:" + "SocketTimeoutException: "+ste.getMessage());
			lastExceptionError = CommonUtility.SocketTimeoutExceptionError;
			return null;
		} catch (IOException ioe) {
            log.error("readStringFromURL:" + "IOException: "+ioe.getMessage());
			lastExceptionError = CommonUtility.NetIOExceptionError;
			return null;
		}		
	}
	
	
	public static char[] readCharArrayFromURL(String urlString, int readTimeout) {
		try {
    		URL url = new URL(urlString);
        	URLConnection urlcon = url.openConnection();
        	urlcon.setReadTimeout(readTimeout);
        	log.info("Read from "+urlString+" start "+CommonUtility.getDateTime());
    		InputStreamReader inReader = 
    			new InputStreamReader(urlcon.getInputStream(), "GB2312"); 
    	
    		int length = getContentLength(urlString);
    		//int len = (length>100000) ? 100000 : length;
    		int len = 1024;
    		log.info(urlString+length);
    		char[] cString = new char[length];
    		int retVal = 0;
    		int offset = 0;
    		while ((retVal = inReader.read(cString, offset, len)) != -1) {
    			log.info(retVal);
    			offset += retVal;
    		}
    		inReader.close();
    		return cString;
    	} catch (MalformedURLException mue) {
            log.error("readCharArrayFromURL:" + "MalformedURLException: "+mue.getMessage());
			lastExceptionError = MalformedURLExceptionError;
			return null;
    	} catch (SocketTimeoutException ste) {
    		log.error("Read from "+urlString+" end "+CommonUtility.getDateTime());
            log.error("����ʱ"+urlString);
            log.error("readCharArrayFromURL:" + "SocketTimeoutException: "+ste.getMessage());
			lastExceptionError = SocketTimeoutExceptionError;
			return null;
		} catch (IOException ioe) {
            log.error("readCharArrayFromURL:" + "IOException: "+ioe.getMessage());
			lastExceptionError = NetIOExceptionError;
			return null;
		}
	}
	
	public static String readStringFormFile (File file) throws CommonException {
    	StringBuffer buff = new StringBuffer();
    	String smallString = "";
    	BufferedReader br = null;
    	
		FileInputStream fileIn = null;
		try {

			br = new BufferedReader(new FileReader(file));

			while ((smallString = br.readLine()) != null) {
				buff.append(smallString);
				buff.append("\n");
			}
//			fileIn = new FileInputStream(file);
//			while ((retVal = fileIn.read(rArray, 0, 1000)) != -1) {
//				buff.append(new String(rArray, 0, retVal, "GB2312"));
//			}
		} catch (FileNotFoundException e) {
			log.error("�Ҳ����ļ���" + e.getMessage());
			throw new CommonException("��Ӧ���ݲ�����");
		}catch (IOException e) {
			log.error("I/O����");
			throw new CommonException("I/O����" + e.getMessage());
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("���ر�ʧ�ܣ�" + e.getMessage());
				}
			}
			if (fileIn != null) {
				try {
					fileIn.close();
				} catch (IOException e) {
					log.error("�ļ��ر�ʧ�ܣ�" + e.getMessage());
				}
			}
		}
    	return buff.toString();
	}
	
	/**
	 * �����ļ��� ���ļ��в����ھʹ���,���ھ��˳� ����:���ļ���temp  Path = "d:/temp/"
	 * @param Path  Ҫ�����ļ���·��
	 * @return  �ɹ�true ʧ��false
	 */
	public static Boolean CreateFolder(String Path) throws SecurityException{
		try {
			if (!(new File(Path).isDirectory())) {
				new File(Path).mkdir();
			}
		} catch (SecurityException e) {
			log.error("CreateFolder Err");
			throw e;
		}
		return true;
	}
	
	/**
	 * ��StoreStr��������ļ�,
	 * 
	 * @param String
	 *            Ҫ������ַ���
	 * @param filename
	 *            ����λ��
	 * @return
	 */
	public static int StoreIntoFile(String StoreStr, String filename) {
		try {
			if(StoreStr == null || StoreStr.equals("")) {
				return 0;
			}
			log.info("�ļ����浽��" + filename);
			FileWriter outWriter = new FileWriter(filename);
			outWriter.write(StoreStr);
			outWriter.flush();
			outWriter.close();
			log.info("�ļ�����ɹ�");
			return 1;
		} catch (IOException ioe) {
			log.error("StoreIntoFile, IOException: " + ioe
					.getMessage());
			return 0;
		}
	}
	
    /**
     * д�ļ�ʱת��ΪGB2312
     * @param StoreStr
     * @param filename
     */
    public static void WriteFile(String StoreStr, String filename) {
        try {
        	if(StoreStr == null || StoreStr.equals("")) {
        		return;
        	}
            //log.info("�ļ����浽��" + filename);
            FileOutputStream os = new FileOutputStream(filename);
            
            Writer out = new OutputStreamWriter(os, "GB2312");
            out.write(StoreStr);
            out.close();
            os.close();
            //log.info("�ļ�����ɹ�");
        } catch (IOException ioe) {
            log.error("WriteFile, IOException: " + ioe
                    .getMessage());
        }
        
    }
    
	/**
	 * ��ӡ�����ջ
	 * @param ce
	 */
	public static void printErrorTrace (Exception ce) {
		// ��ӡ��ջ����
        StackTraceElement[] trace = ce.getStackTrace();
        // �����Ϣ����
        StringBuffer errorBuffer = new StringBuffer();
        for (int i=0; i < trace.length; i++) {
        	errorBuffer.append("\tat " + trace[i] + "\n");
        }
		log.error("�����ջ��\n" + errorBuffer.toString());
	}
	
	  /**
	   * ��һ�������ַ���ת��������
	   * @param sDate String
	   * @return Date yyyy-mm-dd
	   */
	  public static Date dateStrToDate(String sDate) {
	    Date date = null;
	    try {
	      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      date = df.parse(sDate);
	    }
	    catch (Exception e) {
	      log.error("����ת��ʧ��:" + e.getMessage());
	    }
	    return date;
	  }
	  
	  /**
	   * 
	   * ȡ��ָ��ʱ���ǰ��󼸸�Сʱ������Ϊ��N��Сʱ������Ϊǰ����Сʱ
	   * ʱ���ַ��� ��ʽΪ"yyyy-MM-dd HH:mm:ss"
	   * 
	   * @param dateTimeStr ʱ���ַ��� ��ʽΪ"yyyy-MM-dd HH:mm:ss"
	   * @param nHour ƫ��ʱ�� ����Ϊ��N��Сʱ������Ϊǰ����Сʱ
	   * @return String ƫ�ƺ��ʱ�䴮
	   */
	  public static String getHourOfDayBefOrAftNHour(String dateTimeStr, int nHour) {
			Calendar calendar = Calendar.getInstance();
			
			Date dTest = dateStrToDate(dateTimeStr);
			
			calendar.setTime(dTest);
			
			calendar.add(Calendar.HOUR_OF_DAY, nHour);
			
			Date d2=calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		    return formatter.format(d2); 
	  }
	  
	  /**
	   * 
	   * ȡ��ָ��ʱ���ǰ����죬����Ϊ��N�죬����Ϊǰ����
	   * ʱ���ַ��� ��ʽΪ"yyyy-MM-dd HH:mm:ss"
	   * 
	   * @param dateTimeStr ʱ���ַ��� ��ʽΪ"yyyy-MM-dd HH:mm:ss"
	   * @param nHour ƫ��ʱ�� ����Ϊ��N�죬����Ϊǰ����
	   * @return String ƫ�ƺ��ʱ�䴮
	   */
	  public static String getDayBefOrAftNMonth(String dateTimeStr, int nDay) {
			Calendar calendar = Calendar.getInstance();
			
			Date dTest = dateStrToDate(dateTimeStr);
			
			calendar.setTime(dTest);
			
			calendar.add(Calendar.DAY_OF_MONTH, nDay);
			
			Date d2=calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		    return formatter.format(d2); 
	  }
      
        /**
         * ���SMGͨ������
         * ͨ������(1:ChannelScanQuery(Ƶ��ɨ���ָ���ѯ) 2: AutoRecord(�Զ�¼��)) 3:GetIndexSet(����ָ�������ͼ)
         * @param indexType
         * @param SMGSendList
         * @return List<SMGSendList>
         */
        public static List checkSMGChannelType(String indexType, List SMGSendList) {
            // �������ݽṹ����
            MemCoreData coreData = MemCoreData.getInstance();
            
            // ȡ��SMG�����ļ���Ϣ
            List SMGCardList = coreData.getSMGCardList();
            // ȥ��URL��ַ���
            String oneURL = "";
            for (int i=0; i< SMGCardList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
                if (oneURL.equals(smg.getURL().trim())) {
                    continue;
                }
                if(indexType.equals(smg.getIndexType())) {
                    // ������ͬ�����ݱ��浽List��
                    SMGSendList.add(smg);
                    // FIXME Ϊ�˲���ȥ���ȽϹ���
                    oneURL = smg.getURL().trim();
                }
            }
            return SMGSendList;
        }
        
        /**
         * ȡ��SMG URL
         * @param index
         * @param TSCSendList
         * @return
         */
        @SuppressWarnings("unchecked")
		public static void checkSMGChannelIndex(int index, List SMGSendList) {
            // �������ݽṹ����
            MemCoreData coreData = MemCoreData.getInstance();
            // ȡ��SMG�����ļ���Ϣ
            List SMGCardList = coreData.getSMGCardList();
            // ȥ��URL��ַ���
            String oneURL = "";
            for (int i=0; i< SMGCardList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
                //by tqy ���ڣ�STOP
                if(smg.getIndexType().equals("Stop")){
                	continue;
                }
                if (oneURL.equals(smg.getURL().trim())) {
                    continue;
                }
                //by tqy 2012-05-20 ���ӿ���ͨ���Ĵ�������ʹ�ÿ��л����Զ�¼�Ƶ�ͨ��
                if(smg.getIndexType().equals("AutoRecord")||smg.getIndexType().equals("Free")){
	                if (index == smg.getIndex()) {
	                    SMGSendList.add(smg);
	                    oneURL = smg.getURL().trim();
	                } else if (index == 99){
	                    SMGSendList.add(smg);
	                    oneURL = smg.getURL().trim();
	                }
                }
            }
        }
        
        /*
         * �жϵ�ǰͨ���Ƿ�ͣ��
         */
        @SuppressWarnings("unchecked")
		public static boolean checkSMGChannelStatus(int index){
        	boolean ret =false;
        	  // �������ݽṹ����
            MemCoreData coreData = MemCoreData.getInstance();
            // ȡ��SMG�����ļ���Ϣ
            List SMGCardList = coreData.getSMGCardList();
            // ȥ��URL��ַ���
            for (int i=0; i< SMGCardList.size(); i++) {
                SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
                //by tqy ���ڣ�STOP
                if(smg.getIndexType().equals("Stop")){
                	ret = true;
                	break;
                }
            }
            return ret;
        }
        /**
         * ȡ��ת��TSC URL
         * @param index
         * @param TSCSendList
         * @return
         */
        public static void checkTSCChannelIndex(int index, List TSCSendList) {
            // �������ݽṹ����
            MemCoreData coreData = MemCoreData.getInstance();
            // ȡ��TSC�����ļ���Ϣ
            List TSCList = coreData.getTSCList();
            TSCInfoVO tsc = null;
            // ȥ��URL��ַ���
            String oneURL = "";
            for (int i=0; i< TSCList.size(); i++) {
                tsc = (TSCInfoVO) TSCList.get(i);
                if (oneURL.equals(tsc.getURL().trim())) {
                    continue;
                }
                if (index >= tsc.getIndexMin() && index <= tsc.getIndexMax()) {
                    oneURL = tsc.getURL().trim();
                    break;
                }
            }
            if (oneURL.equals("")) {
            	return;
            }
            int sendListSize = TSCSendList.size();
            if (sendListSize == 0) {
            	TSCSendList.add(tsc);
            } else {
	            for(int i=0; i < sendListSize; i++) {
	            	TSCInfoVO oldtsc = (TSCInfoVO) TSCSendList.get(i);
	            	if (oneURL.equals(oldtsc.getURL().trim())) {
	            		continue;
	            	} else {
	            		TSCSendList.add(tsc);
	            		break;
	            	}
	            }
            }
        }
        
        /**
         * ȡ�ö໭IPM URL
         * @param index
         * @param TSCSendList
         * @return
         */
        public static void checkIPMChannelIndex(int index, List IPMSendList) {
            // �������ݽṹ����
            MemCoreData coreData = MemCoreData.getInstance();
            // ȡ��IPM�����ļ���Ϣ
            List IPMList = coreData.getIPMList();
            // ȥ��URL��ַ���
            String oneURL = "";
            IPMInfoVO ipm = null;
            for (int i=0; i< IPMList.size(); i++) {
                ipm = (IPMInfoVO) IPMList.get(i);
                if (oneURL.equals(ipm.getURL().trim())) {
                    continue;
                }
                if (index == 0 || (index >= ipm.getIndexMin() && index <= ipm.getIndexMax())) {
                    oneURL = ipm.getURL().trim();
                    break;
                }
            }
            
            if (oneURL.equals("")) {
            	return;
            }
            int sendListSize = IPMSendList.size();
            if (sendListSize == 0) {
            	IPMSendList.add(ipm);
            } else {
	            for(int i=0; i < sendListSize; i++) {
	            	IPMInfoVO oldtsc = (IPMInfoVO) IPMSendList.get(i);
	            	if (oneURL.equals(oldtsc.getURL().trim())) {
	            		continue;
	            	} else {
	            		IPMSendList.add(ipm);
	            		break;
	            	}
	            }
            }
        }
        
        /**
         * ͨ��������ʽ�滻ϵͳ��ǰʱ��
         * @param replaceString ��Ҫ�滻��XML�ַ���
         * @param stringType �ַ���������
         * @return �滻���String
         */
        public static String RegReplaceString(String replaceString, String stringType) {
        	String regEx= stringType + "=\"(\\d+)-(\\d+)-(\\d+)(\\s)(\\d+):(\\d+):(\\d+)\""; //��ʾһ������a 
        	
        	Pattern p=Pattern.compile(regEx); 

        	Matcher m=p.matcher(replaceString); 

        	String s=m.replaceAll(stringType + "=\"" + CommonUtility.getDateTime()+ "\""); 
      
        	return s;
        }

        public static String getIPbyFreq(int freq) {
    		String rtvsIP = "";
    		
    		if(freq != 0) {
    			rtvsIP = "239.0.0." + (freq/4000);
    		}
    		return rtvsIP;
        }
        
        public static int getPortbyServiceID(int serviceID) {
    		int rtvsPort = 0;
    		
    		if(serviceID != 0) {
    			rtvsPort = serviceID + 2000;
    		}
    		return rtvsPort;
        }
        
        public static int getMsgID() {
        	if(MSG_CENTER_ID > (Integer.MAX_VALUE-10)) {
        		MSG_CENTER_ID = 0;
        	} else {
        		MSG_CENTER_ID++;
        	}
        	return MSG_CENTER_ID;
        }
        
        public static int NVRVideoSleepTime(String startTime, String EndTime) {
        	int retMin = 0;
        	Date time1 = CommonUtility.dateStrToDate(startTime);
        	
        	Date time2 = CommonUtility.dateStrToDate(EndTime);
        	
        	// ȡ��ʱ��
        	long min = ((time2.getTime() - time1.getTime())/(1000*60));
        	
	    	if(min <= 10) {
				retMin = 1000 * 2;
	    	} else if(min > 10 && min <=30) {
	    		retMin = 1000 * 5;
	    	} else if(min > 30 && min <= 60) {
	    		retMin = 1000 * 7;
	    	} else if(min > 60 && min <=120) {
	    		retMin = 1000 * 10;
	    	} else {
	    		retMin = 1000 * 13;
	    	}
	    	
	    	try {
    	    	Thread.sleep(retMin);
    		} catch (InterruptedException e) {
    			
    		}
    		return retMin;
        }
}
