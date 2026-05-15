package com.nfwork.dbfound.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundWrappedException;
import com.nfwork.dbfound.web.base.WebExceptionHandler;

public final class ExceptionHandlerFacade {

	private static WebExceptionHandler exceptionHandler = new WebExceptionHandler();

	public static void initExceptionHandler(DBFoundInitToken dbfoundInitToken, String name){
		DBFoundConfig.checkInitToken(dbfoundInitToken);
		try {
			exceptionHandler = (WebExceptionHandler) Class.forName(name).getConstructor().newInstance();
		} catch (Exception e) {
			throw new DBFoundRuntimeException("init exceptionHandler failed, "+ e.getMessage(),e);
		}
	}

	public static void handle(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
		try {
			throwable = unwrapException(throwable);
			ResponseObject ro = exceptionHandler.handle(throwable, request, response);
			WebWriter.jsonWriter(response, JsonUtil.toJson(ro));
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	private static Throwable unwrapException(Throwable throwable) {
		if (throwable instanceof DBFoundWrappedException) {
			Throwable cause = throwable.getCause();
			if (cause != null) {
				return cause;
			}
		}
		return throwable;
	}

}
