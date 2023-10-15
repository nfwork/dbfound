package com.nfwork.dbfound.model.enums;

public interface EnumTypeHandler<E> {

     E locateEnum(String value) ;

     Object getEnumValue(E param);

     void initType(Class<E> type) ;
}