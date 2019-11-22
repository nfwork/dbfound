package com.nfwork.dbfound.util;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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

	public static boolean openLog = true;

	private static Log log = LogFactory.getLog("dbfound");

	public static void log(String sql, Map<String, Param> params) {
		if (openLog) {
			log.info("execute sql: " + sql);
			Set<Entry<String, Param>> setp = params.entrySet();
			for (Entry<String, Param> entry : setp) {
				Param param = entry.getValue();
				log.info(String.format("sql param:%s, value:%s, dataType:%s, sourcePath:%s", param.getName(), param
						.getValue(), param.getDataType(), param.getSourcePathHistory()));
			}
		}
	}

	public static void debug(String message) {
		if (openLog) {
			log.debug(message);
		}
	}

	public static void info(String message) {
		if (openLog) {
			log.info(message);
		}
	}

	public static void warn(String message) {
		if (openLog) {
			log.warn(message);
		}
	}

	public static void error(String message, Throwable throwable) {
		log.error(message, throwable);
	}

	public void openLog() {
		openLog = true;
	}

	public void closeLog() {
		openLog = false;
	}

	public static boolean isOpenLog() {
		return openLog;
	}

	public static void setOpenLog(boolean openLog) {
		LogUtil.openLog = openLog;
	}

}
