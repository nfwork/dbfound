package com.nfwork.dbfound.model.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.base.Count;
import com.nfwork.dbfound.model.bean.Param;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public interface QueryAdapter<T> {

    default void beforeQuery(Context context, Map<String, Param> params){

    }

    default void beforeCount(Context context, Map<String, Param> params, Count count){

    }

    default void afterQuery(Context context, Map<String, Param> params, QueryResponseObject<T> responseObject){

    }

    default Class<?> getEntityClass() {
        Class<?> class1 = getClass();
        Type[] interfacesList = class1.getGenericInterfaces();
        for ( Type interfaces : interfacesList) {
            if (interfaces instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) interfaces;
                String rawTypeName = pType.getRawType().getTypeName();
                String className = QueryAdapter.class.getTypeName();
                if(className.equals(rawTypeName)) {
                    Type clazz = pType.getActualTypeArguments()[0];
                    if (clazz instanceof Class<?>) {
                        return (Class<?>) clazz;
                    }
                }
            }
        }
        return null;
    }
}
