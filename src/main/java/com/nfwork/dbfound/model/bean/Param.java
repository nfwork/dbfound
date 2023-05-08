package com.nfwork.dbfound.model.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.base.Entity;
import com.nfwork.dbfound.util.DataUtil;

public class Param extends Entity {

	private static final long serialVersionUID = 5538229252299018282L;

	private String name;
	private DataType dataType = DataType.VARCHAR;
	private String ioType = "in";
	private String autoSession;
	private String autoCookie;
	private Object value;
	private String sourcePath;
	private String sourcePathHistory;
	private String scope;
	private String fileSaveType = "db"; // disk ,db
	private String innerPath;
	private String fileNameParam;
	private boolean UUID = false;
	private boolean batchAssign = true;
	private boolean requireLog = false;

	@Override
	public void run() {
		if (getParent() instanceof Model) {
			Model model = (Model) getParent();
			model.getParams().put(name, this);
		} else if (getParent() instanceof Execute) {
			Execute execute = (Execute) getParent();
			execute.getParams().put(name, this);
		} else if (getParent() instanceof Query) {
			Query query = (Query) getParent();
			query.getParams().put(name, this);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getIoType() {
		return ioType;
	}

	public void setIoType(String ioType) {
		this.ioType = ioType;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Object getValue() {
		return value;
	}

	public Integer getIntValue(){
		return DataUtil.intValue(value);
	}

	public Long getLongValue(){
		return DataUtil.longValue(value);
	}

	public Float getFloatValue(){
		return DataUtil.floatValue(value);
	}

	public Double getDoubleValue(){
		return DataUtil.doubleValue(value);
	}

	public String getStringValue() {
		return getStringValue(null);
	}

	public String getStringValue(Context context) {
		if (value == null) {
			return null;
		} else {
			if(value instanceof String) {
				return (String) value;
			}else if(value instanceof  java.sql.Date){
				SimpleDateFormat dateFormat;
				if(context != null){
					dateFormat = context.getDateFormat();
				}else{
					dateFormat = new SimpleDateFormat(DBFoundConfig.getDateFormat());
				}
				return dateFormat.format(value);
			}else if (value instanceof Date) {
				SimpleDateFormat datetimeFormat;
				if(context != null){
					datetimeFormat = context.getDateTimeFormat();
				}else {
					datetimeFormat = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
				}
				return datetimeFormat.format(value);
			} else {
				return value.toString();
			}
		}
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getAutoSession() {
		return autoSession;
	}

	public void setAutoSession(String autoSession) {
		this.autoSession = autoSession;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getSourcePathHistory() {
		return sourcePathHistory;
	}

	public void setSourcePathHistory(String sourcePathHistory) {
		this.sourcePathHistory = sourcePathHistory;
	}

	public String getFileSaveType() {
		return fileSaveType;
	}

	public void setFileSaveType(String fileSaveType) {
		this.fileSaveType = fileSaveType;
	}

	public boolean isUUID() {
		return UUID;
	}

	public void setUUID(boolean UUID) {
		this.UUID = UUID;
	}

	public String getFileNameParam() {
		return fileNameParam;
	}

	public void setFileNameParam(String fileNameParam) {
		this.fileNameParam = fileNameParam;
	}

	public String getAutoCookie() {
		return autoCookie;
	}

	public void setAutoCookie(String autoCookie) {
		this.autoCookie = autoCookie;
	}

	public boolean isBatchAssign() {
		return batchAssign;
	}

	public void setBatchAssign(boolean batchAssign) {
		this.batchAssign = batchAssign;
	}

	public String getInnerPath() {
		return innerPath;
	}

	public void setInnerPath(String innerPath) {
		this.innerPath = innerPath;
	}

	public boolean isRequireLog() {
		return requireLog;
	}

	public void setRequireLog(boolean requireLog) {
		this.requireLog = requireLog;
	}
}
