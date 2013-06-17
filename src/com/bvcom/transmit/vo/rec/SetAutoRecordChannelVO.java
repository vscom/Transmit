package com.bvcom.transmit.vo.rec;

import com.bvcom.transmit.vo.MSGHeadVO;
//�Զ�¼��
public class SetAutoRecordChannelVO {
	
		private MSGHeadVO MSGHead = new MSGHeadVO();

	    private String Action;
	    
	    private String CodingFormat;
	    private String Width;
	    private String Height;
	    private String Fps;
	    private String Bps;
	     
	    
	    private int Index = 0;
	    
	    private int DevIndex = 0;
	    
	    private int DownIndex = 0;
	    
	    /**
	     * TSC���͵�ͨ����
	     */
	    private int TscIndex = 0;
	    /**
	     * IPM���͵�ͨ����
	     */
	    private int IpmIndex = 0;
	    
	    private String ProgramName;
	    
	    private int Freq = 0;

	    private int SymbolRate;

	    private int QAM;

	    private int ServiceID;

	    private int Pcr_PID;

	    private int VideoPID;

	    private int AudioPID;

	    private int ReutnValue;
	    
	    private int HDFlag;
	    
	    private String udp;
	    
	    private int port;
	    
	    private String smgURL;
	    
	    private int RecordType; //  0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��)	3: ����¼��
	    
	    private String Comment;//ʧ��ԭ��

		public String getComment() {
			return Comment;
		}

		public void setComment(String comment) {
			Comment = comment;
		}

		public MSGHeadVO getMSGHead() {
			return MSGHead;
		}

		public void setMSGHead(MSGHeadVO head) {
			MSGHead = head;
		}

		public String getAction() {
			return Action;
		}

		public void setAction(String action) {
			Action = action;
		}

		public int getIndex() {
			return Index;
		}

		public void setIndex(int index) {
			Index = index;
		}

		public int getFreq() {
			return Freq;
		}

		public void setFreq(int freq) {
			Freq = freq;
		}

		public int getSymbolRate() {
			return SymbolRate;
		}

		public void setSymbolRate(int symbolRate) {
			SymbolRate = symbolRate;
		}

		public int getQAM() {
			return QAM;
		}

		public void setQAM(int qam) {
			QAM = qam;
		}

		public int getServiceID() {
			return ServiceID;
		}

		public void setServiceID(int serviceID) {
			ServiceID = serviceID;
		}

		public int getPcr_PID() {
			return Pcr_PID;
		}

		public void setPcr_PID(int pcr_PID) {
			Pcr_PID = pcr_PID;
		}

		public int getVideoPID() {
			return VideoPID;
		}

		public void setVideoPID(int videoPID) {
			VideoPID = videoPID;
		}

		public int getAudioPID() {
			return AudioPID;
		}

		public void setAudioPID(int audioPID) {
			AudioPID = audioPID;
		}

		public int getReutnValue() {
			return ReutnValue;
		}

		public void setReutnValue(int reutnValue) {
			ReutnValue = reutnValue;
		}

		public int getHDFlag() {
			return HDFlag;
		}

		public void setHDFlag(int flag) {
			HDFlag = flag;
		}

		public int getDevIndex() {
			return DevIndex;
		}

		public void setDevIndex(int devIndex) {
			DevIndex = devIndex;
		}

		public int getDownIndex() {
			return DownIndex;
		}

		public void setDownIndex(int downIndex) {
			DownIndex = downIndex;
		}

		/**
		 * 0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��)	3: ����¼��  4: �����˺ϳ��ֲ�
		 * Type: 4: �����˺ϳ��ֲ� add By: Bian Jiang 2012.3.21
		 * @return
		 */
		public int getRecordType() {
			return RecordType;
		}

		/**
		 * 0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��)	3: ����¼��  4: �����˺ϳ��ֲ�
		 * Type: 4: �����˺ϳ��ֲ� add By: Bian Jiang 2012.3.21
		 * @param recordType
		 */
		public void setRecordType(int recordType) {
			RecordType = recordType;
		}

		public String getUdp() {
			return udp;
		}

		public void setUdp(String udp) {
			this.udp = udp;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getSmgURL() {
			return smgURL;
		}

		public void setSmgURL(String smgURL) {
			this.smgURL = smgURL;
		}

		public String getProgramName() {
			return ProgramName;
		}

		public void setProgramName(String programName) {
			ProgramName = programName;
		}

		/**
		 * TSC���͵�ͨ����
		 * @return
		 */
		public int getTscIndex() {
			return TscIndex;
		}

		/**
		 * TSC���͵�ͨ����
		 * @param tscIndex
		 */
		public void setTscIndex(int tscIndex) {
			TscIndex = tscIndex;
		}

		public int getIpmIndex() {
			return IpmIndex;
		}

		public void setIpmIndex(int ipmIndex) {
			IpmIndex = ipmIndex;
		}

		public String getCodingFormat() {
			return CodingFormat;
		}

		public void setCodingFormat(String codingFormat) {
			CodingFormat = codingFormat;
		}

		public String getWidth() {
			return Width;
		}

		public void setWidth(String width) {
			Width = width;
		}

		public String getHeight() {
			return Height;
		}

		public void setHeight(String height) {
			Height = height;
		}

		public String getFps() {
			return Fps;
		}

		public void setFps(String fps) {
			Fps = fps;
		}

		public String getBps() {
			return Bps;
		}

		public void setBps(String bps) {
			Bps = bps;
		}

}
