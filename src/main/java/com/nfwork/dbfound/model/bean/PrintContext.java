package com.nfwork.dbfound.model.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;

import java.util.Map;

public class PrintContext extends SqlEntity{

    private static final ObjectWriter PRETTY_WRITER = JsonUtil.getObjectMapper().writerWithDefaultPrettyPrinter();

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {
        try {
            LogUtil.info("Context infos:\ncurrentPath: "+context.getCurrentPath()+"\ndata: "
                    + PRETTY_WRITER.writeValueAsString(context.getDatas()));
        } catch (JsonProcessingException e) {
            LogUtil.error("printContext failed",e);
        }
    }

}
