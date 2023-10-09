package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.util.DataUtil;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class WriterResolver {

    protected abstract void writer(File file, List<Object> dataList,List<ExcelColumn> columns);

    public void register(String type, boolean asDefault){
        if(asDefault){
            ExcelWriter.defaultType = type;
        }
        ExcelWriter.writerResolverMap.put(type,this);
    }

    protected Object getMapperValue(Object values, Map<String,Object> mapper){
        if(values instanceof Collection){
            values = ((Collection<?>)values).toArray();
        }else if(values instanceof String && ((String) values).contains(",")){
            values = values.toString().split(",");
        }
        if(!DataUtil.isArray(values)){
            Object valItem = mapper.get(values.toString().trim());
            return valItem == null ? values: valItem;
        }
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
    }

}
