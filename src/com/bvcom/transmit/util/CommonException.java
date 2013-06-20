
package com.bvcom.transmit.util;

import java.util.ArrayList;
import java.util.List;
//import org.apache.log4j.Logger;

import org.apache.log4j.Logger;

/*
 * 功能：错误信息记录类，将错误信息保存在此类中，提供读写错误信息继承父异常类
 * 
 **/
public class CommonException extends Exception {
	
	private static Logger log = Logger.getLogger(CommonException.class.getSimpleName());
	
	private static final long serialVersionUID = 1L;

	public static boolean errorFlg = false;

	private List<String> errorList = new ArrayList<String>();

	public CommonException(String errString) 
	{
		super(errString);
		//log.error(errString);
		errorList.add(errString);
		//this.printStackTrace();
	}

	/**
	 * 获取异常信息列表
	 * @return error List 异常信息
	 */
	public List getErrorList() 
	{
		return errorList;
	}

	/**
	 * 判断是否有异常
	 * @return boolean 
	 */
	public boolean isHaveError() 
	{
		if (errorList.size() > 0) 
		{
			errorFlg = false;
		}
		else
		{
			errorFlg = true;
		}
		return errorFlg;
	}
    
    /**
     * 
     */
    public CommonException() {
        super();
    }

    /**
     * @param arg0
     */
    public CommonException(Throwable arg0) {
        super(arg0);
        //this.printStackTrace();

    }

    /**
     * @param arg0
     * @param arg1
     */
    public CommonException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        //this.printStackTrace();
    }

    /**
     * 打印错误堆栈
     * @param ce
     */
    public static void printErrorTrace (Exception ce) {
        // 打印堆栈错误
        StackTraceElement[] trace = ce.getStackTrace();
        // 存放信息内容
        StringBuffer errorBuffer = new StringBuffer();
        for (int i=0; i < trace.length; i++) {
            errorBuffer.append("\tat " + trace[i] + "\n");
        }
        log.error("错误堆栈：\n" + errorBuffer.toString());
    }
}
