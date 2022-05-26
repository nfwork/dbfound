package com.nfwork.dbfound.model.bean;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.ModelEngine;

public class Execute extends SqlEntity {

	private static final long serialVersionUID = 7670352852092590245L;

	private String name = "_default";// execute对象的名字
	private String modelName;
	private Sqls sqls; // execute对象对应的配置sql
	private Map<String, Param> params; // query对象对应参数

	@Override
	public void init(Element element) {
		params = new HashMap<String, Param>();
		super.init(element);
	}

	@Override
	public void run() {
		if (getParent() instanceof Model) {
			Model model = (Model) getParent();
			model.putExecute(name, this);
		} else {
			super.run();
		}
	}

	public Execute cloneEntity() {
		Execute execute;
		try {
			execute = (Execute) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new DBFoundPackageException(
					"Execute克隆异常:" + e.getMessage(), e);
		}
		HashMap<String, Param> params = new HashMap<String, Param>();
		for (Iterator iterator = this.params.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Param param = (Param) entry.getValue();
			params.put(entry.getKey().toString(), (Param) param.cloneEntity());
		}
		execute.setParams(params);
		return execute;
	}

	/**
	 * 执行对应的sql集合
	 * @param context
	 * @param provideName
	 */
	public void execute(Context context, String provideName){
		if (sqls != null) {
			for (int i = 0; i < sqls.sqlList.size(); i++) {
				SqlEntity sql = sqls.sqlList.get(i);
				sql.execute(context, params, provideName);
			}
		}
	}

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName){
		String currentPath = context.getCurrentPath();
		if (modelName == null) {
			modelName = context.getCurrentModel();
		}
		ModelEngine.execute(context, modelName, name, currentPath);
	}

	/**
	 * 设定参数
	 * 
	 * @param name
	 * @param value
	 */
	public void setParam(String name, String value) {
		params.get(name).setValue(value);
	}

	/**
	 * 得到参数值
	 * 
	 * @param name
	 * @return
	 */
	public String getParam(String name) {
		return params.get(name).getStringValue();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Param> getParams() {
		return params;
	}

	public Sqls getSqls() {
		return sqls;
	}

	public void setSqls(Sqls sqls) {
		this.sqls = sqls;
	}

	public void setParams(Map<String, Param> params) {
		this.params = params;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
}
