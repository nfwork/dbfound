package com.nfwork.dbfound.util;

import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
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

	public static void log(String sqlName, String sql, Collection<Param> params , List<Object> exeParam) {
		if (DBFoundConfig.isOpenLog()) {
			sqlLog("Execute " + sqlName + ": ", sql, exeParam);
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

	public static void sqlLog(String comment, String sql, List<Object> exeParam){
		if (DBFoundConfig.isOpenLog()) {
			if(!DBFoundConfig.isLogWithParamSql() || exeParam.isEmpty()){
				log.info(comment + sql);
				return;
			}
			char [] chars = sql.toCharArray();
			int dyh = 0;
			int syh = 0;

			int paramIndex = 0;
			StringBuilder buffer = new StringBuilder();
			for(int i=0; i< chars.length; i++){
				if (chars[i] == '\'' && (i==0 || chars[i-1] != '\\') && syh==0) {
					dyh = dyh ^ 1;
					buffer.append(chars[i]);
				}else if (chars[i] == '\"' && (i==0 || chars[i-1] != '\\') && dyh==0) {
					syh = syh ^ 1;
					buffer.append(chars[i]);
				}else if (dyh == 0 && syh ==0) {
					if(chars[i] == '?'){
						Object value = exeParam.get(paramIndex++);
						if(value == null){
							buffer.append("null");
						}else if (value instanceof Number){
							buffer.append(value);
						}else if(value instanceof String){
							String sValue = (String) value;
							if(sValue.contains("'")){
								sValue = sValue.replace("'","\\'");
							}
							buffer.append("'").append(sValue).append("'");
						} else if (value instanceof Date) {
							buffer.append("'").append(LocalDateUtil.formatDate((Date) value)).append("'");
						} else if (value instanceof Temporal) {
							buffer.append("'").append(LocalDateUtil.formatTemporal((Temporal) value)).append("'");
						} else if(value instanceof Boolean){
							buffer.append(value);
						} else{
							buffer.append("?");
						}
					}else {
						buffer.append(chars[i]);
					}
				}
			}
			log.info(buffer.toString());
		}
	}

	public static void info(String message) {
		if (DBFoundConfig.isOpenLog()) {
			log.info(message);
		}
	}

	public static void warn(String message) {
		if (DBFoundConfig.isOpenLog()) {
			log.warn(message);
		}
	}

	public static void error(String message, Throwable throwable) {
		log.error(message, throwable);
	}

}
