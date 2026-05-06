package com.nfwork.dbfound.exception;

public class DSqlUnsupportedException extends DBFoundRuntimeException {

	public DSqlUnsupportedException() {
		super("dynamic sql expression is unsupported");
	}

	public DSqlUnsupportedException(String message) {
		super(message);
	}
}
