package com.nfwork.dbfound.exception;

public class DBFoundRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6146338159798811238L;

	public DBFoundRuntimeException(String message) {
		super(message);
	}

	public DBFoundRuntimeException(Throwable cause) {
		super(cause);
	}

	public DBFoundRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
