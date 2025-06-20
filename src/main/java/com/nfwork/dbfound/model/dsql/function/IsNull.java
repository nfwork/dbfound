package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;

import java.util.List;

public class IsNull extends DSqlFunction {

    @Override
    public Object apply(List<Object> params, SqlDialect sqlDialect) {
        return isNull(params);
    }

    protected Object isNull(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            return p0 == null;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
