package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import com.nfwork.dbfound.el.ELEngine;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.StreamUtils;
import com.nfwork.dbfound.web.file.FilePart;

public class ExcelReader extends SqlEntity {
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
		String type = "_default";
		String sourcePath = param.getSourcePathHistory();
		if(sourcePath.endsWith(".content") && !sourcePath.equals("param.content")){
			sourcePath  = sourcePath.substring(0,sourcePath.length()-8) ;
		}
		Object obj = context.getData(sourcePath);
		if(obj instanceof FilePart){
			FilePart filePart = (FilePart) obj;
			type = com.nfwork.dbfound.excel.ExcelReader.getType(filePart);
		}else if(sourcePath.toLowerCase().endsWith(".csv")){
			type = "csv";
		}

		initParamValue(param);
		initParamType(param);

		Object ofile = param.getValue();

		if(ofile instanceof byte[]){
			Object object;
			if("map".equals(requiredDataType)){
				object = com.nfwork.dbfound.excel.ExcelReader.readExcelForMap((byte[]) ofile, type);
			}else {
				object = com.nfwork.dbfound.excel.ExcelReader.readExcel((byte[]) ofile, type);
			}
			context.setData(setPath, object);
			LogUtil.info("ExcelReader execute success, param value: " + param.getValue() + ", sourcePath: " +param.getSourcePathHistory());
		}else{
			InputStream inputStream = null;
			try {
				if (ofile instanceof File) {
					inputStream = Files.newInputStream(((File) ofile).toPath());
				} else if (ofile instanceof FilePart) {
					inputStream = ((FilePart) ofile).inputStream();
				} else if (ofile instanceof InputStream) {
					inputStream = (InputStream) ofile;
				}

				if (inputStream != null) {
					Object object;
					if ("map".equals(requiredDataType)) {
						object = com.nfwork.dbfound.excel.ExcelReader.readExcelForMap(inputStream, type);
					} else {
						object = com.nfwork.dbfound.excel.ExcelReader.readExcel(inputStream, type);
					}
					context.setData(setPath, object);
					LogUtil.info("ExcelReader execute success, param value: " + param.getValue() + ", sourcePath: " +param.getSourcePathHistory());
				}
			}catch (IOException exception){
				throw new DBFoundRuntimeException("ExcelReader execute failed, caused by : " + exception.getMessage(),exception);
			}finally {
				StreamUtils.closeInputStream(inputStream);
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

	public String getRequiredDataType() {
		return requiredDataType;
	}

	public void setRequiredDataType(String requiredDataType) {
		this.requiredDataType = requiredDataType;
	}
}
