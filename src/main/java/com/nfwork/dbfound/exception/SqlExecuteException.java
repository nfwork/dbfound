package com.nfwork.dbfound.exception;

public class SqlExecuteException extends DBFoundPackageException{

    private final String provideName;

    private final String task ;

    private final String sql;

    public SqlExecuteException(String provideName, String task, String sql ,String message, Exception e) {
        super(message, e);
        this.provideName = provideName;
        this.task = task;
        this.sql = sql;
    }

    public String getTask() {
        return task;
    }

    public String getSql() {
        return sql;
    }

    public String getProvideName() {
        return provideName;
    }
}
