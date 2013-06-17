package com.bvcom.transmit.util;

import java.util.ArrayList;
import java.util.List;

import com.bvcom.transmit.parse.video.domain.AlarmTime;
import com.bvcom.transmit.parse.video.domain.AlarmTimeDao;
/**
 * 
 * @author JI LONG 2011-6-15
 *把运行图节目信息放入内存
 */
public class AlarmTimeMemory {
	public static List<AlarmTime> alarmTimeList=new ArrayList<AlarmTime>();
	public static void alarmTimeToMemory(){
		AlarmTimeDao dao=new AlarmTimeDao();
		alarmTimeList=dao.list();
		//System.out.println("内存中运行图信息："+alarmTimeList);
	}
}
