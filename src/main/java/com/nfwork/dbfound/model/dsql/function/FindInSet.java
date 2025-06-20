package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlConfig;
import com.nfwork.dbfound.model.dsql.DSqlFunction;

import java.util.List;

public class FindInSet extends DSqlFunction {

    @Override
    public Object apply(List<Object> list, SqlDialect sqlDialect) {
        // FIND_IN_SET 需要2个参数：要查找的字符串和逗号分隔的字符串集合
        if (list.size() != 2) {
            throw new DSqlNotSupportException();
        }
        
        Object searchObj = list.get(0);
        Object setObj = list.get(1);
        
        // 如果任一参数为NULL，返回NULL（与MySQL行为一致）
        if (searchObj == null || setObj == null) {
            return null;
        }
        
        String searchStr = searchObj.toString();
        String setStr = setObj.toString();
        
        // 处理空字符串情况
        if (searchStr.isEmpty() || setStr.isEmpty()) {
            return 0;
        }
        
        // 分割字符串集合
        String[] elements = setStr.split(",");
        
        // 查找匹配项的位置（1-based）
        for (int i = 0; i < elements.length; i++) {
            if(DSqlConfig.isCompareIgnoreCase()) {
                if (elements[i].equalsIgnoreCase(searchStr)) {
                    return i + 1; // 返回1-based位置
                }
            }else{
                if (elements[i].equals(searchStr)) {
                    return i + 1; // 返回1-based位置
                }
            }
        }
        // 未找到返回0
        return 0;
    }
}