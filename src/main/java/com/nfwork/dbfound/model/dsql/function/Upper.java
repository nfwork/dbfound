package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.util.DataUtil;

import java.util.List;

public class Upper extends DSqlFunction {
    @Override
    public boolean isSupported(SqlDialect sqlDialect) {
        return true;
    }

    @Override
    public Object apply(List<Object> args, SqlDialect sqlDialect) {
        if (args.size() != 1) {
            throw new DSqlNotSupportException();
        }
        String str = DataUtil.stringValue(args.get(0));
        return str != null ? str.toUpperCase() : null;
    }
}