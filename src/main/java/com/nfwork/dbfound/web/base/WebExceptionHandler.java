package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.util.LogUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class WebExceptionHandler {

	public ResponseObject handle(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
		String em = throwable.getMessage();
		String code = null;
		if(throwable instanceof CollisionException){
			response.setStatus(422);
			code = ((CollisionException) throwable).getCode();
			LogUtil.info(throwable.getClass().getName() + ": " + em);
		} else {
			response.setStatus(500);
			String message = "an exception: "+throwable.getClass().getName()+" caused, when request url: "+request.getRequestURI();
			LogUtil.error(message, throwable);
			if(throwable.getCause() instanceof SQLException){
				em = throwable.getCause().getMessage();
			}
			em =  throwable.getClass().getName() +": " + em;
		}
		ResponseObject ro = new ResponseObject();
		ro.setSuccess(false);
		ro.setCode(code);
		ro.setMessage(em);
		return ro;
	}
}
