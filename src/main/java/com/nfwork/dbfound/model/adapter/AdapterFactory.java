package com.nfwork.dbfound.model.adapter;

public class AdapterFactory {

    private static AdapterFactory adapterFactory = new AdapterFactory();

    public QueryAdapter<?> createQueryAdapter(Class<?> cls) throws Exception {
        return (QueryAdapter<?>) cls.getConstructor().newInstance();
    }

    public ExecuteAdapter createExecuteAdapter(Class<?> cls) throws Exception {
        return (ExecuteAdapter) cls.getConstructor().newInstance();
    }

    public static QueryAdapter<?> getQueryAdapter(Class<?> cls) throws Exception {
        return adapterFactory.createQueryAdapter(cls);
    }

    public static ExecuteAdapter getExecuteAdapter(Class<?> cls) throws Exception {
        return adapterFactory.createExecuteAdapter(cls);
    }

    protected void setAdapterFactory(AdapterFactory adapterFactory) {
        AdapterFactory.adapterFactory = adapterFactory;
    }
}
