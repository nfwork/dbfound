package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NotExpression;

import java.util.List;

public class NotExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        NotExpression notExpression = (NotExpression) expression;
        Object object = DSqlEngine.getExpressionValue(notExpression.getExpression(),param,provideName,context);
        if(object == null){
            return null;
        }
        boolean result = getBooleanValue(object);
        return ! result;
   }
}