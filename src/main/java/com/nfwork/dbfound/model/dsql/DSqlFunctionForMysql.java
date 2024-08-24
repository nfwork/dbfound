package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;

import java.util.List;

public class DSqlFunctionForMysql extends DSqlFunction {

    @Override
    public Object doApply(String functionName, List<Object> params,  String provideName, Context context){
        switch (functionName) {
            case "trim": {
               return trim(params);
            }
            case "length": {
                return length(params);
            }
            case "char_length": {
                return charLength(params);
            }
            case "ifnull": {
                return ifNull(params);
            }
            case "isnull": {
                return isNull(params);
            }
            case "if": {
                return ifExpress(params);
            }
            default:{
                throw new DSqlNotSupportException();
            }
        }
    }
}
