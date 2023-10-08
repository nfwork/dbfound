package com.nfwork.dbfound.excel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class ReaderResolver {

    public void register(String type){
        ExcelReader.readerResolverMap.put(type,this);
    }

    protected static boolean isLong(double value) {
        double decimalPart = value % 1;
        return decimalPart == 0;
    }

    protected abstract List<List<Map<String,Object>>> read(InputStream input);

    protected abstract Map<String, List<Map<String,Object>>> readForMap(InputStream input);
}
