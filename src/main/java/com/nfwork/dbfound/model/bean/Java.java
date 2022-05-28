package com.nfwork.dbfound.model.bean;

import java.util.Map;
import org.apache.commons.beanutils.MethodUtils;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.JavaSupport;
import com.nfwork.dbfound.model.base.ContextAware;
import com.nfwork.dbfound.model.base.ParamsAware;
import com.nfwork.dbfound.model.base.ProvideNameAware;
import com.nfwork.dbfound.util.LogUtil;

public class Java extends SqlEntity {

	private static final long serialVersionUID = -8978726798343582780L;

	private String className;

	private String method;

	@Override
	public void run() {
		super.run();
		if (method == null || "".equals(method)) {
			method = "execute";
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context context, Map<String, Param> params,
			String provideName) {
		LogUtil.info("execute Java plugin：" + className + ", method：" + method);
		try {
			Class executeClass = Class.forName(className);
			Object object = executeClass.newInstance();
			
			if (object instanceof JavaSupport) {
				MethodUtils.invokeMethod(object, "setParams",
						new Object[] { params });
				MethodUtils.invokeMethod(object, "setContext",
						new Object[] { context });
				MethodUtils.invokeMethod(object, "setProvideName",
						new Object[] { provideName });
			} else if (executeClass.getInterfaces().length > 0) {
				if (object instanceof ParamsAware) {
					MethodUtils.invokeMethod(object, "setParams",
							new Object[] { params });
				}
				if (object instanceof ContextAware) {
					MethodUtils.invokeMethod(object, "setContext",
							new Object[] { context });
				}
				if (object instanceof ProvideNameAware) {
					MethodUtils.invokeMethod(object, "setProvideName",
							new Object[] { provideName });
				}
			}
			MethodUtils.invokeMethod(object, method, new Object[] {});
		} catch (ClassNotFoundException e) {
			throw new DBFoundRuntimeException("class:" + className + " not found");
		} catch (Exception ee) {
			Throwable throwable = ee.getCause();
			if (throwable != null && throwable instanceof Exception) {
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
