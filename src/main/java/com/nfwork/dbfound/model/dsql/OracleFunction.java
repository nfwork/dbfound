package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class OracleFunction extends MySqlFunction{

    static Object apply(Expression expression, List<Object> param, String provideName, Context context){
        Function function = (Function)expression;
        String functionName = function.getName().toLowerCase();
        switch (functionName) {
            case "trim": {
               return trim(function,param,provideName,context);
            }
            case "length": {
                return charLength(function,param,provideName,context);
            }
            case "lengthb": {
                return length(function,param,provideName,context);
            }
            case "nvl": {
                return ifNull(function,param,provideName,context);
            }
            default:{
                throw new DSqlNotSupportException();
            }
        }
    }
}
