package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

public class NullValueResolver extends DSqlValueResolver {

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        return null;
    }
}