package com.nfwork.dbfound.exception;

public class SqlExecuteException extends DBFoundRuntimeException{

    private final String provideName;

    private final String task ;

    private final String sql;

    public SqlExecuteException(String provideName, String task, String sql ,String message, Exception e) {
        super(message, e);
        this.provideName = provideName;
        this.task = task;
        this.sql = sql;
    }

    @Override
    public String getMessage() {
       return this.getTask() +"; " + super.getMessage() +"; [ " + this.getSql() +" ]";
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
