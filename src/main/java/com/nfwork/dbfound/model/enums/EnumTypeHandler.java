package com.nfwork.dbfound.model.enums;

public interface EnumTypeHandler<E> {

     E locateEnum(String value) ;

     Object getEnumValue(E param);

     public void initType(Class<E> type) ;
}