package com.nfwork.dbfound.exception;

public class DBFoundWrappedException extends DBFoundRuntimeException {

	private static final long serialVersionUID = -2995635750158569598L;

	public DBFoundWrappedException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBFoundWrappedException(Throwable cause) {
		super(cause);
	}

}
