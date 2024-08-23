package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class DSqlFunctionForMysql extends DSqlFunction {

    public Object apply(String functionName, Function function, List<Object> param, String provideName, Context context){
        switch (functionName) {
            case "trim": {
               return trim(function,param,provideName,context);
            }
            case "length": {
                return length(function,param,provideName,context);
            }
            case "char_length": {
                return charLength(function,param,provideName,context);
            }
            case "ifnull": {
                return ifNull(function,param,provideName,context);
            }
            case "isnull": {
                return isNull(function,param,provideName,context);
            }
            case "if": {
                return ifExpress(function,param,provideName,context);
            }
            default:{
                throw new DSqlNotSupportException();
            }
        }
    }
}
