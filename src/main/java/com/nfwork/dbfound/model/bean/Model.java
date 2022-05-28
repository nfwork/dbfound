package com.nfwork.dbfound.model.bean;

import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.web.jstl.ForEach;
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

	@Override
	public void init(Element element) {
		querys = new HashMap<String, Query>();
		executes = new HashMap<String, Execute>();
		params = new HashMap<String, Param>();
		super.init(element);
	}

	@Override
	public void run() {
		for (Param param : params.values()){
			for (Execute execute : executes.values()){
				Param exeParam = execute.getParams().get(param.getName());
				if (exeParam == null ||  "unknown".equals(exeParam.getDataType())){
					execute.getParams().put(param.getName(),param);
				}
			}
			for (Query query : querys.values()){
				Param queryParam = query.getParams().get(param.getName());
				if (queryParam == null ||  "unknown".equals(queryParam.getDataType())){
					query.getParams().put(param.getName(),param);
				}
			}
		}
	}

	public Model(String modelName) {
		this.modelName = modelName;
	}

	public Query getQuery(String name) {
		Query query = querys.get(name);
		return  query;
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
		Execute execute = executes.get(name);
		return execute;
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

	public Model() {

	}

	public Map<String, Param> getParams() {
		return params;
	}

	public void setParams(Map<String, Param> params) {
		this.params = params;
	}

	public String getConnectionProvide(Context context) {
		// 支持动态设置数据来源
		if (!"_default".equals(connectionProvide)) {
			String value = context.getString(connectionProvide);
			if (value != null) {
				return value;
			}
		}
		return connectionProvide;
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
}
