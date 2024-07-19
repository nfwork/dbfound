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
        if(left != null && getBooleanValue(left)){
            return true;
        }
        Object right = DSqlEngine.getExpressionValue(orExpression.getRightExpression(),param, provideName, context);
        if(right != null && getBooleanValue(right)){
            return true;
        }
        if(left == null || right == null){
            return null;
        }
        return false;
    }
}