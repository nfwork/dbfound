package com.nfwork.dbfound.exception;

public class ExecuteNotFoundException extends DBFoundRuntimeException{
	
	private static final long serialVersionUID = 5805277358351784991L;

	public ExecuteNotFoundException(String message) {
		super(message);
	}
}
