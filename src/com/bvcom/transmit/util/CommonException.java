
package com.bvcom.transmit.util;

import java.util.ArrayList;
import java.util.List;
//import org.apache.log4j.Logger;

import org.apache.log4j.Logger;

/*
 * ���ܣ�������Ϣ��¼�࣬��������Ϣ�����ڴ����У��ṩ��д������Ϣ�̳и��쳣��
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
	 * ��ȡ�쳣��Ϣ�б�
	 * @return error List �쳣��Ϣ
	 */
	public List getErrorList() 
	{
		return errorList;
	}

	/**
	 * �ж��Ƿ����쳣
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
}
