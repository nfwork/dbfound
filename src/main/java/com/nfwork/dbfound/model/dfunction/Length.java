package com.nfwork.dbfound.model.dfunction;

import com.nfwork.dbfound.db.dialect.OracleDialect;
import com.nfwork.dbfound.db.dialect.SqlDialect;

import java.util.List;

public class Length extends DSqlFunction {

    @Override
    public boolean isSupported(SqlDialect sqlDialect) {
        return true;
    }

    @Override
    public Object apply(List<Object> params,SqlDialect sqlDialect) {
        if (sqlDialect instanceof OracleDialect){
            return charLength(params);
        }else{
            return length(params);
        }
    }
}
