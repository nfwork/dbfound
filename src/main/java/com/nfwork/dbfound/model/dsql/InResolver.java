package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;

import java.util.List;

public class InResolver extends DSqlValueResolver {

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        InExpression inExpression = (InExpression) expression;

        Object leftValue = DSqlEngine.getExpressionValue(inExpression.getLeftExpression(),param,provideName,context);
        if(leftValue == null){
            return null;
        }
        if(!isEqualsSupport(leftValue)){
            throw new DSqlNotSupportException();
        }
        if(inExpression.getRightItemsList() instanceof ExpressionList){
            ExpressionList expressionList = (ExpressionList) inExpression.getRightItemsList();
            List<Expression> list = expressionList.getExpressions();
            boolean hasNull = false;
            for(Expression express : list){
                Object value = DSqlEngine.getExpressionValue(express,param,provideName,context);
                if(value == null){
                    hasNull = true;
                    continue;
                }
                if(isEqualsSupport(value)){
                    boolean result = equalsTo(leftValue,value);
                    if(result){
                        if(inExpression.isNot()){
                            return false;
                        }else {
                            return true;
                        }
                    }
                }else{
                    throw new DSqlNotSupportException();
                }
            }
            if(hasNull){
                return null;
            }
            if(inExpression.isNot()){
                return true;
            }else {
                return false;
            }
        }else{
            throw new DSqlNotSupportException();
        }
    }
}