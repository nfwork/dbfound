package com.nfwork.dbfound.util;

import java.util.Collection;
import java.util.Comparator;

import com.nfwork.dbfound.core.DBFoundConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nfwork.dbfound.model.bean.Param;

/**
 * 日志处理
 * 
 * @author John
 * 
 */
public class LogUtil {

	private static final Log log = LogFactory.getLog("dbfound");

	public static void log(String sqlName, String sql, Collection<Param> params) {
		if (DBFoundConfig.isOpenLog()) {
			log.info("Execute " + sqlName + ": "+sql);
			params.stream().filter(Param::isRequireLog).sorted(Comparator.comparing(Param::getName)).forEach(param -> {
				param.setRequireLog(false);
				log.info("  paramName: "+ param.getName() +", value: "+ param.getStringValue() +
						", dataType: "+ param.getDataType().getValue() +", sourcePath: " + param.getSourcePathHistory());
			});
		}
	}

	public static void debug(String message) {
		log.debug(message);
	}

	public static void info(String message) {
		log.info(message);
	}

	public static void warn(String message) {
		log.warn(message);
	}

	public static void error(String message, Throwable throwable) {
		log.error(message, throwable);
	}

}
