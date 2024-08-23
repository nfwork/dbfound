package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.db.dialect.MySqlDialect;
import com.nfwork.dbfound.db.dialect.OracleDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FunctionResolver extends DSqlValueResolver {

    private static final Map<String, DSqlFunction> functionMap = new ConcurrentHashMap<>();
    static {
        register(MySqlDialect.class, new DSqlFunctionForMysql());
        register(OracleDialect.class, new DSqlFunctionForOracle());
    }

    public static void register(Class<?> clazz, DSqlFunction function){
        functionMap.put(clazz.getName(), function);
    }

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
        Function function = (Function)expression;
        if(function.getMultipartName().size()!=1){
            throw new DSqlNotSupportException();
        }
        String functionName = function.getMultipartName().get(0).toLowerCase();
        DSqlFunction dSqlFunction = functionMap.get(provide.getSqlDialect().getClass().getName());
        if(dSqlFunction == null){
            throw new DSqlNotSupportException();
        }
        return dSqlFunction.apply(functionName,function,param,provideName,context);
    }
}