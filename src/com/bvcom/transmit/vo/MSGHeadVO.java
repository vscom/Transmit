/**
 * controlAlarm (java转发)
 * 
 * BaseXMLData.java    2007.8.15
 * 
 * Copyright 2007 Dautoit. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.vo;

import org.dom4j.Document;

/**
 * 
 * XML数据对象
 * 
 * @version  V1.0
 * @author   边 江
 * Date 2007.8.15
 */

public class MSGHeadVO {
    
	private Document document = null;
	// XML头部<Msg>属性信息
	private String version = null;
	private String centerMsgID = null;
	private String type = null;
	private String dateTime = null;
	private String srcCode = null;
	private String dstCode = null;
	private String srcURL = null;
	// 实体信息
	private String StatusQueryType = null;
	private String ErrTypeStr = null;
	
	private String SystemType = null;
	// 命令优先级
	private String Priority = null;
	private long 	    XMLLen = 0;

	private String ReplyID;
	
	private String Return_Type;
	
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getDstCode() {
		return dstCode;
	}

	public void setDstCode(String dstCode) {
		this.dstCode = dstCode;
	}

	public String getErrTypeStr() {
		return ErrTypeStr;
	}

	public void setErrTypeStr(String errTypeStr) {
		ErrTypeStr = errTypeStr;
	}

	public String getCenterMsgID() {
		return centerMsgID;
	}

	public void setCenterMsgID(String msgID) {
		this.centerMsgID = msgID;
	}

	public String getSrcCode() {
		return srcCode;
	}

	public void setSrcCode(String srcCode) {
		this.srcCode = srcCode;
	}

	public String getSrcURL() {
		return srcURL;
	}

	public void setSrcURL(String srcURL) {
		this.srcURL = srcURL;
	}

	public String getStatusQueryType() {
		return StatusQueryType;
	}

	public void setStatusQueryType(String statusQueryType) {
		StatusQueryType = statusQueryType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getXMLLen() {
		return XMLLen;
	}

	public void setXMLLen(long len) {
		XMLLen = len;
	}

	public String getSystemType() {
		return SystemType;
	}

	public void setSystemType(String systemType) {
		SystemType = systemType;
	}

	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

	public String getReplyID() {
		return ReplyID;
	}

	public void setReplyID(String replyID) {
		ReplyID = replyID;
	}

	public String getReturn_Type() {
		return Return_Type;
	}

	public void setReturn_Type(String return_Type) {
		Return_Type = return_Type;
	}
	
}
