package com.nfwork.dbfound.model.bean;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.model.adapter.AdapterFactory;
import com.nfwork.dbfound.model.adapter.ExecuteAdapter;
import com.nfwork.dbfound.model.base.FileSaveType;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StreamUtils;
import com.nfwork.dbfound.web.file.FilePart;
import org.dom4j.Element;
import com.nfwork.dbfound.core.Context;

public class Execute extends SqlEntity {

	private static final long serialVersionUID = 7670352852092590245L;

	private String connectionProvide;
	private String name = "_default";// execute对象的名字
	private String modelName;
	private Sqls sqls; // execute对象对应的配置sql
	private Map<String, Param> params; // query对象对应参数

	private String adapter;
	private ExecuteAdapter executeAdapter;
	private String currentPath;

	@Override
	public void init(Element element) {
		params = new LinkedHashMap<>();
		super.init(element);
	}

	@Override
	public void run() {
		if(DataUtil.isNotNull(adapter)){
			try {
				executeAdapter = AdapterFactory.getExecuteAdapter(Class.forName(adapter));
			}catch (Exception exception){
				String message = "ExecuteAdapter init failed, please check the class "+ adapter+" is exists or it is implement ExecuteAdapter";
				throw new DBFoundRuntimeException(message, exception);
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
		HashMap<String, Param> params = new LinkedHashMap<>();
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
			try {
				Object value = param.getValue();
				if (value instanceof InputStream) {
					if (list == null) {
						list = new ArrayList<>();
					}
					list.add((InputStream)value);
				} else if (value instanceof FilePart) {
					if (list == null) {
						list = new ArrayList<>();
					}
					InputStream inputStream = ((FilePart) value).inputStream();
					param.setValue(inputStream);
					list.add(inputStream);
				} else if (param.getFileSaveType() == FileSaveType.DISK && value instanceof String ){
					if (list == null) {
						list = new ArrayList<>();
					}
					InputStream inputStream = new FileInputStream((String) value);
					param.setValue(inputStream);
					param.setSourcePathHistory((String) value);
					list.add(inputStream);
				}
			}catch (Exception exception){
				closeFileParam(list);
				throw new DBFoundRuntimeException("init file param failed, "+exception.getMessage(), exception);
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

	public String getConnectionProvide() {
		return connectionProvide;
	}

	public void setConnectionProvide(String connectionProvide) {
		this.connectionProvide = connectionProvide;
	}
}
