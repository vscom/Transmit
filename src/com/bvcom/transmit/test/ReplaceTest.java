package com.bvcom.transmit.test;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bvcom.transmit.util.CommonUtility;

public class ReplaceTest {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) {

//    	NVRVideoSleepTime("2010-10-31 08:09:40", "2010-10-31 09:10:50");
//    	
//    	ReplaceTest  ReplaceTest = new ReplaceTest();
//    	
//    	System.out.println(ReplaceTest.replaceFunc(3));
//    	
//    	System.out.println(ReplaceTest.replaceFunc(7));
//    	
//    	System.out.println(ReplaceTest.replaceFunc(21));
    	
//    	String channelScan = "XXX=\"2000-01-01 08:09:40\"";
//
//    	String s = RegReplaceString(channelScan, "XXX");
    	
    	String dateTime = "2011-03-30 08:37:46";
//    	
//    	String s = getDateTimeFormat(dateTime);
//    	System.out.println(s);
    	
//    	String newDate = CommonUtility.getDayBefOrAftNMonth(dateTime, -3);
//    	System.out.println(newDate);
    	String filePath = CommonUtility.mkDateTimeFold("C:\\tomcat-6.0.10\\webapps\\PSI", dateTime);
    	System.out.println(filePath);
//        String returnStr = "";
//        File readFilePath = new File("D:\\Loging\\ChannelScanQuery.xml");
//        Document requestDoc = null;
        
//        String dateTime = "2001-01-01 08:37:46";
//        
//        if(dateTime.startsWith("2000")) {
//        	System.out.println("Time Error");
//        }
//        try {
//            returnStr = CommonUtility.readStringFormFile(readFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("Len: " + returnStr.length());
//        System.out.println("msgLen: " + CommonUtility.XML_MSG_END.length());
//        
//        int msgEnd = returnStr.indexOf(CommonUtility.XML_MSG_END) + CommonUtility.XML_MSG_END.length();
//        
//        System.out.println("</Msg>: " + msgEnd);
//        
//        if (msgEnd != CommonUtility.XML_MSG_END.length()) {
//        	returnStr = returnStr.substring(0, msgEnd);
//        }
//        
//        System.out.println("Len: " + returnStr.length());
//        
//        System.out.println(returnStr);
//    	String programName = "StartTime=\"2038-04-22 12:ff:ff\"";
//    	System.out.println(RegReplaceErrTime(programName, "StartTime"));
    }

    public static String RegReplaceErrTime(String replaceString, String stringType) {
    	
    	String regEx= stringType + "=\"(\\d+)-(\\d+)-(\\d+)(\\s)([^0-9]+):([^0-9]+):([^0-9]+)\""; //表示一个或多个a 
    	
    	Pattern p=Pattern.compile(regEx); 

    	Matcher m=p.matcher(replaceString); 

    	String s = m.replaceAll(stringType + "=\"" + CommonUtility.getDateTime()+ "\"");
    	
    	return s;
    }
    
    public static String RegReplaceProgramName(String replaceString, String stringType) {
    	String regEx= stringType + "=\"(\\s*)\""; //表示一个或多个a 
    	
    	Pattern p=Pattern.compile(regEx); 

    	Matcher m=p.matcher(replaceString); 

    	String s=m.replaceAll(stringType + "=\"节目名为空\""); 
    	return s;
    }
    
    public int replaceFunc(int input) {
    	int ret = input;
    	if(input % 3== 0 && input % 7== 0) {
    		ret = 300;
    	} else if(input % 3== 0) {
    		ret = 100;
    	} else if(input % 7 == 0) {
    		ret = 200;
    	}
    	return ret;
    }
    
    public static void NVRVideoSleepTime(String startTime, String EndTime) {
    	Date time1 = CommonUtility.dateStrToDate("2010-10-31 08:09:40");
    	
    	Date time2 = CommonUtility.dateStrToDate("2010-10-31 09:10:50");
    	
    	// 取得时间
    	long min = ((time2.getTime() - time1.getTime())/(1000*60));
    	
//    	System.out.println(min + "m");
		try {
	    	if(min <= 10) {
					Thread.sleep(1000 * 2);
	    	} else if(min > 10 && min <=30) {
	    		Thread.sleep(1000 * 5);
	    	} else if(min > 30 && min <= 60) {
	    		Thread.sleep(1000 * 10);
	    	} else {
	    		Thread.sleep(1000 * 15);
	    	}
	    	
		} catch (InterruptedException e) {
			
		}
    }
    
	public static String getDateTimeFormat (String dateTime) {
		
		dateTime = dateTime.replaceAll("-", "");
		dateTime = dateTime.replaceAll(" ", "");
		dateTime = dateTime.replaceAll(":", "");
		return dateTime;
	}
	
    private static String replaceString(String strText) {
        
        strText = strText.replaceAll("SymbolRate=\"\"", "SymbolRate=\"6875\"");
        
        strText = strText.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
        
        return strText;
    }
    
    public static String RegReplaceString(String replaceString, String stringType) {
    	String regEx= stringType + "=\"(\\d+)-(\\d+)-(\\d+)(\\s)(\\d+):(\\d+):(\\d+)\""; //表示一个或多个a 
    	
    	Pattern p=Pattern.compile(regEx); 

    	Matcher m=p.matcher(replaceString); 

    	String s=m.replaceAll(stringType + "=\"" + CommonUtility.getDateTime()+ "\""); 
  
    	return s;
    }

}
