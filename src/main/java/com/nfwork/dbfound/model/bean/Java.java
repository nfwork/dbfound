package com.nfwork.dbfound.model.bean;

import java.util.Map;

import com.nfwork.dbfound.model.reflector.Reflector;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.JavaSupport;
import com.nfwork.dbfound.model.base.ContextAware;
import com.nfwork.dbfound.model.base.ParamsAware;
import com.nfwork.dbfound.model.base.ProvideNameAware;
import com.nfwork.dbfound.util.LogUtil;

public class Java extends SqlEntity {
	private String className;

	private String method;

	@Override
	public void doEndTag() {
		super.doEndTag();
		if (method == null || method.isEmpty()) {
			method = "execute";
		}
	}

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName) {
		LogUtil.info("execute Java plugin: " + className + ", method: " + method);
		try {
			Class<?> executeClass = Class.forName(className);
			Object object = executeClass.getConstructor().newInstance();

			Reflector reflector = Reflector.forClass(executeClass);

			if (object instanceof JavaSupport) {
				reflector.setProperty(object,"params", params);
				reflector.setProperty(object,"context", context);
				reflector.setProperty(object,"provideName", provideName);
			} else if (executeClass.getInterfaces().length > 0) {
				if (object instanceof ParamsAware) {
					reflector.setProperty(object,"params", params);
				}
				if (object instanceof ContextAware) {
					reflector.setProperty(object,"context", context);
				}
				if (object instanceof ProvideNameAware) {
					reflector.setProperty(object,"provideName", provideName);
				}
			}
			reflector.getMethodInvoker(method).invoke(object, new Object[] {});
		} catch (ClassNotFoundException e) {
			throw new DBFoundRuntimeException("class:" + className + " not found");
		} catch (Exception ee) {
			Throwable throwable = ee.getCause();
			if (throwable instanceof Exception) {
				if (throwable instanceof RuntimeException) {
					throw (RuntimeException)throwable;
				}
				throw new DBFoundPackageException((Exception) throwable);
			}
			throw new DBFoundPackageException(ee);
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
