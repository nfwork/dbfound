package com.nfwork.dbfound.model.enums;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumHandlerFactory {

    private static final Map<String,EnumTypeHandler<Enum<?>>> cache = new ConcurrentHashMap<>();

    private static final Map<String, Boolean> enumClassMap = new ConcurrentHashMap<>();

    public static <T extends Enum<?>> EnumTypeHandler<T> getEnumHandler(Class<?> clazz){
        try {
            EnumTypeHandler<Enum<?>> result = cache.get(clazz.getName());
            if (result == null) {
                synchronized (cache) {
                    result =  cache.get(clazz.getName());
                    if (result == null) {
                        result = new DefaultEnumTypeHandler<>();
                        result.initType((Class<Enum<?>>)clazz);
                        cache.put(clazz.getName(), result);
                    }
                }
            }
            return (EnumTypeHandler<T>) result;
        }catch (Exception e){
            throw new DBFoundRuntimeException("EnumTypeHandler create failed, " + e.getMessage(),e);
        }
    }

    public static boolean isEnum(Class<?> clazz){
         Boolean result = enumClassMap.get(clazz.getName());
         if(result == null){
             result = Enum.class.isAssignableFrom(clazz);
             enumClassMap.put(clazz.getName(),result);
         }
         return result;
    }
}
