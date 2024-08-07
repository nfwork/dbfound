package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.util.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class WebExceptionHandler {

	public ResponseObject handle(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		String em = exception.getMessage();
		String code = null;
		if(exception instanceof CollisionException){
			code = ((CollisionException) exception).getCode();
			LogUtil.info(exception.getClass().getName() + ": " + em);
		} else {
			String message = "an exception: "+exception.getClass().getName()+" caused, when request url: "+request.getRequestURI();
			LogUtil.error(message, exception);
			if(exception.getCause() instanceof SQLException){
				em = exception.getCause().getMessage();
			}
			em =  exception.getClass().getName() +": " + em;
		}
		ResponseObject ro = new ResponseObject();
		ro.setSuccess(false);
		ro.setCode(code);
		ro.setMessage(em);
		return ro;
	}
}
