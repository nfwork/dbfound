package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.LocalDateUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
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
        }else if (result instanceof Temporal) {
            return LocalDateUtil.formatTemporal((Temporal) result);
        }else{
            return result;
        }
    }
}