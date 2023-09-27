package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.util.DataUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WriterResolver {

    protected abstract void writer(File file, List<Object> dataList,List<Map<String,Object>> cls);

    public void register(String type, boolean asDefault){
        if(asDefault){
            ExcelWriter.defaultType = type;
        }
        ExcelWriter.writerResolverMap.put(type,this);
    }

    protected Map<String, Map<String,Object>> getMappers(List<Map<String,Object>> cls ){
        Map<String,Map<String,Object>> mappers = new HashMap<>();
        for (Map<String,Object> col : cls){
            Object object =col.get("mapper");
            if (DataUtil.isNotNull(object)){
                Map<String,Object> mapper = (Map)object;
                Map<String,Object> newMapper  = new HashMap<>();

                for (String key: mapper.keySet()){
                    newMapper.put(key,mapper.get(key));
                }
                mappers.put(col.get("name").toString(),newMapper);
            }
        }
        return mappers;
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
