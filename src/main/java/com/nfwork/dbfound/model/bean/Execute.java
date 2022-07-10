package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.adapter.AdapterFactory;
import com.nfwork.dbfound.model.adapter.ExecuteAdapter;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StreamUtils;
import org.apache.commons.fileupload.FileItem;
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
		params = new HashMap<>();
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
		HashMap<String, Param> params = new HashMap<>();
		for(Map.Entry<String,Param> entry : this.params.entrySet()){
			params.put(entry.getKey(), (Param) entry.getValue().cloneEntity());
		}
		return params;
	}

	public void executeRun(Context context,Map<String, Param> params, String provideName){
		List<InputStream> list = initFileParam(params);
		try {
			if (sqls != null) {
				for (int i = 0; i < sqls.sqlList.size(); i++) {
					SqlEntity sql = sqls.sqlList.get(i);
					sql.execute(context, params, provideName);
				}
			}
		}finally {
			closeFileParam(list);
		}
	}

	@Override
	public void execute(Context context, Map<String, Param> params,String provideName){
		final String currentPath = context.getCurrentPath();
		final String currentModel = context.getCurrentModel();

		String mName = modelName != null ? modelName : currentModel;

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

	private List<InputStream> initFileParam(Map<String, Param> params){
		List<InputStream> list = null;
		for (Param param : params.values()) {
			if ("file".equals(param.getDataType())) {
				if (list == null) {
					list = new ArrayList<>();
				}
				try {
					if (param.getValue() instanceof FileItem) {
						InputStream inputStream = ((FileItem) param.getValue()).getInputStream();
						param.setValue(inputStream);
						list.add(inputStream);
					} else if (param.getValue() instanceof File) {
						InputStream inputStream = new FileInputStream((File) param.getValue());
						param.setValue(inputStream);
						list.add(inputStream);
					} else if (param.getValue() instanceof InputStream) {
						list.add((InputStream) param.getValue());
					}
				} catch (Exception e) {
					closeFileParam(list);
					throw new DBFoundPackageException(e);
				}
			}
		}
		return list;
	}

	private void closeFileParam(List<InputStream> list){
		if(list != null) {
			for (InputStream inputStream : list) {
				StreamUtils.closeInputStream(inputStream);
			}
		}
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
