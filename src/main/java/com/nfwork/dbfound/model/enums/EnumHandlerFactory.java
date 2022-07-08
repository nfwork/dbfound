package com.nfwork.dbfound.model.enums;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import java.util.concurrent.ConcurrentHashMap;

public class EnumHandlerFactory {

    static final ConcurrentHashMap<Class,EnumTypeHandler> cache = new ConcurrentHashMap<>();

    public static EnumTypeHandler getEnumHandler(Class class1){
        try {
            EnumTypeHandler result = cache.get(class1);
            if (result == null) {
                synchronized (cache) {
                    result = cache.get(class1);
                    if (result == null) {
                        result = new DefaultEnumTypeHandler();
                        result.setType(class1);
                        cache.put(class1, result);
                    }
                }
            }
            return result;
        }catch (Exception e){
            throw new DBFoundRuntimeException("EnumTypeHandler create failed",e);
        }
    }
}
