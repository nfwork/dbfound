package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;

import java.util.List;

public class AdditionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Addition addition = (Addition) expression;
        Object leftValue = DSqlEngine.getExpressionValue(addition.getLeftExpression(),param,provideName,context);
        Object rightValue = DSqlEngine.getExpressionValue(addition.getRightExpression(),param,provideName,context);

        //null compare always return null;；
        if(leftValue == null || rightValue == null){
            return null;
        }
        if(isCompareSupport(leftValue,rightValue)) {
            double left = getDoubleValue(leftValue);
            double right = getDoubleValue(rightValue);
            return left + right;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}