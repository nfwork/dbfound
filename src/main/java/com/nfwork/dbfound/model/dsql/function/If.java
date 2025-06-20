package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import com.nfwork.dbfound.model.dsql.DSqlFunction;

import java.util.List;

public class If extends DSqlFunction {

    @Override
    public Object apply(List<Object> params, SqlDialect sqlDialect) {
        return ifExpress(params);
    }

    protected Object ifExpress(List<Object> params){
        if (params.size() == 3) {
            Object result = params.get(0);
            boolean p0 = result != null && DSqlEngine.getBooleanValue(result);
            Object p1 = params.get(1);
            Object p2 = params.get(2);
            return p0 ? p1 : p2;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
