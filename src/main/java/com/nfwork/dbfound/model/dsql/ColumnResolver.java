package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

public class ColumnResolver extends DSqlValueResolver {
    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        Column column = (Column) expression;
        if(column.getColumnName().equals("true")){
            return true;
        }else if(column.getColumnName().equals("false")){
            return false;
        }else{
            return DSqlEngine.NOT_SUPPORT;
        }
    }
}