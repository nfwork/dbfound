package com.nfwork.dbfound.model.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public interface ExecuteAdapter {

    default ResponseObject handleExecute(Context context, Map<String, Param> params){
        return null;
    }

    default void beforeExecute(Context context, Map<String, Param> params){

    }

    default void afterExecute(Context context, Map<String, Param> params){

    }
}
