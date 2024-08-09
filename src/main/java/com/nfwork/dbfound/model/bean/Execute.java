package com.nfwork.dbfound.model.bean;

import java.util.*;

import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.model.adapter.AdapterFactory;
import com.nfwork.dbfound.model.adapter.ExecuteAdapter;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.StringUtil;
import org.dom4j.Element;
import com.nfwork.dbfound.core.Context;

public class Execute extends SqlEntity {
	private String connectionProvide;
	private String name = "_default";// execute对象的名字
	private String modelName;
	private Sqls sqls; // execute对象对应的配置sql
	private Map<String, Param> params; // query对象对应参数

	private String adapter;
	private List<ExecuteAdapter> executeAdapterList;
	private String currentPath;

	@Override
	public void doStartTag(Element element) {
		params = new LinkedHashMap<>();
		super.doStartTag(element);
	}

	@Override
	public void doEndTag() {
		if(DataUtil.isNotNull(adapter)){
			executeAdapterList = new ArrayList<>();
			List<String> nameList = StringUtil.splitToList(adapter);
			for (String name : nameList) {
				try {
					executeAdapterList.add(AdapterFactory.getExecuteAdapter(Class.forName(name)));
				} catch (Exception exception) {
					String message = "ExecuteAdapter init failed, please check the class " + name + " is exists or it is implement ExecuteAdapter";
					throw new DBFoundRuntimeException(message, exception);
				}
			}
		}
		if (getParent() instanceof Model) {
			Model model = (Model) getParent();
			if(DataUtil.isNull(name)){
				name = "_default";
			}
			model.putExecute(name, this);
		} else {
			super.doEndTag();
		}
	}

	public Map<String, Param> cloneParams() {
		HashMap<String, Param> params = new LinkedHashMap<>();
		for(Map.Entry<String,Param> entry : this.params.entrySet()){
			params.put(entry.getKey(), (Param) entry.getValue().cloneEntity());
		}
		return params;
	}

	public ResponseObject doExecute(Context context, String currentPath, Map<String, Object> elCache) {
		Map<String, Param> params = cloneParams();

		// 设想sql查询参数
		for (Param nfParam : params.values()) {
			setParam(nfParam, context, currentPath, elCache);
		}

		if(executeAdapterList!=null){
			for(ExecuteAdapter executeAdapter : executeAdapterList) {
				executeAdapter.beforeExecute(context, params);
			}
		}

		String provideName = ((Model)getParent()).getConnectionProvide(context, getConnectionProvide());
		LogUtil.info("Execute info (modelName:" + context.getCurrentModel() + ", executeName:" + name + ", provideName:"+provideName+")");

		if (sqls != null) {
			for (int i = 0; i < sqls.sqlList.size(); i++) {
				SqlEntity sql = sqls.sqlList.get(i);
				sql.execute(context, params, provideName);
			}
		}

		if(executeAdapterList!=null){
			for(ExecuteAdapter executeAdapter : executeAdapterList) {
				executeAdapter.afterExecute(context, params);
			}
		}

		ResponseObject ro = new ResponseObject();
		ro.setSuccess(true);
		ro.setMessage("success");
		ro = initOutParams(context, params, ro);

		return ro;
	}

	@Override
	public void execute(Context context, Map<String, Param> params,String provideName){
		final String currentPath = context.getCurrentPath();
		final String currentModel = context.getCurrentModel();

		String mName = DataUtil.isNull(modelName) ? currentModel : modelName;

		String exePath = this.currentPath;
		if(DataUtil.isNotNull(exePath)){
			if(!ELEngine.isAbsolutePath(exePath)) {
				exePath = currentPath +"." + exePath;
			}
		}else{
			exePath = currentPath;
		}
		ModelEngine.execute(context, mName, name, exePath);
		context.setCurrentPath(currentPath);
		context.setCurrentModel(currentModel);
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

	public String getAdapter() {
		return adapter;
	}

	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public String getConnectionProvide() {
		return connectionProvide;
	}

	public void setConnectionProvide(String connectionProvide) {
		this.connectionProvide = connectionProvide;
	}
}
