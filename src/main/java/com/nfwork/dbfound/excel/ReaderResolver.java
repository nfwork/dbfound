package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.util.DataUtil;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ReaderResolver {

    public void register(String type){
        ExcelReader.readerResolverMap.put(type,this);
    }

    protected abstract List<List<Map<String,Object>>> read(InputStream input, List<ExcelColumn> columns);

    protected abstract Map<String, List<Map<String,Object>>> readForMap(InputStream input, List<ExcelColumn> columns);

    protected Object getMapperValue(Object values, Map<String,Object> mapper){
        if(values instanceof String && ((String) values).contains(",")){
            values = values.toString().split(",");
            StringBuilder valueBuilder = new StringBuilder();
            for (int i =0; i< DataUtil.getDataLength(values); i++) {
                Object value = DataUtil.getArrayDataByIndex(values, i);
                Object valItem = null;
                if (value != null) {
                    valItem = mapper.get(value.toString().trim());
                }
                valueBuilder.append(valItem != null ? valItem : value).append(",");
            }
            if(valueBuilder.length()>0) {
                valueBuilder.deleteCharAt(valueBuilder.length() - 1);
            }
            return valueBuilder.toString();
        }else{
            Object valItem = mapper.get(values.toString().trim());
            return valItem == null ? values: valItem;
        }
    }
}
