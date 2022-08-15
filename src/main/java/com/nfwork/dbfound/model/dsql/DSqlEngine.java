package com.nfwork.dbfound.model.dsql;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.Date;
import java.util.List;

public class DSqlEngine {

    private static final String NOT_SUPPORT = "Not Support";

    public static String getWhenSql(String sql){
       return "select 1 from dual where " + sql;
    }

    public static Boolean checkWhenSql(String sql, List<Object> param){
        Expression expression = getExpression(sql);
        if(expression == null){
            return null;
        }
        Object result = getExpressionValue(expression,param);
        if(result instanceof Boolean){
            return (Boolean) result;
        } if(result instanceof Number){
            return ((Number) result).doubleValue() > 0;
        } else {
            return null;
        }
    }

    private static Expression getExpression(String sql) {
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            return selectBody.getWhere();
        }catch (Exception exception){
            return null;
        }
    }

    private static Object getExpressionValue(Expression expression , List<Object> param){
        if(expression instanceof StringValue){
            StringValue stringValue = (StringValue) expression;
            return stringValue.getValue();
        }
        if(expression instanceof LongValue){
            LongValue longValue = (LongValue) expression;
            return longValue.getValue();
        }
        if(expression instanceof NullValue){
            return null;
        }
        if(expression instanceof DoubleValue){
            DoubleValue doubleValue = (DoubleValue) expression;
            return doubleValue.getValue();
        }
        if(expression instanceof JdbcParameter){
            JdbcParameter jdbcParameter = (JdbcParameter) expression;
            int index = jdbcParameter.getIndex();
            return param.get(index-1);
        }
        if(expression instanceof Column){
            Column column = (Column) expression;
            if(column.getColumnName().equals("true")){
                return true;
            }else if(column.getColumnName().equals("false")){
                return false;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof AndExpression){
            AndExpression andExpression = (AndExpression) expression;
            Object leftValue = getExpressionValue(andExpression.getLeftExpression(),param);
            Object rightValue = getExpressionValue(andExpression.getRightExpression(),param);
            if(leftValue instanceof Boolean  && rightValue instanceof Boolean){
                return (Boolean)leftValue && (Boolean) rightValue;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof OrExpression){
            OrExpression orExpression = (OrExpression) expression;
            Object leftValue =  getExpressionValue(orExpression.getLeftExpression(),param);
            Object rightValue = getExpressionValue(orExpression.getRightExpression(),param);
            if(leftValue instanceof Boolean  && rightValue instanceof Boolean){
                return (Boolean)leftValue || (Boolean) rightValue;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof IsNullExpression){
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Object value = getExpressionValue(isNullExpression.getLeftExpression(),param);
            if(value == null && !isNullExpression.isNot()){
                return true;
            }else{
                return value != null && isNullExpression.isNot();
            }
        }

        if(expression instanceof EqualsTo){
            EqualsTo equalsTo = (EqualsTo) expression;
            Object leftValue = getExpressionValue(equalsTo.getLeftExpression(),param);
            Object rightValue  = getExpressionValue(equalsTo.getRightExpression(),param);

            if(leftValue == NOT_SUPPORT || rightValue == NOT_SUPPORT || leftValue instanceof Date || rightValue instanceof Date ){
                return NOT_SUPPORT;
            }
            return equalsTo(leftValue,rightValue);
        }

        if(expression instanceof NotEqualsTo){
            NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
            Object leftValue = getExpressionValue(notEqualsTo.getLeftExpression(),param);
            Object rightValue  = getExpressionValue(notEqualsTo.getRightExpression(),param);

            if(leftValue == NOT_SUPPORT || rightValue == NOT_SUPPORT || leftValue instanceof Date || rightValue instanceof Date ){
                return NOT_SUPPORT;
            }
            return ! equalsTo(leftValue,rightValue);
        }

        if(expression instanceof GreaterThan){
            GreaterThan greaterThan = (GreaterThan) expression;
            Object left = getExpressionValue(greaterThan.getLeftExpression(),param);
            Object right = getExpressionValue(greaterThan.getRightExpression(),param);
            if(left == NOT_SUPPORT || right == NOT_SUPPORT ||left instanceof Date || right instanceof Date){
                return NOT_SUPPORT;
            }
            return compareTo(left,right)>0;
        }

        if(expression instanceof GreaterThanEquals){
            GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
            Object left = getExpressionValue(greaterThanEquals.getLeftExpression(),param);
            Object right = getExpressionValue(greaterThanEquals.getRightExpression(),param);
            if(left == NOT_SUPPORT || right == NOT_SUPPORT ||left instanceof Date || right instanceof Date){
                return NOT_SUPPORT;
            }
            return compareTo(left, right) >= 0;
        }

        if(expression instanceof MinorThan){
            MinorThan minorThan = (MinorThan) expression;
            Object left = getExpressionValue(minorThan.getLeftExpression(),param);
            Object right = getExpressionValue(minorThan.getRightExpression(),param);
            if(left == NOT_SUPPORT || right == NOT_SUPPORT ||left instanceof Date || right instanceof Date){
                return NOT_SUPPORT;
            }
            return compareTo(left, right) < 0;
        }

        if(expression instanceof MinorThanEquals){
            MinorThanEquals minorThanEquals = (MinorThanEquals) expression;
            Object left = getExpressionValue(minorThanEquals.getLeftExpression(),param);
            Object right = getExpressionValue(minorThanEquals.getRightExpression(),param);
            if(left == NOT_SUPPORT || right == NOT_SUPPORT ||left instanceof Date || right instanceof Date){
                return NOT_SUPPORT;
            }
            return compareTo(left, right) <= 0;
        }

        return NOT_SUPPORT;
    }

    private static boolean equalsTo(Object leftValue, Object rightValue){
        if(leftValue == null && rightValue == null){
            return true;
        }
        if(leftValue == null || rightValue == null){
            return false;
        }
        if(leftValue instanceof Number || rightValue instanceof Number){
            try {
                double left = leftValue instanceof Number?((Number)leftValue).doubleValue():Double.parseDouble(leftValue.toString());
                double right = rightValue instanceof Number?((Number)rightValue).doubleValue():Double.parseDouble(rightValue.toString());
                return left == right;
            }catch (NumberFormatException exception){
                return false;
            }
        }else{
            String leftString = leftValue.toString();
            String rightString = rightValue.toString();
            return !leftString.equalsIgnoreCase(rightString);
        }
    }

    private static double compareTo(Object leftValue, Object rightValue){
        if(leftValue == null && rightValue == null){
            return 0;
        }
        if(leftValue == null){
            return -1;
        }
        if(rightValue == null){
            return  1;
        }
        if(leftValue instanceof Number || rightValue instanceof Number){
            double left = leftValue instanceof Number?((Number)leftValue).doubleValue():Double.parseDouble(leftValue.toString());
            double right = rightValue instanceof Number?((Number)rightValue).doubleValue():Double.parseDouble(rightValue.toString());
            return left - right;
        }else{
            return leftValue.toString().compareTo(rightValue.toString());
        }
    }

}
