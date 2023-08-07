package com.nfwork.dbfound.model.base;

import com.nfwork.dbfound.model.enums.BaseEnum;

public enum CountType implements BaseEnum<CountType,String> {

    NOT_REQUIRED("not_required"), REQUIRED("required");

    CountType(String value){
        this.value = value;
    }

    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
