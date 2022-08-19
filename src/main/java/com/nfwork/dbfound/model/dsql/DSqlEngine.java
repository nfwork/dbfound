package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DSqlEngine {

    private static final DSqlCache<String,Expression> lruCache = new DSqlCache<>(5000, new ExpressionFunction());

    private static final Map<Class<? extends Expression>,DSqlValueResolver> resolverMap = new ConcurrentHashMap<>();

    private static final Expression NOT_SUPPORT_EXPRESSION = new Column();

    public static final String NOT_SUPPORT = "Not Support";

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
            Select select = (Select) CCJSqlParserUtil.parse("select 1 from dual where " + sql);
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            return selectBody.getWhere();
        }catch (Exception exception){
            return NOT_SUPPORT_EXPRESSION;
        }
    }

    static Object getExpressionValue(Expression expression , List<Object> param, String provideName, Context context){
        DSqlValueResolver resolver = resolverMap.get(expression.getClass());
        if(resolver == null){
            return null;
        }
        return resolver.getValue(expression,param,provideName,context);
    }

    static Boolean getBooleanValue(Object value){
        if(value == NOT_SUPPORT){
            return false;
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

    static {
        resolverMap.put(StringValue.class, new StringValueResolver());
        resolverMap.put(LongValue.class, new LongValueResolver());
        resolverMap.put(NullValue.class, new NullValueResolver());
        resolverMap.put(DoubleValue.class,new DoubleValueResolver());
        resolverMap.put(JdbcParameter.class,new JdbcParameterResolver());
        resolverMap.put(Column.class,new ColumnResolver());

        resolverMap.put(NotExpression.class, new NotExpressionResolver());
        resolverMap.put(AndExpression.class,new AndExpressionResolver());
        resolverMap.put(OrExpression.class, new OrExpressionResolver());
        resolverMap.put(IsNullExpression.class, new IsNullExpressionResolver());

        resolverMap.put(EqualsTo.class,new EqualsToResolver());
        resolverMap.put(NotEqualsTo.class, new NotEqualsToResolver());
        resolverMap.put(GreaterThan.class,new GreaterThanResolver());
        resolverMap.put(GreaterThanEquals.class,new GreaterThanEqualsResolver());
        resolverMap.put(MinorThan.class,new MinorThanResolver());
        resolverMap.put(MinorThanEquals.class,new MinorThanEqualsResolver());

        resolverMap.put(net.sf.jsqlparser.expression.Function.class, new FunctionResolver());

        resolverMap.put(Multiplication.class,new MultiplicationResolver());
        resolverMap.put(Division.class, new DivisionResolver());
        resolverMap.put(Addition.class,new AdditionResolver());
        resolverMap.put(Subtraction.class,new SubtractionResolver());
        resolverMap.put(Modulo.class, new ModuloResolver());
        resolverMap.put(Parenthesis.class, new ParenthesisResolver());
    }
}
