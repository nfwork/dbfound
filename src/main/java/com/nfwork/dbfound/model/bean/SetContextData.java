package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DataUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

public class SetContextData extends SqlEntity{

    private String name;

    private String scope;

    private boolean inCurrentPath = false;

    private String value;

    private String param;

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

        if(!setPath.contains(".")){
            context.setData(setPath+"."+name,valueObj);
        }else {
            Object object = context.getData(setPath);
            if (object instanceof Map) {
                Map cMap = (Map) object;
                cMap.put(name, value);
            } else if (object != null) {
                try {
                    BeanUtils.setProperty(object, name, valueObj);
                } catch (Exception e) {
                    throw new DBFoundRuntimeException("set context data failed", e);
                }
            }
        }
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
}
