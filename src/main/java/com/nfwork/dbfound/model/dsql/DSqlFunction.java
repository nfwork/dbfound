package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.List;

public abstract class DSqlFunction {

    public Object apply(Function function, List<Object> sqlParamList, String provideName, Context context){
        String functionName = function.getMultipartName().get(0).toLowerCase();
        List<Object> fpList = new ArrayList<>();
        if(function.getParameters()!=null) {
            List<Expression> list = function.getParameters().getExpressions();
            if (list != null) {
                for (Expression expression : list) {
                    Object value = DSqlEngine.getExpressionValue(expression, sqlParamList, provideName, context);
                    fpList.add(value);
                }
            }
        }
        return doApply(functionName, fpList, provideName, context);
    }

    public abstract Object doApply(String functionName, List<Object> param, String provideName, Context context);

    public void register(Class<?> clazz){
        FunctionResolver.functionMap.put(clazz.getName(), this);
    }

    protected Object trim(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            if (p0 == null) {
                return null;
            }
            return p0.toString().trim();
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object length(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);

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

    protected Object charLength(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            if (p0 == null) {
                return null;
            }
            return p0.toString().length();
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object ifNull(List<Object> params){
        if (params.size() == 2) {
            Object p0 = params.get(0);
            Object p1 = params.get(1);
            return p0 == null ? p1 : p0;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object isNull(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            return p0 == null;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object ifExpress(List<Object> params){
        if (params.size() == 3) {
            Object result = params.get(0);
            boolean p0 = result != null && DSqlEngine.getBooleanValue(result);
            Object p1 = params.get(1);
            Object p2 = params.get(2);
            return p0 ? p1 : p2;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
