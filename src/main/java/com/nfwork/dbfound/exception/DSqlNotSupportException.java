package com.nfwork.dbfound.exception;

public class DSqlNotSupportException extends DBFoundRuntimeException{

	String message;

	public DSqlNotSupportException() {
		super("");
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
