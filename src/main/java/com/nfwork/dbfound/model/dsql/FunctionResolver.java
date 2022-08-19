package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.db.dialect.MySqlDialect;
import com.nfwork.dbfound.db.dialect.OracleDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

public class FunctionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
        if(provide.getSqlDialect() instanceof MySqlDialect){
            return MySqlFunction.apply(expression,param,provideName,context);
        }else if(provide.getSqlDialect() instanceof OracleDialect){
            return OracleFunction.apply(expression,param,provideName,context);
        }else{
            throw new DSqlNotSupportException();
        }
    }
}