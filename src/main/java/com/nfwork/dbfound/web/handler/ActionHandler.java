package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundErrorException;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.web.WebExceptionHandler;
import com.nfwork.dbfound.web.WebWriter;
import com.nfwork.dbfound.web.file.FileUploadUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ActionHandler {

    public void handle(HttpServletRequest request, HttpServletResponse response){
        String requestUrl = request.getServletPath();
        boolean isFileUpload = false;

        try {
            if (request.getCharacterEncoding() == null) {
                request.setCharacterEncoding(DBFoundConfig.getEncoding());// 编码设置
            }
            Context context = Context.getCurrentContext(request, response);
            // 初始化文件上传组件
            isFileUpload = FileUploadUtil.isUploadRequest(context);
            if (isFileUpload) {
                FileUploadUtil.initFileUpload(context);
            }

            ResponseObject responseObject = doHandle(context, requestUrl);
            if(context.isOutMessage() && responseObject != null){
                WebWriter.jsonWriter(response, JsonUtil.toJson(responseObject));
            }
        } catch (Throwable throwable) {
            Exception exception;
            if(throwable instanceof Exception){
                exception = (Exception) throwable;
            }else{
                exception = new DBFoundErrorException("dbfound execute error, cause by "+ throwable.getMessage(), throwable);
            }
            WebExceptionHandler.handle(exception, request, response);
        }finally {
            if(isFileUpload){
                FileUploadUtil.clearFileItemLocal();
            }
        }
    }

    public abstract boolean isSupport(String requestPath);

    protected abstract ResponseObject doHandle(Context context, String requestPath) throws Exception;

}
