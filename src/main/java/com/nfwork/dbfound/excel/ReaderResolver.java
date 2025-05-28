package com.nfwork.dbfound.excel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class ReaderResolver {

    public void register(String type){
        ExcelReader.readerResolverMap.put(type,this);
    }

    protected abstract List<List<Map<String,Object>>> read(InputStream input, List<ExcelColumn> columns);

    protected abstract Map<String, List<Map<String,Object>>> readForMap(InputStream input, List<ExcelColumn> columns);
}
