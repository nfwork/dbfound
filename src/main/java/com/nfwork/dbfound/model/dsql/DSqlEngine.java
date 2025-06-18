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

    private static final Map<String,DSqlValueResolver> resolverMap = new ConcurrentHashMap<>();

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
        DSqlValueResolver resolver = resolverMap.get(expression.getClass().getName());
        if(resolver == null){
            throw new DSqlNotSupportException();
        }
        return resolver.getValue(expression,param,provideName,context);
    }

    public static boolean getBooleanValue(Object value){
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
        resolverMap.put(StringValue.class.getName(), new StringValueResolver());
        resolverMap.put(LongValue.class.getName(), new LongValueResolver());
        resolverMap.put(NullValue.class.getName(), new NullValueResolver());
        resolverMap.put(DoubleValue.class.getName(),new DoubleValueResolver());
        resolverMap.put(JdbcParameter.class.getName(),new JdbcParameterResolver());
        resolverMap.put(Column.class.getName(),new ColumnResolver());

        resolverMap.put(NotExpression.class.getName(), new NotExpressionResolver());
        resolverMap.put(AndExpression.class.getName(),new AndExpressionResolver());
        resolverMap.put(OrExpression.class.getName(), new OrExpressionResolver());
        resolverMap.put(IsNullExpression.class.getName(), new IsNullExpressionResolver());

        resolverMap.put(EqualsTo.class.getName(),new EqualsToResolver());
        resolverMap.put(NotEqualsTo.class.getName(), new NotEqualsToResolver());
        resolverMap.put(GreaterThan.class.getName(),new GreaterThanResolver());
        resolverMap.put(GreaterThanEquals.class.getName(),new GreaterThanEqualsResolver());
        resolverMap.put(MinorThan.class.getName(),new MinorThanResolver());
        resolverMap.put(MinorThanEquals.class.getName(),new MinorThanEqualsResolver());
        resolverMap.put(LikeExpression.class.getName(),new LikeResolver());
        resolverMap.put(InExpression.class.getName(),new InResolver());
        resolverMap.put(Between.class.getName(),new BetweenResolver());

        resolverMap.put(Function.class.getName(), new FunctionResolver());

        resolverMap.put(Multiplication.class.getName(),new MultiplicationResolver());
        resolverMap.put(Division.class.getName(), new DivisionResolver());
        resolverMap.put(Addition.class.getName(),new AdditionResolver());
        resolverMap.put(Subtraction.class.getName(),new SubtractionResolver());
        resolverMap.put(Modulo.class.getName(), new ModuloResolver());
        resolverMap.put(SignedExpression.class.getName(), new SignedExpressionResolver());
        resolverMap.put(Parenthesis.class.getName(), new ParenthesisResolver());
    }
}
