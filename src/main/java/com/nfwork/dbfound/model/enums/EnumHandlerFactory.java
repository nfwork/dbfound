package com.nfwork.dbfound.model.enums;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import java.util.concurrent.ConcurrentHashMap;

public class EnumHandlerFactory {

    static final ConcurrentHashMap<Class<?>,EnumTypeHandler<Enum<?>>> cache = new ConcurrentHashMap<>();

    public static <T extends Enum<?>> EnumTypeHandler<T> getEnumHandler(Class<?> clazz){
        try {
            EnumTypeHandler<Enum<?>> result = cache.get(clazz);
            if (result == null) {
                synchronized (cache) {
                    result =  cache.get(clazz);
                    if (result == null) {
                        result = new DefaultEnumTypeHandler<>();
                        result.initType((Class<Enum<?>>)clazz);
                        cache.put(clazz, result);
                    }
                }
            }
            return (EnumTypeHandler<T>) result;
        }catch (Exception e){
            throw new DBFoundRuntimeException("EnumTypeHandler create failed, " + e.getMessage(),e);
        }
    }
}
