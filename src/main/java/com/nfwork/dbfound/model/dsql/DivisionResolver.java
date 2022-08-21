package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DivisionResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Division division = (Division) expression;
        Object leftValue = DSqlEngine.getExpressionValue(division.getLeftExpression(),param,provideName,context);
        Object rightValue = DSqlEngine.getExpressionValue(division.getRightExpression(),param,provideName,context);
        //null compare always return null;；
        if(leftValue == null || rightValue == null){
            return null;
        }
        if(isCompareSupport(leftValue,rightValue)) {
            BigDecimal left = getBigDecimal(leftValue);
            BigDecimal right = getBigDecimal(rightValue);
            return left.divide(right,9, RoundingMode.HALF_UP).stripTrailingZeros() ;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}