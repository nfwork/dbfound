package com.nfwork.dbfound.model.base;

import com.nfwork.dbfound.model.enums.BaseEnum;

public enum SqlPartType implements BaseEnum<SqlPartType, String> {

    IF("if"),
    ELSEIF("elseif"),
    ELSE("else"),
    FOR("for");

    final String value;

    SqlPartType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
