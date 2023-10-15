package com.nfwork.dbfound.model.adapter;

import java.util.Map;

public interface MapQueryAdapter extends QueryAdapter<Map<String,Object>>{

    @Override
    default Class<?> getEntityClass() {
        return Map.class;
    }
}
