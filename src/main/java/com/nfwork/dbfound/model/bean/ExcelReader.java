package com.nfwork.dbfound.model.bean;

import java.io.InputStream;
import java.util.Map;

import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DataUtil;

public class ExcelReader extends SqlEntity {

	private static final long serialVersionUID = -9013279323716030635L;

	private String sourceParam;
	private String rootPath;
	private String requiredDataType;

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName){
		Param param = params.get(sourceParam);
		if (param == null) {
			throw new ParamNotFoundException("param: " + sourceParam + " not defined");
		}
		if(DataUtil.isNull(rootPath)){
			throw new DBFoundRuntimeException("rootPath can not be null");
		}
		String setPath = rootPath;
		if(!ELEngine.isAbsolutePath(setPath)){
			setPath = context.getCurrentPath() + "." + setPath;
		}

		Object ofile = param.getValue();
		if(ofile instanceof InputStream){
			try {
				Object object;
				if("map".equals(requiredDataType)){
					object = com.nfwork.dbfound.excel.ExcelReader.readExcelForMap((InputStream) ofile);
				} else {
					object = com.nfwork.dbfound.excel.ExcelReader.readExcel((InputStream) ofile);
				}
				context.setData(setPath, object);
			}catch (Exception exception){
				throw new DBFoundPackageException("excel reader failed, "+ exception.getMessage(),exception);
			}
		}else if(ofile instanceof byte[]){
			Object object;
			if("map".equals(requiredDataType)){
				object = com.nfwork.dbfound.excel.ExcelReader.readExcelForMap((byte[]) ofile);
			}else {
				object = com.nfwork.dbfound.excel.ExcelReader.readExcel((byte[]) ofile);
			}
			context.setData(setPath, object);
		}
	}

	public String getSourceParam() {
		return sourceParam;
	}

	public void setSourceParam(String sourceParam) {
		this.sourceParam = sourceParam;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getRequiredDataType() {
		return requiredDataType;
	}

	public void setRequiredDataType(String requiredDataType) {
		this.requiredDataType = requiredDataType;
	}
}
