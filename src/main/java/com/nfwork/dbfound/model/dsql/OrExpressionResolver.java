package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;

import java.util.List;

public class OrExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        OrExpression orExpression = (OrExpression) expression;
        Boolean leftValue = getBooleanValue(DSqlEngine.getExpressionValue(orExpression.getLeftExpression(),param, provideName, context));
        Boolean rightValue = getBooleanValue(DSqlEngine.getExpressionValue(orExpression.getRightExpression(),param, provideName, context));
        if(leftValue != null  && rightValue != null){
            return leftValue || rightValue;
        }else{
            return DSqlEngine.NOT_SUPPORT;
        }
    }
}