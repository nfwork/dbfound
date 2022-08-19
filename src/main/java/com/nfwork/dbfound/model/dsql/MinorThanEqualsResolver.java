package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;

import java.util.List;

public class MinorThanEqualsResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        MinorThanEquals minorThanEquals = (MinorThanEquals) expression;
        Object left = DSqlEngine.getExpressionValue(minorThanEquals.getLeftExpression(),param,provideName,context);
        Object right = DSqlEngine.getExpressionValue(minorThanEquals.getRightExpression(),param,provideName,context);

        //null compare always return null；
        if(left == null || right == null){
            return null;
        }
        if(isCompareSupport(left,right)) {
            return compareTo(left, right) <= 0;
        }else{
            return DSqlEngine.NOT_SUPPORT;
        }
    }
}