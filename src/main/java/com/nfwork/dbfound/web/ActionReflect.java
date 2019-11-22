package com.nfwork.dbfound.web;

import org.apache.commons.beanutils.MethodUtils;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.WebWriter;
import com.nfwork.dbfound.web.base.BaseControl;

/**
 * 反射机制，动态的调用方法
 */
public class ActionReflect {

	public static void reflect(Context context, String className, String method, boolean singleton) throws Exception {
		
		//开启事务
		Transaction transaction = context.getTransaction();
		transaction.begin();
		
		BaseControl baseControl = ActionBeanFactory.getControl(className,singleton);
		Object result = MethodUtils.invokeMethod(baseControl, method, new Object[] { context });
		
		if (result != null) {
			if (result instanceof ResponseObject) {
				// 提交关闭事务
				transaction.commitAndEnd();
				
				WebWriter.jsonWriter(context.response, JsonUtil.beanToJson(result));
			} else {
				throw new DBFoundRuntimeException("return object must extends ResponseObject");
			}
		}
	}
}
