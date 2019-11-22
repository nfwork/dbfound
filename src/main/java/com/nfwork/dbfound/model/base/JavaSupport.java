package com.nfwork.dbfound.model.base;

import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.bean.Param;

public abstract class JavaSupport {

	protected Map<String, Param> params;
	protected Context context;
	protected String provideName;

	public abstract void execute() throws Exception;

	public  Map<String, Param> getParams() {
		return params;
	}

	public  void setParams(Map<String, Param> params) {
		this.params = params;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getProvideName() {
		return provideName;
	}

	public void setProvideName(String provideName) {
		this.provideName = provideName;
	}
	
}
