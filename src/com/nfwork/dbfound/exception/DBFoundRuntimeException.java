package com.nfwork.dbfound.exception;

public class DBFoundRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6146338159798811238L;

	public DBFoundRuntimeException(String message) {
		super(message);
	}

	public DBFoundRuntimeException(Exception e) {
		super(e);
	}

	public DBFoundRuntimeException(String message, Exception e) {
		super(message, e);
	}

}
