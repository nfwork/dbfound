package com.nfwork.dbfound.web.action;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.reflector.MethodInvoker;
import com.nfwork.dbfound.model.reflector.Reflector;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.base.ActionController;
import com.nfwork.dbfound.web.base.ActionTransactional;
import com.nfwork.dbfound.web.base.BaseControl;

import java.lang.reflect.InvocationTargetException;

/**
 * 反射机制，动态的调用方法
 */
public class ActionReflect {

	private final ActionBeanFactory actionBeanFactory = new ActionBeanFactory();

	public ResponseObject reflect(Context context, String className, String method, boolean singleton) throws Exception {
		try {
			BaseControl baseControl = actionBeanFactory.getControl(className, singleton);

			Reflector reflector = Reflector.forClass(baseControl.getClass());
			MethodInvoker methodInvoker = reflector.getMethodInvoker(method, Context.class);

			boolean requireTransaction = true;
			if (baseControl instanceof ActionController){
				ActionTransactional transactional = methodInvoker.getMethod().getAnnotation(ActionTransactional.class);
				if(transactional == null){
					requireTransaction = false;
				}else{
					if(transactional.isolation() != null) {
						context.getTransaction().setTransactionIsolation(transactional.isolation().getValue());
					}
				}
			}

			if(requireTransaction) {
				context.getTransaction().begin();
			}
			Object result = methodInvoker.invoke(baseControl, new Object[] {context});
			if (result != null) {
				if (result instanceof ResponseObject) {
					context.getTransaction().commit();
					return (ResponseObject) result;
				} else {
					throw new DBFoundRuntimeException("return object must extends ResponseObject");
				}
			}
			return null;
		}catch (Throwable e){
			context.getTransaction().rollback();
			Throwable throwable = e.getCause();
			if(throwable instanceof DBFoundRuntimeException){
				throw (DBFoundRuntimeException)throwable;
			}
			if(e instanceof InvocationTargetException){
				if(throwable instanceof Exception) {
					throw new DBFoundPackageException("ActionReflect execute failed, " + e.getMessage(), (Exception) throwable);
				}
			}
			if(e instanceof Exception) {
				throw new DBFoundPackageException("ActionReflect execute failed, " + e.getMessage(), (Exception) e);
			}else{
				throw e;
			}
		}finally {
			context.getTransaction().end();
		}
	}
}
