package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

import java.util.List;

public class AndExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        AndExpression andExpression = (AndExpression) expression;
        boolean leftValue = getBooleanValue(DSqlEngine.getExpressionValue(andExpression.getLeftExpression(),param, provideName,context));
        boolean rightValue = getBooleanValue(DSqlEngine.getExpressionValue(andExpression.getRightExpression(),param, provideName, context));
        return leftValue && rightValue;
    }
}