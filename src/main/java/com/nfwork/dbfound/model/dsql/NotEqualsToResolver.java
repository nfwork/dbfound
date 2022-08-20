package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;

import java.util.List;

public class NotEqualsToResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
        Object leftValue = DSqlEngine.getExpressionValue(notEqualsTo.getLeftExpression(),param, provideName, context);
        Object rightValue  = DSqlEngine.getExpressionValue(notEqualsTo.getRightExpression(),param, provideName, context);

        //null compare always return null;
        if(leftValue == null || rightValue == null){
            return null;
        }
        if(isEqualsSupport(leftValue,rightValue)){
            return !equalsTo(leftValue,rightValue);
        }else{
            throw new DSqlNotSupportException();
        }
    }
}