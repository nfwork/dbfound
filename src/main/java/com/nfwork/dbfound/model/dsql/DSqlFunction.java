package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import java.util.List;

public abstract class DSqlFunction {

    public abstract Object apply(List<Object> params,SqlDialect sqlDialect);

    public boolean isSupported(SqlDialect sqlDialect){
        return true;
    }

    public void register(String functionName) {
        FunctionResolver.register(functionName, this);
    }
}
