package com.nfwork.dbfound.model.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public interface ExecuteAdapter {

    public void beforeExecute(Context context, Map<String, Param> params);

    public void afterExecute(Context context, Map<String, Param> params);
}
