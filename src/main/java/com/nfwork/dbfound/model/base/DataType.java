package com.nfwork.dbfound.model.base;

public enum DataType {

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
