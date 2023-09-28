package com.nfwork.dbfound.web;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.reflector.Reflector;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.base.BaseControl;

import java.lang.reflect.InvocationTargetException;

/**
 * 反射机制，动态的调用方法
 */
public class ActionReflect {

	public static void reflect(Context context, String className, String method, boolean singleton) {
		
		//开启事务
		Transaction transaction = context.getTransaction();
		transaction.begin();

		try {
			BaseControl baseControl = ActionBeanFactory.getControl(className, singleton);
			Reflector reflector = Reflector.forClass(baseControl.getClass());
			Object result = reflector.getMethodInvoker(method).invoke(baseControl, new Object[] {context});

			if (result != null) {
				if (result instanceof ResponseObject) {
					// 提交关闭事务
					transaction.commit();
					transaction.end();

					WebWriter.jsonWriter(context.response, JsonUtil.toJson(result));
				} else {
					throw new DBFoundRuntimeException("return object must extends ResponseObject");
				}
			}
		}catch (Exception e){
			Throwable throwable = e.getCause();
			if(throwable instanceof DBFoundRuntimeException){
				throw (DBFoundRuntimeException)throwable;
			}
			if(e instanceof InvocationTargetException){
				if(throwable instanceof Exception) {
					throw new DBFoundPackageException("ActionReflect execute failed, " + e.getMessage(), (Exception) throwable);
				}
			}
			throw new DBFoundPackageException("ActionReflect execute failed, "+e.getMessage(), e);
		}
	}
}
