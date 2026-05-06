package com.nfwork.dbfound.web.handler;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.web.InterceptorFacade;
import com.nfwork.dbfound.web.ExceptionHandlerFacade;
import com.nfwork.dbfound.web.WebWriter;
import com.nfwork.dbfound.web.file.FileUploadUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class ActionHandler {

    protected final WebApiPermissionChecker permissionChecker;

    protected ActionHandler(WebApiPermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response){
        String requestUrl = request.getServletPath();
        boolean isFileUpload = false;

        try {
            InterceptorFacade.setCors(request,response);
            if("OPTIONS".equals(request.getMethod())){
                return;
            }
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
            ExceptionHandlerFacade.handle(throwable, request, response);
        }finally {
            if(isFileUpload){
                FileUploadUtil.clearFileItemLocal();
            }
        }
    }

    public abstract boolean isSupport(String requestPath);

    protected abstract ResponseObject doHandle(Context context, String requestPath) throws Exception;

}
