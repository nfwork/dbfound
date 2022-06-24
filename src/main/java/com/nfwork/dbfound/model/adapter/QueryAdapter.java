package com.nfwork.dbfound.model.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public interface QueryAdapter {

    public void beforeQuery(Context context, Map<String, Param> params);

    public void afterQuery(Context context, Map<String, Param> params, QueryResponseObject responseObject);

}
