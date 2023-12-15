package com.nfwork.dbfound.model.base;

import com.nfwork.dbfound.model.enums.BaseEnum;

public enum FileSaveType implements BaseEnum<FileSaveType,String> {

    DB("db"),
    DISK("disk");

    final String value;

    FileSaveType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
