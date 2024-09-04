package com.nfwork.dbfound.model.base;

import java.util.List;

public class Count {

    String countSql;

    boolean executeCount = true;

    int dataSize ;

    long totalCounts;

    List<Object> sqlParams;

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    public boolean isExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(boolean executeCount) {
        this.executeCount = executeCount;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public long getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(long totalCounts) {
        this.totalCounts = totalCounts;
    }

    public List<Object> getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(List<Object> sqlParams) {
        this.sqlParams = sqlParams;
    }
}
