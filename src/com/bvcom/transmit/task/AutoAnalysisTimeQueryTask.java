package com.bvcom.transmit.task;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.tiling.scheduling.Scheduler;
import org.tiling.scheduling.SchedulerTask;
import org.tiling.scheduling.examples.iterators.DailyIterator;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.handle.si.ChannelScanQueryHandle;

public class AutoAnalysisTimeQueryTask {

	private static Logger log = Logger.getLogger(AutoAnalysisTimeQueryTask.class.getSimpleName());
	
    public static Scheduler scheduler = new Scheduler(); 
    private final SimpleDateFormat dateFormat =  new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS"); 
    private int hourOfDay, minute, second; 

    public AutoAnalysisTimeQueryTask(int hourOfDay, int minute, int second) { 
        this.hourOfDay = hourOfDay; 
        this.minute = minute; 
        this.second = second; 
    }
    public AutoAnalysisTimeQueryTask() {
    	getStartTime();
    }
    
    public void newScheduler() {
    	scheduler = new Scheduler(); 
    }
    
    private void getStartTime() {
    	
        AutoAnalysisTimeQueryConfigFile AutoAnalysisTimeQueryConfigFile = new AutoAnalysisTimeQueryConfigFile();
        try {
	        String startTime = AutoAnalysisTimeQueryConfigFile.getAutoAnalysisTime();
	        
	        String[] timeArray = startTime.split(":");
	        this.hourOfDay =Integer.valueOf(timeArray[0]); 
	        this.minute = Integer.valueOf(timeArray[1]); 
	        this.second = Integer.valueOf(timeArray[2]); 
        } catch (Exception ex) {
	        this.hourOfDay = 1; 
	        this.minute = 0; 
	        this.second = 0; 
        }
    }
    
    public void start() {
    	log.info("\n\n    \t------- 启动数据业务时间分析任务: " + hourOfDay + ":" + minute + ":" + second + " ---------\n");
    	
        scheduler.schedule(new SchedulerTask() {
            public void run() {
                startChannelScan(); 
            }
            private void startChannelScan() {
            	log.info("------- 开始数据业务时间分析任务: " + hourOfDay + ":" + minute + ":" + second + " ---------");
            	ChannelScanQueryHandle ChannelScanQueryHandle = new ChannelScanQueryHandle();
            	ChannelScanQueryHandle.channelScanNow();
            }
        }, new DailyIterator(hourOfDay, minute, second)); 
    }
    
    public static void stop() {
    	scheduler.cancel();
    }

//    public static void main(String[] args) {
//    	log.info("启动数据业务时间分析任务");
//    	AutoAnalysisTimeQueryTask alarmClock = new AutoAnalysisTimeQueryTask(10, 32, 0); 
//        alarmClock.start(); 
//    }
    
}
