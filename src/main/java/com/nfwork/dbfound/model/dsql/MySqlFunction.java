package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.web.WebWriter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class MySqlFunction {

    static Object apply(Expression expression, List<Object> param, String provideName, Context context){
        net.sf.jsqlparser.expression.Function function = (net.sf.jsqlparser.expression.Function)expression;
        String functionName = function.getName().toLowerCase();
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

    public static Object trim(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName, context);
            if (p0 == null) {
                return null;
            }
            return p0.toString().trim();
        }else{
            throw new DSqlNotSupportException();
        }
    }

    public static Object length(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName,context);

            if (p0 == null) {
                return null;
            }
            try {
                return p0.toString().getBytes(WebWriter.getEncoding()).length;
            } catch (Exception exception) {
                throw new DBFoundPackageException(exception);
            }
        }else{
            throw new DSqlNotSupportException();
        }
    }

    public static Object charLength(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName, context);
            if (p0 == null) {
                return null;
            }
            return p0.toString().length();
        }else{
            throw new DSqlNotSupportException();
        }
    }

    public static Object ifNull(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 2) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName,context);
            Object p1 = DSqlEngine.getExpressionValue(list.get(1), param, provideName,context);
            return p0 == null ? p1 : p0;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    public static Object isNull(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName,context );
            return p0 == null;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    public static Object ifExpress(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 3) {
            Object result =  DSqlEngine.getExpressionValue(list.get(0), param, provideName, context);
            boolean p0 = result != null && DSqlEngine.getBooleanValue(result);
            Object p1 = DSqlEngine.getExpressionValue(list.get(1), param, provideName,context);
            Object p2 = DSqlEngine.getExpressionValue(list.get(2), param, provideName,context);
            return p0 ? p1 : p2;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
