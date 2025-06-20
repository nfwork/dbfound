package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.db.dialect.OracleDialect;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;

import java.util.List;

public class Length extends DSqlFunction {

    @Override
    public Object apply(List<Object> params,SqlDialect sqlDialect) {
        if (sqlDialect instanceof OracleDialect){
            return charLength(params);
        }else{
            return length(params);
        }
    }

    protected Object length(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);

            if (p0 == null) {
                return null;
            }
            try {
                return p0.toString().getBytes(DBFoundConfig.getEncoding()).length;
            } catch (Exception exception) {
                throw new DBFoundPackageException(exception);
            }
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object charLength(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            if (p0 == null) {
                return null;
            }
            return p0.toString().length();
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
