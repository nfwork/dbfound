package com.nfwork.dbfound.exception;

public class CollisionException extends DBFoundRuntimeException {

	private static final long serialVersionUID = -3076254639898621459L;

	private String code;

	public CollisionException(String message) {
		super(message);
	}

	public CollisionException(String message, String code){
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
