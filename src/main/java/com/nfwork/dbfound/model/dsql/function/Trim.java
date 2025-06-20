package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;

import java.util.List;

public class Trim extends DSqlFunction {

    @Override
    public Object apply(List<Object> params,SqlDialect sqlDialect) {
        return trim(params);
    }

    protected Object trim(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            if (p0 == null) {
                return null;
            }
            return p0.toString().trim();
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
