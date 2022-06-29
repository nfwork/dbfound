package com.nfwork.dbfound.web;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.FileDownLoadInterrupt;

public class WebExceptionHandle {

	public static void handle(Exception exception, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (exception instanceof SocketException
					|| exception.getCause() instanceof SocketException) {
				LogUtil.warn("client socket exception:" + exception.getMessage());
				return;
			}
			if (exception instanceof FileDownLoadInterrupt) {
				LogUtil.warn(exception.getMessage());
				return;
			}

			exception = getException(exception);

			String em = exception.getMessage();
			if(exception instanceof CollisionException){
				LogUtil.info(exception.getClass().getName() + ":" + em);
			} else {
				em = exception.getClass().getName() + ":" + em;
				LogUtil.error(em, exception);
			}
			ResponseObject ro = new ResponseObject();
			ro.setSuccess(false);
			ro.setMessage(em);
			WebWriter.jsonWriter(response, JsonUtil.beanToJson(ro));
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	private static Exception getException(Exception exception) {
		if (exception instanceof DBFoundPackageException) {
			DBFoundPackageException pkgException = (DBFoundPackageException) exception;
			if (pkgException.getMessage() != null) {
				return pkgException;
			}
			Throwable throwable = exception.getCause();
			if (throwable != null && throwable instanceof Exception) {
				return (Exception) throwable;
			}
		} else if (exception instanceof InvocationTargetException) {
			Throwable throwable = exception.getCause();
			if (throwable != null && throwable instanceof Exception) {
				return (Exception) throwable;
			}
		}
		return exception;
	}

}
