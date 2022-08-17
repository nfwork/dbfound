package com.nfwork.dbfound.db.dialect;

import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public abstract class AbstractSqlDialect implements SqlDialect{

    @Override
    public String getPagerSql(String sql, int pagerSize, long startWith) {
        return null;
    }

    public abstract String  getPagerSql(String sql, String limitHold, String startHold);

    public String  getPagerSql(String sql, int pagerSize, long startWith, Map<String, Param> params){
        Param start = new Param();
        start.setName("start");
        start.setValue(startWith);
        start.setDataType(DataType.NUMBER);
        start.setSourcePathHistory("param.start");
        params.put(start.getName(),start);

        Param limit = new Param();
        limit.setName("limit");
        limit.setValue(pagerSize);
        limit.setDataType(DataType.NUMBER);
        limit.setSourcePathHistory("param.limit");
        params.put(limit.getName(),limit);

        return getPagerSql(sql, "${@limit}","${@start}");
    }

}
