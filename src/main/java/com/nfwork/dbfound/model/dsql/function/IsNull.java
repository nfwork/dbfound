package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;

import java.util.List;

public class IsNull extends DSqlFunction{
    @Override
    public boolean isSupported(SqlDialect sqlDialect) {
        return true;
    }

    @Override
    public Object apply(List<Object> params, SqlDialect sqlDialect) {
        return isNull(params);
    }
}
