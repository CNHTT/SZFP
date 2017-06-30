package com.hiklife.rfidapi;

import java.util.EventObject;

public class MacErrorEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private MacErrorInfo errorInfo;
	
	public MacErrorEvent(Object source , MacErrorInfo date){
		super(source);
		this.errorInfo = date;
	}
	
	public long GetErrorInfo(){
		return errorInfo.macError;
	}
}
