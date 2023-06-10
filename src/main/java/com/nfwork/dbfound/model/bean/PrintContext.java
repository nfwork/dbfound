package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;

import java.util.Map;

public class PrintContext extends SqlEntity{

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {
        LogUtil.info("PrintContext currentPath: "+context.getCurrentPath()+", data: "+JsonUtil.mapToJson(context.getDatas()));
    }

}
