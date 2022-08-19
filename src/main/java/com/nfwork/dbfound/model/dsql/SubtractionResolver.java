package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;

import java.util.List;

public class SubtractionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Subtraction subtraction = (Subtraction) expression;
        Object leftValue = DSqlEngine.getExpressionValue(subtraction.getLeftExpression(),param,provideName,context);
        Object rightValue = DSqlEngine.getExpressionValue(subtraction.getRightExpression(),param,provideName,context);
        //null compare always return null;；
        if(leftValue == null || rightValue == null){
            return null;
        }
        if(isCompareSupport(leftValue,rightValue)) {
            double left = getDoubleValue(leftValue);
            double right = getDoubleValue(rightValue);
            return left - right;
        }else{
            return DSqlEngine.NOT_SUPPORT;
        }
    }
}