package com.nfwork.dbfound.exception;

public class ModelNotFoundException extends DBFoundRuntimeException {
	
	private static final long serialVersionUID = 45905697084828538L;

	public ModelNotFoundException(String message) {
		super(message);
	}
}
