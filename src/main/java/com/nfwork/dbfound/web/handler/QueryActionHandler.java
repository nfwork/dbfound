package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.web.InterceptorHandler;

public class QueryActionHandler extends ActionHandler {

    @Override
    public boolean isSupport(String requestPath) {
        return requestPath.endsWith(".query") || requestPath.contains(".query!");
    }

    @Override
    protected ResponseObject doHandle(Context context, String requestPath) throws Exception{
        int modelIndex = requestPath.indexOf(".query!");
        String modelName;
        String queryName;
        if(modelIndex > -1) {
            modelName = requestPath.substring(1, modelIndex);
            queryName = requestPath.substring(modelIndex + 7);
        }else{
            modelName = requestPath.substring(1,requestPath.length() - 6);
            queryName = null;
        }
        if (!InterceptorHandler.queryInterceptor(context, modelName, queryName)) {
            return null;
        }
        return ModelEngine.query(context, modelName, queryName);
    }
}
