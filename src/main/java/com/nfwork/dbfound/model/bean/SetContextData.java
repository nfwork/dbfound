package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DataUtil;

import java.util.Map;

public class SetContextData extends SqlEntity{

    private String name;

    private String scope;

    private boolean inCurrentPath = false;

    private String value;

    private String param;

    private String sourcePath;

    private String valueTemplate;

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {
        if(DataUtil.isNull(name)){
            throw new DBFoundRuntimeException("attribute name can not be null in setContextData tag");
        }

        String setPath = scope;
        if(inCurrentPath){
            setPath = context.getCurrentPath();
        }
        if(DataUtil.isNull(setPath)){
            throw new DBFoundRuntimeException("path can not be null in setContextData tag");
        }

        Object valueObj = value;
        if(DataUtil.isNotNull(param)){
            Param paramObj = params.get(param);
            if(paramObj == null) {
                throw new ParamNotFoundException("param: " + param + " not defined");
            }
            valueObj = paramObj.getValue();
        }
        if(DataUtil.isNotNull(sourcePath)){
            String exePath = sourcePath;
            if(!ELEngine.isAbsolutePath(exePath)){
                exePath = context.getCurrentPath() + "." +exePath;
            }
            if(exePath.contains("[index]")){
                String index = getIndex(context.getCurrentPath());
                exePath = exePath.replace("[index]",index);
            }
            valueObj = context.getData(exePath);
        }

        if(DataUtil.isNotNull(valueTemplate)){
            valueObj = valueTemplate.replace("#{@"+name+"}",valueObj==null?"":valueObj.toString());
        }

        if(name.contains("[index]")){
            String index = getIndex(context.getCurrentPath());
            setPath = setPath+"." + name.replace("[index]",index);
        }else{
            setPath = setPath + "." + name;
        }
        context.setData(setPath,valueObj);
    }

    private String getIndex(String currentPath){
        if(currentPath.endsWith("]")){
            int index = currentPath.lastIndexOf("[");
            if(index != -1) {
                return currentPath.substring(index);
            }
        }
        throw new DBFoundRuntimeException("SetContextData cant not found index in currentPath");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isInCurrentPath() {
        return inCurrentPath;
    }

    public void setInCurrentPath(boolean inCurrentPath) {
        this.inCurrentPath = inCurrentPath;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getValueTemplate() {
        return valueTemplate;
    }

    public void setValueTemplate(String valueTemplate) {
        this.valueTemplate = valueTemplate;
    }
}
