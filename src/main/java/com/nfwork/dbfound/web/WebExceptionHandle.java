package com.nfwork.dbfound.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.FileDownLoadInterrupt;

public class WebExceptionHandle {

	public static void handle(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		try {
			exception = getException(exception);

			if (exception instanceof FileDownLoadInterrupt) {
				LogUtil.warn(exception.getMessage());
				return;
			}

			String em = exception.getMessage();
			String code = null;
			if(exception instanceof CollisionException){
				code = ((CollisionException) exception).getCode();
				LogUtil.info(exception.getClass().getName() + ":" + em);
			} else {
				em = exception.getClass().getName() + ":" + em;
				LogUtil.error(em, exception);
			}
			ResponseObject ro = new ResponseObject();
			ro.setSuccess(false);
			ro.setCode(code);
			ro.setMessage(em);
			WebWriter.jsonWriter(response, JsonUtil.beanToJson(ro));
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
