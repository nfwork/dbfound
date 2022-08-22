package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.SignedExpression;
import java.util.List;

public class SignedExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        SignedExpression signedExpression = (SignedExpression) expression;
        Object value = DSqlEngine.getExpressionValue(signedExpression.getExpression(),param,provideName,context);
        if(signedExpression.getSign() == '-'){
            return getDoubleValue(value) * -1;
        }else if(signedExpression.getSign() == '+'){
            return value;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}