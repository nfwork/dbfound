package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.List;

public abstract class DSqlFunction {

    public abstract Object apply(String functionName, Function function, List<Object> param, String provideName, Context context);

    protected Object getExpressionValue(Expression expression , List<Object> param, String provideName, Context context){
        return DSqlEngine.getExpressionValue(expression, param, provideName, context);
    }

    protected Object trim(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = getExpressionValue(list.get(0), param, provideName, context);
            if (p0 == null) {
                return null;
            }
            return p0.toString().trim();
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object length(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = getExpressionValue(list.get(0), param, provideName,context);

            if (p0 == null) {
                return null;
            }
            try {
                return p0.toString().getBytes(DBFoundConfig.getEncoding()).length;
            } catch (Exception exception) {
                throw new DBFoundPackageException(exception);
            }
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object charLength(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = getExpressionValue(list.get(0), param, provideName, context);
            if (p0 == null) {
                return null;
            }
            return p0.toString().length();
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object ifNull(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 2) {
            Object p0 = getExpressionValue(list.get(0), param, provideName,context);
            Object p1 = getExpressionValue(list.get(1), param, provideName,context);
            return p0 == null ? p1 : p0;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object isNull(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 1) {
            Object p0 = getExpressionValue(list.get(0), param, provideName,context );
            return p0 == null;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object ifExpress(Function function,List<Object> param, String provideName, Context context){
        List<Expression> list = function.getParameters().getExpressions();
        if (list.size() == 3) {
            Object result =  DSqlEngine.getExpressionValue(list.get(0), param, provideName, context);
            boolean p0 = result != null && DSqlEngine.getBooleanValue(result);
            Object p1 = getExpressionValue(list.get(1), param, provideName,context);
            Object p2 = getExpressionValue(list.get(2), param, provideName,context);
            return p0 ? p1 : p2;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
