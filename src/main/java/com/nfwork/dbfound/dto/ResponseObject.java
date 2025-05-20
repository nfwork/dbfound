package com.nfwork.dbfound.dto;

import java.util.Map;

public class ResponseObject {

	private boolean success;

	private String message;

	private String code;
	
	private Map<String,Object> outParam;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Map<String, Object> getOutParam() {
		return outParam;
	}

	public <T> T getOutParam(String paramName){
		if (outParam == null) {
			return null;
		}
		return (T) outParam.get(paramName);
	}

	public void setOutParam(Map<String, Object> outParam) {
		this.outParam = outParam;
	}

}
