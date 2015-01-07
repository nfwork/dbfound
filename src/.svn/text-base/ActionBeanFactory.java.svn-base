package com.nfwork.dbfound.web;

import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.base.BaseControl;

/**
 * 对应配置文件中的Action
 */
public class ActionBeanFactory {

	private static Map<String, BaseControl> controlMap = new HashMap<String, BaseControl>();

	public static BaseControl getControl(String className, boolean singleton) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
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
			ClassNotFoundException {
		Object object = Class.forName(className).newInstance();
		if (object instanceof BaseControl) {
			BaseControl baseControl = (BaseControl) object;
			return baseControl;
		} else {
			throw new DBFoundRuntimeException("Control必须要实现BaseControl接口");
		}
	}
}
