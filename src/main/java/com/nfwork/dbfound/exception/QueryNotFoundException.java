package com.nfwork.dbfound.exception;

public class QueryNotFoundException extends DBFoundRuntimeException {
	private static final long serialVersionUID = -3052754849055302217L;

	public QueryNotFoundException(String message) {
		super(message);
	}

}
