package com.nfwork.dbfound.model.bean;

import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.util.DataUtil;
import org.dom4j.Element;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.base.Entity;

public class Model extends Entity {

	private static final long serialVersionUID = -1944232395561791090L;

	private long fileLastModify;
	private Map<String, Query> querys;
	private Map<String, Execute> executes;
	private Map<String, Param> params; // query对象对应参数
	private String modelName;
	private String connectionProvide = "_default";
	private String fileLocation;
	private boolean pkgModel = false;

	@Override
	public void init(Element element) {
		querys = new HashMap<>();
		executes = new HashMap<>();
		params = new HashMap<>();
		super.init(element);
	}

	@Override
	public void run() {
		for (Param param : params.values()){
			for (Execute execute : executes.values()){
				Param exeParam = execute.getParams().get(param.getName());
				if (exeParam == null ||  exeParam.getDataType() == DataType.UNKNOWN){
					execute.getParams().put(param.getName(),param);
				}
			}
			for (Query query : querys.values()){
				Param queryParam = query.getParams().get(param.getName());
				if (queryParam == null ||  queryParam.getDataType() == DataType.UNKNOWN){
					query.getParams().put(param.getName(),param);
				}
			}
		}
	}

	public Model(String modelName) {
		this.modelName = modelName;
	}

	public Query getQuery(String name) {
		return querys.get(name);
	}

	public Query getQuery(){
		return getQuery("_default");
	}

	public void putQuery(String name, Query query) {
		querys.put(name, query);
	}

	public void putExecute(String name, Execute execute) {
		executes.put(name, execute);
	}

	public Execute getExecute(String name) {
		return executes.get(name);
	}

	public Execute getExecute(){
		return getExecute("_default");
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Map<String, Param> getParams() {
		return params;
	}

	public void setParams(Map<String, Param> params) {
		this.params = params;
	}

	public String getConnectionProvide(Context context, String provideName) {
		if(DataUtil.isNull(provideName)){
			provideName = connectionProvide;
		}
		// 支持动态设置数据来源
		if (ELEngine.isAbsolutePath(provideName)) {
			String value = context.getString(provideName);
			if (value != null) {
				return value;
			}
		}
		return provideName;
	}

	public void setConnectionProvide(String connectionProvide) {
		this.connectionProvide = connectionProvide;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public long getFileLastModify() {
		return fileLastModify;
	}

	public void setFileLastModify(long fileLastModify) {
		this.fileLastModify = fileLastModify;
	}

	public boolean isPkgModel() {
		return pkgModel;
	}

	public void setPkgModel(boolean pkgModel) {
		this.pkgModel = pkgModel;
	}
}
