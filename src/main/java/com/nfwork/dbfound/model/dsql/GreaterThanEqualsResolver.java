package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;

import java.util.List;

public class GreaterThanEqualsResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {

        GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
        Object left = DSqlEngine.getExpressionValue(greaterThanEquals.getLeftExpression(),param,provideName, context);
        Object right = DSqlEngine.getExpressionValue(greaterThanEquals.getRightExpression(),param,provideName, context);

        //null compare always return null;
        if(left == null || right == null){
            return null;
        }
        if(isCompareSupport(left,right)) {
            return compareTo(left, right) >= 0;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}