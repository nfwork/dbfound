package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;

import java.util.List;

public class IfNull extends DSqlFunction {

    @Override
    public Object apply(List<Object> params, SqlDialect sqlDialect) {
        return ifNull(params);
    }

    protected Object ifNull(List<Object> params){
        if (params.size() == 2) {
            Object p0 = params.get(0);
            Object p1 = params.get(1);
            return p0 == null ? p1 : p0;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
