package com.nfwork.dbfound.model.base;

import com.nfwork.dbfound.model.enums.BaseEnum;

public enum IOType implements BaseEnum<IOType,String> {

    IN("in"),
    OUT("out"),
    BOTH("both");

    final String value;

    IOType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
