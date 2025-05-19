package com.nfwork.dbfound.web.action;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.base.BaseController;

/**
 * 对应配置文件中的Action
 */
public class ActionBeanFactory {

	private final Map<String, BaseController> controlMap = new ConcurrentHashMap<>();

	protected BaseController getControl(String className, boolean singleton) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
		if (singleton) {
			BaseController baseControl = controlMap.get(className);
			if (baseControl == null) {
				baseControl = controlMap.get(className);
				if (baseControl == null) {
					baseControl = instance(className);
					controlMap.put(className, baseControl);
				}
			}
			return baseControl;
		} else {
			return instance(className);
		}
	}

	private BaseController instance(String className) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		Object object = Class.forName(className).getConstructor().newInstance();
		if (object instanceof BaseController) {
            return (BaseController) object;
		} else {
			throw new DBFoundRuntimeException("Control必须要实现BaseControl接口");
		}
	}
}
