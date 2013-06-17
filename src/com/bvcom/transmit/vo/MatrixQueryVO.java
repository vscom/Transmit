package com.bvcom.transmit.vo;
//æÿ’Û«–ªª
public class MatrixQueryVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

    private int Index1;
    
    private int Index2;
    
    private int Type;
    
    private int ReuturnValue;
    
    private String Comment;// ß∞‹‘≠“Ú

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getIndex1() {
		return Index1;
	}

	public void setIndex1(int index1) {
		Index1 = index1;
	}

	public int getIndex2() {
		return Index2;
	}

	public void setIndex2(int index2) {
		Index2 = index2;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getReuturnValue() {
		return ReuturnValue;
	}

	public void setReuturnValue(int reuturnValue) {
		ReuturnValue = reuturnValue;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

}
