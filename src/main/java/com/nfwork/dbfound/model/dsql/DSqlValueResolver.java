package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.*;
import java.util.List;

public abstract class DSqlValueResolver {

    public abstract Object getValue(Expression expression , List<Object> param, String provideName, Context context);

    protected boolean equalsTo(Object leftValue, Object rightValue){
        if(leftValue instanceof Number || rightValue instanceof Number){
            double left = getDoubleValue(leftValue);
            double right = getDoubleValue(rightValue);
            return left == right;
        }else{
            String leftString = leftValue.toString();
            String rightString = rightValue.toString();
            if(DSqlConfig.isCompareIgnoreCase()) {
                return leftString.equalsIgnoreCase(rightString);
            }else{
                return leftString.equals(rightString);
            }
        }
    }

    protected boolean isCompareSupport(Object leftValue, Object rightValue){
        return  leftValue != DSqlEngine.NOT_SUPPORT && rightValue != DSqlEngine.NOT_SUPPORT
                && (leftValue instanceof Number || leftValue instanceof String)
                && (rightValue instanceof Number || rightValue instanceof String);
    }

    protected double compareTo(Object leftValue, Object rightValue){
        if(leftValue instanceof Number || rightValue instanceof Number){
            double left = getDoubleValue(leftValue);
            double right = getDoubleValue(rightValue);
            return left - right;
        }else{
            if(DSqlConfig.isCompareIgnoreCase()) {
                return leftValue.toString().compareToIgnoreCase(rightValue.toString());
            }else{
                return leftValue.toString().compareTo(rightValue.toString());
            }
        }
    }

    protected double getDoubleValue(Object value){
        assert value != null;
        return value instanceof Number?((Number)value).doubleValue():Double.parseDouble(value.toString());
    }

    protected Boolean getBooleanValue(Object value){
        return DSqlEngine.getBooleanValue(value);
    }

}
