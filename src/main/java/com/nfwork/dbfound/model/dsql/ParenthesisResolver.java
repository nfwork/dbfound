package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;

import java.util.List;

public class ParenthesisResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Parenthesis parenthesis = (Parenthesis) expression;
        return DSqlEngine.getExpressionValue(parenthesis.getExpression(),param,provideName,context);
    }
}