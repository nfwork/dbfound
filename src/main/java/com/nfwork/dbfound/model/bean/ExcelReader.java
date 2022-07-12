package com.nfwork.dbfound.model.bean;

import java.io.InputStream;
import java.util.List;
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
		Object ofile = param.getValue();
		if(ofile instanceof InputStream){
			try {
				InputStream inputStream = (InputStream) ofile;
				List<List<Map>> dataList = com.nfwork.dbfound.excel.ExcelReader.readExcel(inputStream);
				String setPath = rootPath;
				if(!ELEngine.isAbsolutePath(setPath)){
					setPath = context.getCurrentPath() + "." + setPath;
				}
				context.setData(setPath, dataList);
			}catch (Exception exception){
				throw new DBFoundPackageException("excel reader failed, "+ exception.getMessage(),exception);
			}
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

}
