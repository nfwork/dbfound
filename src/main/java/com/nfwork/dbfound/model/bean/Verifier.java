package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;

public class Verifier extends Entity{

    private String express;

    private String message;

    private String code;

    @Override
    public void doEndTag() {
        if(DataUtil.isNull(express) || DataUtil.isNull(message)){
            throw new DBFoundRuntimeException("Verifier attribute express and message can not be null");
        }
        if (getParent() instanceof Query) {
            Query query = (Query) getParent();
            query.getVerifiers().add(this);
        }
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
