package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;

import java.util.List;

public class LikeResolver extends DSqlValueResolver {

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        LikeExpression likeExpression = (LikeExpression) expression;
        Object leftValue = DSqlEngine.getExpressionValue(likeExpression.getLeftExpression(),param,provideName,context);
        Object rightValue = DSqlEngine.getExpressionValue(likeExpression.getRightExpression(),param,provideName,context);

        //null compare always return null;；
        if(leftValue == null || rightValue == null){
            return null;
        }
        if(isCompareSupport(leftValue,rightValue)) {
            String left =  leftValue.toString();
            String right = rightValue.toString();
            if(DSqlConfig.isCompareIgnoreCase()){
                left = left.toLowerCase();
                right = right.toLowerCase();
            }
            right = right.replace("%",".*");

            return left.matches(right);
        }else{
            throw new DSqlNotSupportException();
        }
    }
}