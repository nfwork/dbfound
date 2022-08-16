package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.web.WebWriter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public class MySqlFunction {

    static Object apply(Expression expression, List<Object> param, String provideName){
        net.sf.jsqlparser.expression.Function function = (net.sf.jsqlparser.expression.Function)expression;
        String functionName = function.getName().toLowerCase();
        switch (functionName) {
            case "trim": {
               return trim(function,param,provideName);
            }
            case "length": {
                return length(function,param,provideName);
            }
            case "char_length": {
                return charLength(function,param,provideName);
            }
            case "ifnull": {
                return ifNull(function,param,provideName);
            }
            case "if": {
                return ifExpress(function,param,provideName);
            }
            default:{
                return DSqlEngine.NOT_SUPPORT;
            }
        }
    }

    public static String trim(Function function,List<Object> param, String provideName){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName);
            if(p0 == null){
                return null;
            }
            return p0.toString().trim();
        }
        return DSqlEngine.NOT_SUPPORT;
    }

    public static Object length(Function function,List<Object> param, String provideName){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName);
            if(p0 == null){
                return null;
            }
            try{
                return p0.toString().getBytes(WebWriter.getEncoding()).length;
            }catch (Exception exception){
                throw new DBFoundPackageException(exception);
            }
        }
        return DSqlEngine.NOT_SUPPORT;
    }

    public static Object charLength(Function function,List<Object> param, String provideName){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName);
            if(p0 == null){
                return null;
            }
            return p0.toString().length();
        }
        return DSqlEngine.NOT_SUPPORT;
    }

    public static Object ifNull(Function function,List<Object> param, String provideName){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 2) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName);
            Object p1 = DSqlEngine.getExpressionValue(list.get(1), param, provideName);
            return p0 == null ? p1 : p0;
        }
        return DSqlEngine.NOT_SUPPORT;
    }

    public static Object ifExpress(Function function,List<Object> param, String provideName){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 3) {
            Object p0 = DSqlEngine.getExpressionValue(list.get(0), param, provideName);
            Object p1 = DSqlEngine.getExpressionValue(list.get(1), param, provideName);
            Object p2 = DSqlEngine.getExpressionValue(list.get(2), param, provideName);
            if(p0 instanceof Boolean){
                return (Boolean) p0 ? p1 : p2;
            }
        }
        return DSqlEngine.NOT_SUPPORT;
    }

}
