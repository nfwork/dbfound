package com.nfwork.dbfound.model.adapter;

public interface ObjectQueryAdapter extends QueryAdapter<Object>{

    @Override
    default Class<?> getEntityClass() {
        return Object.class;
    }
}
