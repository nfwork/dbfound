package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.excel.ExcelWriter;
import com.nfwork.dbfound.web.InterceptorHandler;

public class ExportActionHandler extends ActionHandler {

    @Override
    public boolean isSupport(String requestPath) {
        return requestPath.endsWith(".export") || requestPath.contains(".export!");
    }

    @Override
    protected ResponseObject doHandle(Context context, String requestPath) throws Exception {
        int modelIndex = requestPath.indexOf(".export!");
        String modelName;
        String queryName;
        if(modelIndex > -1) {
            modelName = requestPath.substring(1, modelIndex);
            queryName = requestPath.substring(modelIndex + 8);
        }else{
            modelName = requestPath.substring(1,requestPath.length() - 7);
            queryName = null;
        }
        if (InterceptorHandler.exportInterceptor(context, modelName, queryName)) {
            ExcelWriter.excelExport(context, modelName, queryName);
        }
        return null;
    }
}
