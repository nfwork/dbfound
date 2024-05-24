package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.util.LogUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebExceptionHandler {

	public ResponseObject handle(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		String em = exception.getMessage();
		String code = null;
		if(exception instanceof CollisionException){
			code = ((CollisionException) exception).getCode();
			LogUtil.info(exception.getClass().getName() + ": " + em);
		} else {
			em = exception.getClass().getName() + ": " + em;
			LogUtil.error(em, exception);
		}
		ResponseObject ro = new ResponseObject();
		ro.setSuccess(false);
		ro.setCode(code);
		ro.setMessage(em);
		return ro;
	}
}
