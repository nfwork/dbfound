package com.nfwork.dbfound.exception;

public class DBFoundErrorException extends RuntimeException {

	private static final long serialVersionUID = 6146338159798811238L;

	public DBFoundErrorException(String message, Throwable e) {
		super(message, e);
	}

}
