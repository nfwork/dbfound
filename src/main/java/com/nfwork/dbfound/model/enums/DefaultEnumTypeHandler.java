package com.nfwork.dbfound.model.enums;

import com.nfwork.dbfound.model.reflector.Reflector;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEnumTypeHandler<E extends Enum<E> >  implements EnumTypeHandler<E>{

    private final Map<String,E>  cache = new ConcurrentHashMap<>();

    @Override
    public E locateEnum(String value) {
        return cache.get(value);
    }

    @Override
    public Object getEnumValue(E param) {
        if(param == null){
            return null;
        }
        if(param instanceof BaseEnum) {
            return ((BaseEnum)param).getValue();
        }else{
            try {
                Reflector reflector = Reflector.forClass(param.getClass());
                if(reflector.hasGetter("value")) {
                    return reflector.getGetInvoker("value").invoke(param, null);
                }else{
                    return param.toString();
                }
            }catch (Exception exception){
                return param.toString();
            }
        }
    }

    public void initType(Class<E> type) {
        E[] enums =  type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
        for (E e : enums) {
            Object value = getEnumValue(e);
            cache.put(value.toString(),e);
        }
    }
}
