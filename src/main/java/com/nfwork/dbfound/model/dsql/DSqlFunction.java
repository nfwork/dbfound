package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DSqlFunction {

    private final Set<String> functionNames = ConcurrentHashMap.newKeySet();

    public abstract Object apply(List<Object> params,SqlDialect sqlDialect);

    public boolean isSupported(SqlDialect sqlDialect){
        return true;
    }

    public void register(String functionName) {
        FunctionResolver.register(functionName, this);
        functionNames.add(functionName);
    }

    public void unRegister() {
        for (String functionName : functionNames) {
            FunctionResolver.unRegister(functionName, this);
        }
        functionNames.clear();
    }
}
