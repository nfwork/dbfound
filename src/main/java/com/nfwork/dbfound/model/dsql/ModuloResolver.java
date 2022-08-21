package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;

import java.math.BigDecimal;
import java.util.List;

public class ModuloResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Modulo modulo = (Modulo) expression;
        Object leftValue = DSqlEngine.getExpressionValue(modulo.getLeftExpression(),param,provideName,context);
        Object rightValue = DSqlEngine.getExpressionValue(modulo.getRightExpression(),param,provideName,context);
        //null compare always return null;；
        if(leftValue == null || rightValue == null){
            return null;
        }
        if(isCompareSupport(leftValue,rightValue)) {
            BigDecimal left = getBigDecimal(leftValue);
            BigDecimal right = getBigDecimal(rightValue);
            return left.remainder(right);
        }else{
            throw new DSqlNotSupportException();
        }
    }
}