package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DataUtil;

import java.util.Map;

public class SetContextData extends SqlEntity{

    private String targetPath;

    private String value;

    private String param;

    private String sourcePath;

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {
        if(DataUtil.isNull(targetPath)){
            throw new DBFoundRuntimeException("attribute targetPath cannot be null in setContextData tag");
        }
        String currentPath = context.getCurrentPath();

        boolean hasValue = value != null;
        boolean hasSourcePath = DataUtil.isNotNull(sourcePath);
        boolean hasParam = DataUtil.isNotNull(param);
        if((hasValue ? 1 : 0) + (hasSourcePath ? 1 : 0) + (hasParam ? 1 : 0) != 1){
            throw new DBFoundRuntimeException("setContextData tag must specify one of value, sourcePath or param");
        }

        String setPath = resolvePath(currentPath, targetPath);
        Object valueObj = resolveValue(context, currentPath, params, hasSourcePath, hasParam);
        context.setData(setPath,valueObj);
    }

    private Object resolveValue(Context context, String currentPath, Map<String, Param> params, boolean hasSourcePath, boolean hasParam){
        if(hasSourcePath){
            return context.getData(resolvePath(currentPath, sourcePath));
        }
        if(hasParam){
            Param paramObj = params == null ? null : params.get(param);
            if(paramObj == null) {
                throw new ParamNotFoundException("param: " + param + " not defined");
            }
            return paramObj.getValue();
        }
        return value;
    }

    private String resolvePath(String currentPath, String path){
        String exePath = path;
        if(!ELEngine.isAbsolutePath(exePath)){
            if(DataUtil.isNull(currentPath)){
                throw new DBFoundRuntimeException("currentPath cannot be null when path is not absolute in setContextData tag");
            }
            exePath = currentPath + "." + exePath;
        }
        if(exePath.contains("[index]")){
            exePath = exePath.replace("[index]",getIndex(currentPath));
        }
        return exePath;
    }

    private String getIndex(String currentPath){
        if(currentPath != null && currentPath.endsWith("]")){
            int index = currentPath.lastIndexOf("[");
            if(index != -1) {
                return currentPath.substring(index);
            }
        }
        throw new DBFoundRuntimeException("SetContextData cannot find index in currentPath");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
}
