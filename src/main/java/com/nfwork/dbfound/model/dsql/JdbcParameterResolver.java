package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.LocalDateUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;

import java.util.Date;
import java.util.List;

public class JdbcParameterResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        JdbcParameter jdbcParameter = (JdbcParameter) expression;
        int index = jdbcParameter.getIndex();
        Object result = param.get(index-1);
        if(result instanceof Date){
            return LocalDateUtil.formatDate((Date)result);
        }else{
            return result;
        }
    }
}