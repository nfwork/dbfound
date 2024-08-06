package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.cache.LruCache;
import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
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

public class DSqlEngine {

    private static final LruCache<String,Expression> lruCache = new LruCache<>(5000, DSqlEngine::getExpression);

    private static final Map<Class<? extends Expression>,DSqlValueResolver> resolverMap = new ConcurrentHashMap<>();

    private static final Expression NOT_SUPPORT_EXPRESSION = new Column();

    public static boolean checkWhenSql(String sql, List<Object> param, String provideName, Context context){
        try {
            Expression expression = lruCache.get(sql);
            if (expression == NOT_SUPPORT_EXPRESSION) {
                throw new DSqlNotSupportException();
            }
            Object result = getExpressionValue(expression, param, provideName, context);
            if(result == null){
                return false;
            }else {
                return getBooleanValue(result);
            }
        }catch (DSqlNotSupportException exception){
            exception.setMessage("sql grammar or sql param type is not supported, sql: " + sql + ", param: " + param);
            throw exception;
        }
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
            throw new DSqlNotSupportException();
        }
        return resolver.getValue(expression,param,provideName,context);
    }

    static boolean getBooleanValue(Object value){
        if(value instanceof Boolean){
            return (boolean) value;
        }
        if(value instanceof Number){
           return ((Number) value).intValue() != 0;
        }
        if(value instanceof String){
            int d = Integer.parseInt((String) value);
            return d != 0;
        }
        throw new DSqlNotSupportException();
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
        resolverMap.put(LikeExpression.class,new LikeResolver());
        resolverMap.put(InExpression.class,new InResolver());
        resolverMap.put(Between.class,new BetweenResolver());

        resolverMap.put(net.sf.jsqlparser.expression.Function.class, new FunctionResolver());

        resolverMap.put(Multiplication.class,new MultiplicationResolver());
        resolverMap.put(Division.class, new DivisionResolver());
        resolverMap.put(Addition.class,new AdditionResolver());
        resolverMap.put(Subtraction.class,new SubtractionResolver());
        resolverMap.put(Modulo.class, new ModuloResolver());
        resolverMap.put(SignedExpression.class, new SignedExpressionResolver());
        resolverMap.put(Parenthesis.class, new ParenthesisResolver());
    }
}
