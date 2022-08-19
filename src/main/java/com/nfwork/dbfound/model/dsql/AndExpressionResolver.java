package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

import java.util.List;

public class AndExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        AndExpression andExpression = (AndExpression) expression;
        Object left = DSqlEngine.getExpressionValue(andExpression.getLeftExpression(),param, provideName,context);
        Object right = DSqlEngine.getExpressionValue(andExpression.getRightExpression(),param, provideName, context);
        if(left == null || right == null){
            return null;
        }
        boolean leftValue = getBooleanValue(left);
        boolean rightValue = getBooleanValue(right);
        return leftValue && rightValue;
    }
}