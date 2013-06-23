package com.bvcom.transmit.test;

import java.io.File;
import java.util.zip.ZipOutputStream;

import com.bvcom.transmit.util.CommonUtility;

public class FileDirTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello, world");
	
		String dataStr = "2010-09-01 10:00:00";
		
		String data = CommonUtility.getDateHourPath(dataStr);	

		String filePath = "D:\\Loging\\SendUpFile\\" + data;
		
		System.out.println(filePath);
		
		getfilePath(filePath);
		
	}
	
	public static String getfilePath (String filePath) {
		
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
						for(int j=fileSubDir.length-1; j>0; j--) {
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

}
