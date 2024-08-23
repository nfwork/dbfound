package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;

import java.util.List;

public class DSqlFunctionForOracle extends DSqlFunction {

    @Override
    public Object doApply(String functionName, List<Object> params,  String provideName, Context context){
        switch (functionName) {
            case "trim": {
               return trim(params);
            }
            case "length": {
                return charLength(params);
            }
            case "lengthb": {
                return length(params);
            }
            case "nvl": {
                return ifNull(params);
            }
            default:{
                throw new DSqlNotSupportException();
            }
        }
    }
}
