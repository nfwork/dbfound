package com.nfwork.dbfound.model.bean;

import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.adapter.AdapterFactory;
import com.nfwork.dbfound.model.adapter.ExecuteAdapter;
import com.nfwork.dbfound.util.DataUtil;
import org.dom4j.Element;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;

public class Execute extends SqlEntity {

	private static final long serialVersionUID = 7670352852092590245L;

	private String name = "_default";// execute对象的名字
	private String modelName;
	private Sqls sqls; // execute对象对应的配置sql
	private Map<String, Param> params; // query对象对应参数

	private String adapter;
	private ExecuteAdapter executeAdapter;
	private String currentPath;

	@Override
	public void init(Element element) {
		params = new HashMap<String, Param>();
		super.init(element);
	}

	@Override
	public void run() {
		if(DataUtil.isNotNull(adapter)){
			try {
				executeAdapter = AdapterFactory.getExecuteAdapter(Class.forName(adapter));
			}catch (Exception exception){
				String message = "ExecuteAdapter init failed, executeAdapter must implement ExecuteAdapter";
				throw new DBFoundPackageException(message, exception);
			}
		}
		if (getParent() instanceof Model) {
			Model model = (Model) getParent();
			model.putExecute(name, this);
		} else {
			super.run();
		}
	}

	public Map<String, Param> cloneParams() {
		HashMap<String, Param> params = new HashMap<String, Param>();
		for(Map.Entry<String,Param> entry : this.params.entrySet()){
			params.put(entry.getKey(), (Param) entry.getValue().cloneEntity());
		}
		return params;
	}

	public void executeRun(Context context,Map<String, Param> params, String provideName){
		if (sqls != null) {
			for (int i = 0; i < sqls.sqlList.size(); i++) {
				SqlEntity sql = sqls.sqlList.get(i);
				sql.execute(context, params, provideName);
			}
		}
	}

	@Override
	public void execute(Context context, Map<String, Param> params,String provideName){
		String currentPath = context.getCurrentPath();
		String currentModel = context.getCurrentModel();
		String mName = modelName != null ? modelName : currentModel;
		if(DataUtil.isNotNull(this.currentPath)){
			currentPath = this.currentPath;
		}
		ModelEngine.execute(context, mName, name, currentPath);
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

	public ExecuteAdapter getExecuteAdapter() {
		return executeAdapter;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}
}
