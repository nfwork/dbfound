package com.nfwork.dbfound.model.bean;

import java.time.temporal.Temporal;
import java.util.Date;

import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.base.Entity;
import com.nfwork.dbfound.model.base.IOType;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LocalDateUtil;

public class Param extends Entity {

	private static final long serialVersionUID = 5538229252299018282L;

	private String name = "";
	private DataType dataType = DataType.VARCHAR;
	private IOType ioType = IOType.IN;
	private boolean autoSession = false;
	private boolean autoCookie = false;
	private Object value;
	private String sourcePath;
	private String sourcePathHistory;
	private String scope;
	private String innerPath;
	private String fileNameParam;
	private boolean UUID = false;
	private boolean batchAssign = true;
	private boolean requireLog = false;

	private boolean emptyAsNull = true;

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

	public IOType getIoType() {
		return ioType;
	}

	public void setIoType(IOType ioType) {
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
		if (value == null) {
			return null;
		} else {
			if(value instanceof String) {
				return (String) value;
			} else if (value instanceof Date) {
				return LocalDateUtil.formatDate((Date) value);
			} else if (value instanceof Temporal) {
				return LocalDateUtil.formatTemporal((Temporal) value);
			} else {
				return value.toString();
			}
		}
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isAutoSession() {
		return autoSession;
	}

	public void setAutoSession(boolean autoSession) {
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

	public boolean isAutoCookie() {
		return autoCookie;
	}

	public void setAutoCookie(boolean autoCookie) {
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

	public boolean isEmptyAsNull() {
		if(this.dataType == DataType.VARCHAR) {
			return emptyAsNull;
		}else{
			return true;
		}
	}

	public void setEmptyAsNull(boolean emptyAsNull) {
		this.emptyAsNull = emptyAsNull;
	}
}
