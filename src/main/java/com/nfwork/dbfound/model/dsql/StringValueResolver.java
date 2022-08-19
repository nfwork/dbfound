package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.List;

public class StringValueResolver extends DSqlValueResolver {

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        StringValue stringValue = (StringValue) expression;
        return stringValue.getValue();
    }

}