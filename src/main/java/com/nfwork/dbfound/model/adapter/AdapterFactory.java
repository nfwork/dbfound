package com.nfwork.dbfound.model.adapter;

public class AdapterFactory {

    private static AdapterFactory adapterFactory = new AdapterFactory();

    public QueryAdapter createQueryAdapter(Class cls) throws InstantiationException, IllegalAccessException {
        return (QueryAdapter) cls.newInstance();
    }

    public ExecuteAdapter createExecuteAdapter(Class cls) throws InstantiationException, IllegalAccessException {
        return (ExecuteAdapter) cls.newInstance();
    }

    public static QueryAdapter getQueryAdapter(Class cls) throws InstantiationException, IllegalAccessException {
        return adapterFactory.createQueryAdapter(cls);
    }

    public static ExecuteAdapter getExecuteAdapter(Class cls) throws InstantiationException, IllegalAccessException {
        return adapterFactory.createExecuteAdapter(cls);
    }

    protected void setAdapterFactory(AdapterFactory adapterFactory) {
        AdapterFactory.adapterFactory = adapterFactory;
    }
}
