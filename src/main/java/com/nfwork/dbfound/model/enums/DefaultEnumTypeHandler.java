package com.nfwork.dbfound.model.enums;

import org.apache.commons.beanutils.BeanUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEnumTypeHandler<E extends Enum<E> >  implements EnumTypeHandler<Enum>{

    private Class<E> type;
    private Map<String,E>  cache = new ConcurrentHashMap<>();

    @Override
    public Enum locateEnum(String value) {
        Enum result = cache.get(value);
        if(result!=null){
            return result;
        }
        E[] enums =  type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
        for (E e : enums) {
            Object evalue = getEnumValue(e);
            if (evalue!= null && evalue.toString().equals(value)) {
                cache.put(value,e);
                return e;
            }
        }
        return null;
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

    public void setType(Class type) {
        this.type = type;
    }
}
