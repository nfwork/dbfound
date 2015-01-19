package com.nfwork.dbfound.exception;

public class DBFoundRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6146338159798811238L;
	
	private String message;

	public DBFoundRuntimeException(String message) {
		super();
		this.message = message;
	}
	
	 DBFoundRuntimeException(Exception e) {
		super(e);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
