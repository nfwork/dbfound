package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.util.LocalDateUtil;

import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

public class Concat extends DSqlFunction {


    @Override
    public Object apply(List<Object> list, SqlDialect sqlDialect) {
        // CONCAT 需要至少一个参数
        if (list.isEmpty()) {
            throw new DSqlNotSupportException();
        }
        StringBuilder result = new StringBuilder();
        
        for (Object arg : list) {
            if (arg == null) {
                // MySQL 中 CONCAT 遇到 NULL 会返回 NULL
                return null;
            }
            if (arg instanceof Temporal) {
                result.append(LocalDateUtil.formatTemporal((Temporal) arg));
            } else if (arg instanceof Date) {
                result.append(LocalDateUtil.formatDate((Date) arg));
            } else {
                result.append(arg);
            }
        }
        
        return result.toString();
    }

    @Override
    public boolean isSupported(SqlDialect sqlDialect) {
        return true;
    }
}