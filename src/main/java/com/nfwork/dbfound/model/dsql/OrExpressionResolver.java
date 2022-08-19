package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;

import java.util.List;

public class OrExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        OrExpression orExpression = (OrExpression) expression;
        Object left = DSqlEngine.getExpressionValue(orExpression.getLeftExpression(),param, provideName, context);
        Object right = DSqlEngine.getExpressionValue(orExpression.getRightExpression(),param, provideName, context);
        if(left == null && right == null){
            return null;
        }
        if(left == null){
            return getBooleanValue(right);
        }
        if(right == null){
            return getBooleanValue(left);
        }
        boolean leftValue = getBooleanValue(left);
        boolean rightValue = getBooleanValue(right);
        return leftValue || rightValue;
    }
}