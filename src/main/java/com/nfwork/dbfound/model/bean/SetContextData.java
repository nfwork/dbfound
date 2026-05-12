package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;

import java.util.Map;

public class SetContextData extends SqlEntity{

    private String targetPath;

    private String value;

    private String sourcePath;

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {
        if(DataUtil.isNull(targetPath)){
            throw new DBFoundRuntimeException("attribute targetPath cannot be null in setContextData tag");
        }

        boolean hasValue = value != null;
        boolean hasSourcePath = DataUtil.isNotNull(sourcePath);
        if(hasValue == hasSourcePath){
            throw new DBFoundRuntimeException("setContextData tag must specify either value or sourcePath");
        }

        String setPath = resolvePath(context, targetPath);
        Object valueObj = hasSourcePath ? context.getData(resolvePath(context, sourcePath)) : value;
        context.setData(setPath,valueObj);
    }

    private String resolvePath(Context context, String path){
        String exePath = path;
        if(!ELEngine.isAbsolutePath(exePath)){
            String currentPath = context.getCurrentPath();
            if(DataUtil.isNull(currentPath)){
                throw new DBFoundRuntimeException("currentPath cannot be null when path is not absolute in setContextData tag");
            }
            exePath = currentPath + "." + exePath;
        }
        if(exePath.contains("[index]")){
            exePath = exePath.replace("[index]",getIndex(context.getCurrentPath()));
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
