package com.nfwork.dbfound.model.dsql;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class OracleFunction extends MySqlFunction{

    static Object apply(Expression expression, List<Object> param, String provideName){
        Function function = (Function)expression;
        String functionName = function.getName().toLowerCase();
        switch (functionName) {
            case "trim": {
               return trim(function,param,provideName);
            }
            case "length": {
                return charLength(function,param,provideName);
            }
            case "lengthb": {
                return length(function,param,provideName);
            }
            case "nvl": {
                return ifNull(function,param,provideName);
            }
            default:{
                return DSqlEngine.NOT_SUPPORT;
            }
        }
    }
}
