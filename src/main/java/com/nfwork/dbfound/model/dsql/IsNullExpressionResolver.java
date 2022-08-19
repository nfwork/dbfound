package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;

import java.util.List;

public class IsNullExpressionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {

        IsNullExpression isNullExpression = (IsNullExpression) expression;
        Object value = DSqlEngine.getExpressionValue(isNullExpression.getLeftExpression(),param, provideName,context);
        if(value == null && !isNullExpression.isNot()){
            return true;
        }else{
            return value != null && isNullExpression.isNot();
        }
    }
}