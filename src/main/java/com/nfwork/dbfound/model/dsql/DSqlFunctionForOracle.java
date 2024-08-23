package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class DSqlFunctionForOracle extends DSqlFunction {

    public Object apply(String functionName, Function function, List<Object> param, String provideName, Context context){
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
