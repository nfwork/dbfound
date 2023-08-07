package com.nfwork.dbfound.model.base;

import com.nfwork.dbfound.model.enums.BaseEnum;

public enum DataType implements BaseEnum<DataType,String> {

    NUMBER("number"),
    VARCHAR("varchar"),
    BOOLEAN("boolean"),
    DATE("date"),
    FILE("file"),
    UNKNOWN("unknown"),
    COLLECTION("collection");

    final String value;

    DataType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
