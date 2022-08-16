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
import java.util.function.Function;

public class DSqlEngine {

    private static final DSqlCache<String,Expression> lruCache = new DSqlCache<>(5000, new ExpressionFunction());

    private static final String NOT_SUPPORT = "Not Support";

    private static final Expression NOT_SUPPORT_EXPRESSION = new Column();

    public static String getWhenSql(String sql){
       return "select 1 from dual where " + sql;
    }

    public static class ExpressionFunction implements Function<String,Expression> {
        @Override
        public Expression apply(String sql) {
            return getExpression(sql);
        }
    }

    public static Boolean checkWhenSql(String sql, List<Object> param){
        Expression expression =  lruCache.get(sql);
        if(expression == NOT_SUPPORT_EXPRESSION){
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
            return NOT_SUPPORT_EXPRESSION;
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
            Object result = param.get(index-1);
            if(result instanceof Boolean){
                return (Boolean)result?1:0;
            }else{
                return result;
            }
        }
        if(expression instanceof Column){
            Column column = (Column) expression;
            if(column.getColumnName().equals("true")){
                return 1;
            }else if(column.getColumnName().equals("false")){
                return 0;
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

            //null compare always return false，user is null instead；
            if(leftValue == null || rightValue == null){
                return false;
            }
            if(isCompareSupport(leftValue,rightValue)){
                return equalsTo(leftValue,rightValue);
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof NotEqualsTo){
            NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
            Object leftValue = getExpressionValue(notEqualsTo.getLeftExpression(),param);
            Object rightValue  = getExpressionValue(notEqualsTo.getRightExpression(),param);

            //null compare always return false，user is null instead；
            if(leftValue == null || rightValue == null){
                return false;
            }
            if(isCompareSupport(leftValue,rightValue)){
                return !equalsTo(leftValue,rightValue);
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof GreaterThan){
            GreaterThan greaterThan = (GreaterThan) expression;
            Object left = getExpressionValue(greaterThan.getLeftExpression(),param);
            Object right = getExpressionValue(greaterThan.getRightExpression(),param);

            //null compare always return false，user is null instead；
            if(left == null || right == null){
                return false;
            }
            if(isCompareSupport(left,right)){
                return compareTo(left,right)>0;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof GreaterThanEquals){
            GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
            Object left = getExpressionValue(greaterThanEquals.getLeftExpression(),param);
            Object right = getExpressionValue(greaterThanEquals.getRightExpression(),param);

            //null compare always return false，user is null instead；
            if(left == null || right == null){
                return false;
            }
            if(isCompareSupport(left,right)) {
                return compareTo(left, right) >= 0;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof MinorThan){
            MinorThan minorThan = (MinorThan) expression;
            Object left = getExpressionValue(minorThan.getLeftExpression(),param);
            Object right = getExpressionValue(minorThan.getRightExpression(),param);

            //null compare always return false，user is null instead；
            if(left == null || right == null){
                return false;
            }
            if(isCompareSupport(left,right)) {
                return compareTo(left, right) < 0;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof MinorThanEquals){
            MinorThanEquals minorThanEquals = (MinorThanEquals) expression;
            Object left = getExpressionValue(minorThanEquals.getLeftExpression(),param);
            Object right = getExpressionValue(minorThanEquals.getRightExpression(),param);

            //null compare always return false，user is null instead；
            if(left == null || right == null){
                return false;
            }
            if(isCompareSupport(left,right)) {
                return compareTo(left, right) <= 0;
            }else {
                return NOT_SUPPORT;
            }
        }

        return NOT_SUPPORT;
    }

    private static boolean equalsTo(Object leftValue, Object rightValue){
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
            if(DSqlConfig.isEqualsIgnoreCase()) {
                return leftString.equalsIgnoreCase(rightString);
            }else{
                return leftString.equals(rightString);
            }
        }
    }

    private static boolean isCompareSupport(Object leftValue, Object rightValue){
        return leftValue != NOT_SUPPORT && rightValue != NOT_SUPPORT &&
                !(leftValue instanceof Date) && !(rightValue instanceof Date);
    }

    private static double compareTo(Object leftValue, Object rightValue){
        if(leftValue instanceof Number || rightValue instanceof Number){
            double left = leftValue instanceof Number?((Number)leftValue).doubleValue():Double.parseDouble(leftValue.toString());
            double right = rightValue instanceof Number?((Number)rightValue).doubleValue():Double.parseDouble(rightValue.toString());
            return left - right;
        }else{
            return leftValue.toString().compareTo(rightValue.toString());
        }
    }

}
