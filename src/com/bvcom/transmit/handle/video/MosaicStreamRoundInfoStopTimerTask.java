package com.bvcom.transmit.handle.video;

import java.util.TimerTask;

import org.apache.log4j.Logger;


public class MosaicStreamRoundInfoStopTimerTask extends TimerTask {

	static Logger log = Logger.getLogger(MosaicStreamRoundInfoStopTimerTask.class.getSimpleName());
	@Override
	public void run() {
		log.info("------- ��ʼ�����������ֲ� ֹͣ����-------");
		
		MosaicStreamRoundInfoStopThread stopThread=new MosaicStreamRoundInfoStopThread();
		stopThread.start();
	}

}
