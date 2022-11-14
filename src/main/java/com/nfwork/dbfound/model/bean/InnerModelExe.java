package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.ModelEngine;

import java.util.Map;

class InnerModelExe extends ModelEngine {

    static void innerExecute(Context context, String modelName, String executeName, String currentPath) {
        if (executeName == null || "".equals(executeName))
            executeName = "_default";
        Map<String, Param> params = executeRun(context, modelName, executeName, currentPath);
        getOutParams(context, params);
    }

    static <T> QueryResponseObject<T> innerQuery(Context context, String modelName, String queryName, String currentPath, boolean autoPaging,
                                                   Class<T> clazz) {
        return exeQuery(context,modelName,queryName,currentPath,autoPaging,clazz);
    }
}