package com.nfwork.dbfound.util;

import java.util.Collection;
import java.util.List;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.model.bean.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志处理
 * 
 * @author John
 * 
 */
public class LogUtil {

	private static final Logger log = LoggerFactory.getLogger("dbfound");

	public static void logSql(String sqlName, String sql, Collection<Param> params , List<Object> exeParam) {
		if (DBFoundConfig.isOpenLog()) {
			if(DBFoundConfig.isLogWithParamSql() && !exeParam.isEmpty()){
				sql = StringUtil.getParamSql(sql, exeParam);
			}
			log.info("Execute " + sqlName + ": " + sql);
			params.stream().filter(Param::isRequireLog).forEach(param -> {
				param.setRequireLog(false);
				log.info("  paramName: "+ param.getName() +", value: "+ getValue(param) +
						", dataType: "+ param.getDataType().getValue() +", sourcePath: " + param.getSourcePathHistory());
			});
		}
	}

	private static String getValue(Param param){
		if(DBFoundConfig.getSensitiveParamSet().contains(param.getName())){
			return "********";
		}else{
			return  param.getStringValue();
		}
	}

	public static void debug(String message) {
		if (DBFoundConfig.isOpenLog()) {
			log.debug(message);
		}
	}

	public static void logCountSql(String sql, List<Object> exeParam){
		if (DBFoundConfig.isOpenLog()) {
			if(DBFoundConfig.isLogWithParamSql() && !exeParam.isEmpty()){
				sql = StringUtil.getParamSql(sql, exeParam);
			}
			log.info("Execute countSql: " + sql);
		}
	}

	public static void info(String message) {
		if (DBFoundConfig.isOpenLog()) {
			log.info(message);
		}
	}

	public static void warn(String message) {
		log.warn(message);
	}

	public static void error(String message, Throwable throwable) {
		log.error(message, throwable);
	}

}
