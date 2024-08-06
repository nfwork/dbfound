package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.Between;

import java.util.List;

public class BetweenResolver extends DSqlValueResolver{

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Between between = (Between) expression;
        Object value = DSqlEngine.getExpressionValue(between.getLeftExpression(),param,provideName,context);
        Object start = DSqlEngine.getExpressionValue(between.getBetweenExpressionStart(),param,provideName,context);
        Object end = DSqlEngine.getExpressionValue(between.getBetweenExpressionEnd(),param,provideName,context);
        if(value == null || start == null || end == null){
            return null;
        }
        if(isBetweenSupport(value,start,end) ){
            if(compareTo(value, start) >= 0 && compareTo(value, end) <= 0) {
                return ! between.isNot();
            }
            return between.isNot();
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
