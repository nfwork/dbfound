package com.nfwork.dbfound.model.enums;

import org.apache.commons.beanutils.BeanUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEnumTypeHandler<E extends Enum<E> >  implements EnumTypeHandler<Enum>{

    private Class<E> type;
    private Map<String,E>  cache = new ConcurrentHashMap<>();

    @Override
    public Enum locateEnum(String value) {
        return cache.get(value);
    }

    @Override
    public Object getEnumValue(Enum param) {
        if(param == null){
            return null;
        }
        if(param instanceof BaseEnum) {
            BaseEnum baseEnum = (BaseEnum) param;
            return baseEnum.getValue();
        }else{
            try {
                Object object = BeanUtils.getProperty(param,"value");
                return object;
            }catch (Exception exception){
            }
        }
        return param.toString();
    }

    public void initType(Class type) {
        this.type = type;
        E[] enums =  this.type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
        for (E e : enums) {
            Object value = getEnumValue(e);
            cache.put(value.toString(),e);
        }
    }
}
