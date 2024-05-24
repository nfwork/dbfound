package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.FileDownloadResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.TransactionUtil;
import com.nfwork.dbfound.web.InterceptorFacade;
import com.nfwork.dbfound.web.file.FileDownloadUtil;

import java.util.List;

public class ExecuteActionHandler extends ActionHandler {

    @Override
    public boolean isSupport(String requestPath) {
        return requestPath.endsWith(".execute") || requestPath.contains(".execute!");
    }

    @Override
    protected ResponseObject doHandle(Context context, String requestPath) throws Exception{
        int modelIndex = requestPath.indexOf(".execute!");
        String modelName;
        String executeName;
        if (modelIndex > -1) {
            modelName = requestPath.substring(1, modelIndex);
            executeName = requestPath.substring(modelIndex + 9);
        } else {
            modelName = requestPath.substring(1, requestPath.length() - 8);
            executeName = null;
        }
        if (!InterceptorFacade.executeInterceptor(context, modelName, executeName)) {
            return null;
        }

        Object gridData = context.getData(ModelEngine.defaultBatchPath);

        ResponseObject object = TransactionUtil.execute(context,()->{
            if (gridData instanceof List) {
                return ModelEngine.batchExecute(context, modelName, executeName);
            } else {
                return ModelEngine.execute(context, modelName, executeName);
            }
        });

        if (object instanceof FileDownloadResponseObject) {
            if (context.isOutMessage()) {
                FileDownloadResponseObject fd = (FileDownloadResponseObject) object;
                FileDownloadUtil.download(fd.getFileParam(), fd.getParams(), context.response);
            }
            return null;
        } else {
            return object;
        }
    }
}
