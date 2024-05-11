package com.nfwork.dbfound.web.action;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.base.BaseControl;

/**
 * 对应配置文件中的Action
 */
public class ActionBeanFactory {

	private final static Map<String, BaseControl> controlMap = new HashMap<>();

	protected static BaseControl getControl(String className, boolean singleton) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
		if (singleton) {
			BaseControl baseControl = controlMap.get(className);
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

	private static BaseControl instance(String className) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		Object object = Class.forName(className).getConstructor().newInstance();
		if (object instanceof BaseControl) {
            return (BaseControl) object;
		} else {
			throw new DBFoundRuntimeException("Control必须要实现BaseControl接口");
		}
	}
}
