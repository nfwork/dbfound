package com.nfwork.dbfound.model.base;

public enum SqlPartType {

    IF("if"),
    FOR("for");

    final String value;

    SqlPartType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
