package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlEngine;

import java.util.List;

public abstract class DSqlFunction {

    public abstract boolean isSupported(SqlDialect sqlDialect);

    public abstract Object apply(List<Object> params,SqlDialect sqlDialect);

    protected Object trim(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            if (p0 == null) {
                return null;
            }
            return p0.toString().trim();
        }else{
            throw new DSqlNotSupportException();
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

    protected Object ifNull(List<Object> params){
        if (params.size() == 2) {
            Object p0 = params.get(0);
            Object p1 = params.get(1);
            return p0 == null ? p1 : p0;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object isNull(List<Object> params){
        if (params.size() == 1) {
            Object p0 = params.get(0);
            return p0 == null;
        }else{
            throw new DSqlNotSupportException();
        }
    }

    protected Object ifExpress(List<Object> params){
        if (params.size() == 3) {
            Object result = params.get(0);
            boolean p0 = result != null && DSqlEngine.getBooleanValue(result);
            Object p1 = params.get(1);
            Object p2 = params.get(2);
            return p0 ? p1 : p2;
        }else{
            throw new DSqlNotSupportException();
        }
    }
}
