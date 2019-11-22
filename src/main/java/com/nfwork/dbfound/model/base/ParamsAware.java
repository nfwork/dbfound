package com.nfwork.dbfound.model.base;

import java.util.Map;

import com.nfwork.dbfound.model.bean.Param;

public interface ParamsAware {

	public void setParams(Map<String, Param> params);

}
