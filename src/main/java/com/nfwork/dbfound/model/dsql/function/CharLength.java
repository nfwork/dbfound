package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;

import java.util.List;

public class CharLength extends Length {

    @Override
    public Object apply(List<Object> params, SqlDialect sqlDialect) {
        return charLength(params);
    }
}
