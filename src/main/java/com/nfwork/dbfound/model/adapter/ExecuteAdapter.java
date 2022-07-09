package com.nfwork.dbfound.model.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public interface ExecuteAdapter {

    default void beforeExecute(Context context, Map<String, Param> params){

    }

    default void afterExecute(Context context, Map<String, Param> params){

    }
}
