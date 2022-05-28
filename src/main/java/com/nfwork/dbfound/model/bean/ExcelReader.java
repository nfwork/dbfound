package com.nfwork.dbfound.model.bean;

import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.ParamNotFoundException;

public class ExcelReader extends SqlEntity {

	private static final long serialVersionUID = -9013279323716030635L;

	private String sourceParam;
	private String rootPath;

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName){
		Param param = params.get(sourceParam);
		if (param == null) {
			throw new ParamNotFoundException("param: " + sourceParam + " 没有定义");
		}
		Object ofile = param.getValue();
		if (ofile != null) {
			FileItem item = (FileItem) ofile;
			List<List<Map>> datas = com.nfwork.dbfound.excel.ExcelReader
					.readExcel(item);
			context.setData(rootPath, datas);
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
