package com.nfwork.dbfound.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.web.base.WebExceptionHandler;

public final class ExceptionHandlerFacade {

	private static WebExceptionHandler exceptionHandler = new WebExceptionHandler();

	public static void initExceptionHandler(String name){
		try {
			exceptionHandler = (WebExceptionHandler) Class.forName(name).getConstructor().newInstance();
		} catch (Exception e) {
			throw new DBFoundRuntimeException("init exceptionHandler failed, "+ e.getMessage(),e);
		}
	}

	public static void handle(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		try {
			exception = getException(exception);
			ResponseObject ro = exceptionHandler.handle(exception, request, response);
			WebWriter.jsonWriter(response, JsonUtil.toJson(ro));
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	private static Exception getException(Exception exception) {
		if (exception instanceof DBFoundPackageException) {
			Throwable throwable = exception.getCause();
			if (throwable instanceof Exception) {
				return (Exception) throwable;
			}
		}
		return exception;
	}

}
