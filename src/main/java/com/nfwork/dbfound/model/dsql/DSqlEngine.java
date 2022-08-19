package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.db.dialect.MySqlDialect;
import com.nfwork.dbfound.db.dialect.OracleDialect;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class DSqlEngine {

    private static final DSqlCache<String,Expression> lruCache = new DSqlCache<>(5000, new ExpressionFunction());

    static final String NOT_SUPPORT = "Not Support";

    private static final Expression NOT_SUPPORT_EXPRESSION = new Column();

    public static class ExpressionFunction implements Function<String,Expression> {
        @Override
        public Expression apply(String sql) {
            return getExpression(sql);
        }
    }

    public static Boolean checkWhenSql(String sql, List<Object> param, String provideName, Context context){
        Expression expression =  lruCache.get(sql);
        if(expression == NOT_SUPPORT_EXPRESSION){
            return null;
        }
        Object result = getExpressionValue(expression,param,provideName, context);
        return getBooleanValue(result);
    }

    private static Expression getExpression(String sql) {
        try {
            return CCJSqlParserUtil.parseExpression(sql);
        }catch (Exception exception){
            return NOT_SUPPORT_EXPRESSION;
        }
    }

    static Object getExpressionValue(Expression expression , List<Object> param, String provideName, Context context){
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
            if(result instanceof java.sql.Date){
                return context.getDateFormat().format((java.sql.Date)result);
            }else if(result instanceof Date){
                return context.getDateTimeFormat().format((Date)result);
            }else{
                return result;
            }
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

        if(expression instanceof NotExpression){
            NotExpression notExpression = (NotExpression) expression;
            Boolean result = getBooleanValue(getExpressionValue(notExpression.getExpression(),param,provideName,context));
            if(result != null){
                return result;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof AndExpression){
            AndExpression andExpression = (AndExpression) expression;
            Boolean leftValue = getBooleanValue(getExpressionValue(andExpression.getLeftExpression(),param, provideName,context));
            Boolean rightValue = getBooleanValue(getExpressionValue(andExpression.getRightExpression(),param, provideName, context));
            if(leftValue != null  && rightValue != null){
                return leftValue && rightValue;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof OrExpression){
            OrExpression orExpression = (OrExpression) expression;
            Boolean leftValue = getBooleanValue(getExpressionValue(orExpression.getLeftExpression(),param, provideName, context));
            Boolean rightValue = getBooleanValue(getExpressionValue(orExpression.getRightExpression(),param, provideName, context));
            if(leftValue != null  && rightValue != null){
                return leftValue || rightValue;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof IsNullExpression){
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            Object value = getExpressionValue(isNullExpression.getLeftExpression(),param, provideName,context);
            if(value == null && !isNullExpression.isNot()){
                return true;
            }else{
                return value != null && isNullExpression.isNot();
            }
        }

        if(expression instanceof EqualsTo){
            EqualsTo equalsTo = (EqualsTo) expression;
            Object leftValue = getExpressionValue(equalsTo.getLeftExpression(),param, provideName,context);
            Object rightValue  = getExpressionValue(equalsTo.getRightExpression(),param, provideName, context);

            //null compare always return null;
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)){
                return equalsTo(leftValue,rightValue);
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof NotEqualsTo){
            NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
            Object leftValue = getExpressionValue(notEqualsTo.getLeftExpression(),param, provideName, context);
            Object rightValue  = getExpressionValue(notEqualsTo.getRightExpression(),param, provideName, context);

            //null compare always return null;
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)){
                return !equalsTo(leftValue,rightValue);
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof GreaterThan){
            GreaterThan greaterThan = (GreaterThan) expression;
            Object left = getExpressionValue(greaterThan.getLeftExpression(),param, provideName, context);
            Object right = getExpressionValue(greaterThan.getRightExpression(),param, provideName, context);

            //null compare always return null;
            if(left == null || right == null){
                return null;
            }
            if(isCompareSupport(left,right)){
                return compareTo(left,right)>0;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof GreaterThanEquals){
            GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
            Object left = getExpressionValue(greaterThanEquals.getLeftExpression(),param,provideName, context);
            Object right = getExpressionValue(greaterThanEquals.getRightExpression(),param,provideName, context);

            //null compare always return null;
            if(left == null || right == null){
                return null;
            }
            if(isCompareSupport(left,right)) {
                return compareTo(left, right) >= 0;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof MinorThan){
            MinorThan minorThan = (MinorThan) expression;
            Object left = getExpressionValue(minorThan.getLeftExpression(),param,provideName,context);
            Object right = getExpressionValue(minorThan.getRightExpression(),param, provideName, context);

            //null compare always return null;
            if(left == null || right == null){
                return null;
            }
            if(isCompareSupport(left,right)) {
                return compareTo(left, right) < 0;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof MinorThanEquals){
            MinorThanEquals minorThanEquals = (MinorThanEquals) expression;
            Object left = getExpressionValue(minorThanEquals.getLeftExpression(),param,provideName,context);
            Object right = getExpressionValue(minorThanEquals.getRightExpression(),param,provideName,context);

            //null compare always return null；
            if(left == null || right == null){
                return null;
            }
            if(isCompareSupport(left,right)) {
                return compareTo(left, right) <= 0;
            }else {
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof net.sf.jsqlparser.expression.Function){
            ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
            if(provide.getSqlDialect() instanceof MySqlDialect){
               return MySqlFunction.apply(expression,param,provideName,context);
            }else if(provide.getSqlDialect() instanceof OracleDialect){
                return OracleFunction.apply(expression,param,provideName,context);
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof Multiplication) {
            Multiplication multiplication = (Multiplication) expression;
            Object leftValue = getExpressionValue(multiplication.getLeftExpression(),param,provideName,context);
            Object rightValue = getExpressionValue(multiplication.getRightExpression(),param,provideName,context);
            //null compare always return null;；
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)) {
                double left = getDoubleValue(leftValue);
                double right = getDoubleValue(rightValue);
                return left * right;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof Division) {
            Division division = (Division) expression;
            Object leftValue = getExpressionValue(division.getLeftExpression(),param,provideName,context);
            Object rightValue = getExpressionValue(division.getRightExpression(),param,provideName,context);
            //null compare always return null;；
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)) {
                double left = getDoubleValue(leftValue);
                double right = getDoubleValue(rightValue);
                return left / right;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof Addition) {
            Addition addition = (Addition) expression;
            Object leftValue = getExpressionValue(addition.getLeftExpression(),param,provideName,context);
            Object rightValue = getExpressionValue(addition.getRightExpression(),param,provideName,context);
            //null compare always return null;；
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)) {
                double left = getDoubleValue(leftValue);
                double right = getDoubleValue(rightValue);
                return left + right;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof Subtraction) {
            Subtraction subtraction = (Subtraction) expression;
            Object leftValue = getExpressionValue(subtraction.getLeftExpression(),param,provideName,context);
            Object rightValue = getExpressionValue(subtraction.getRightExpression(),param,provideName,context);
            //null compare always return null;；
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)) {
                double left = getDoubleValue(leftValue);
                double right = getDoubleValue(rightValue);
                return left - right;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof Modulo) {
            Modulo modulo = (Modulo) expression;
            Object leftValue = getExpressionValue(modulo.getLeftExpression(),param,provideName,context);
            Object rightValue = getExpressionValue(modulo.getRightExpression(),param,provideName,context);
            //null compare always return null;；
            if(leftValue == null || rightValue == null){
                return null;
            }
            if(isCompareSupport(leftValue,rightValue)) {
                double left = getDoubleValue(leftValue);
                double right = getDoubleValue(rightValue);
                return left % right;
            }else{
                return NOT_SUPPORT;
            }
        }

        if(expression instanceof Parenthesis){
            Parenthesis parenthesis = (Parenthesis) expression;
            return getExpressionValue(parenthesis.getExpression(),param,provideName,context);
        }

        return NOT_SUPPORT;
    }

    private static boolean equalsTo(Object leftValue, Object rightValue){
        if(leftValue instanceof Number || rightValue instanceof Number){
            double left = getDoubleValue(leftValue);
            double right = getDoubleValue(rightValue);
            return left == right;
        }else{
            String leftString = leftValue.toString();
            String rightString = rightValue.toString();
            if(DSqlConfig.isCompareIgnoreCase()) {
                return leftString.equalsIgnoreCase(rightString);
            }else{
                return leftString.equals(rightString);
            }
        }
    }

    private static boolean isCompareSupport(Object leftValue, Object rightValue){
        return leftValue != NOT_SUPPORT && rightValue != NOT_SUPPORT
                && (leftValue instanceof Number || leftValue instanceof String)
                && (rightValue instanceof Number || rightValue instanceof String);
    }

    private static double compareTo(Object leftValue, Object rightValue){
        if(leftValue instanceof Number || rightValue instanceof Number){
            double left = getDoubleValue(leftValue);
            double right = getDoubleValue(rightValue);
            return left - right;
        }else{
            if(DSqlConfig.isCompareIgnoreCase()) {
                return leftValue.toString().compareToIgnoreCase(rightValue.toString());
            }else{
                return leftValue.toString().compareTo(rightValue.toString());
            }
        }
    }

    private static double getDoubleValue(Object value){
        assert value != null;
        return value instanceof Number?((Number)value).doubleValue():Double.parseDouble(value.toString());
    }

    static Boolean getBooleanValue(Object value){
        if(value == DSqlEngine.NOT_SUPPORT){
            return null;
        }
        if(value instanceof Boolean){
            return (Boolean) value;
        }
        if(value instanceof Number){
           return ((Number) value).intValue() != 0;
        }
        if(value instanceof String){
            int d = Integer.parseInt((String) value);
            return d != 0;
        }
        return null;
    }

}
