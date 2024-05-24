package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.FileDownloadResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.InterceptorFacade;
import com.nfwork.dbfound.web.action.ActionBean;
import com.nfwork.dbfound.web.action.ActionEngine;
import com.nfwork.dbfound.web.action.ActionReflect;
import com.nfwork.dbfound.web.file.FileDownloadUtil;


public class DoActionHandler extends ActionHandler {

    private final ActionReflect actionReflect = new ActionReflect();

    @Override
    public boolean isSupport(String requestPath) {
        return requestPath.endsWith(".do") || requestPath.contains(".do!");
    }

    @Override
    protected ResponseObject doHandle(Context context, String requestPath) throws Exception{
        int modelIndex = requestPath.indexOf(".do!");
        String modelName;
        String methodName;
        if (modelIndex > -1) {
            modelName = requestPath.substring(1, modelIndex);
            methodName = requestPath.substring(modelIndex + 4);
        } else {
            modelName = requestPath.substring(1, requestPath.length() - 3);
            methodName = "execute";
        }
        ResponseObject object = null;

        ActionBean actionBean = ActionEngine.findActionBean(modelName); // 得到对应的class类的名字
        if (actionBean != null) {
            if (InterceptorFacade.doInterceptor(context, actionBean.getClassName(), methodName)) {
                object = actionReflect.reflect(context, actionBean.getClassName(), methodName, actionBean.isSingleton());
            }
        } else {
            String message = "cant not found url:" + requestPath.substring(1) + " mapping class, please check config";
            throw new DBFoundRuntimeException(message);
        }
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
