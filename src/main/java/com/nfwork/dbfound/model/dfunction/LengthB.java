package com.nfwork.dbfound.model.dfunction;

import com.nfwork.dbfound.db.dialect.OracleDialect;
import com.nfwork.dbfound.db.dialect.SqlDialect;

import java.util.List;

public class LengthB extends DSqlFunction {

    @Override
    public boolean isSupported(SqlDialect sqlDialect) {
        return true;
    }

    @Override
    public Object apply(List<Object> params,SqlDialect sqlDialect) {
        return length(params);
    }
}
