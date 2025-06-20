package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;
import com.nfwork.dbfound.util.DataUtil;

import java.util.List;

public class Instr extends DSqlFunction {

    @Override
    public Object apply(List<Object> list, SqlDialect sqlDialect) {
        // INSTR函数需要2个参数：主字符串和要查找的子字符串
        if (list.size() != 2) {
            throw new DSqlNotSupportException();
        }
        
        String str = DataUtil.stringValue(list.get(0));
        String substr = DataUtil.stringValue(list.get(1));
        
        // 如果任一参数为null，返回null
        if (str == null || substr == null) {
            return null;
        }
        
        // 调用实际的INSTR实现
        return instr(str, substr);
    }

    /**
     * 实现MySQL INSTR函数的功能
     * @param str 主字符串
     * @param substr 要查找的子字符串
     * @return 子字符串在主字符串中第一次出现的位置(1-based)，如果没找到返回0
     */
    private static int instr(String str, String substr) {
        if (str.isEmpty()) {
            return 0;
        }
        if(substr.isEmpty()){
            return 1;
        }
        int index = str.indexOf(substr);
        return index == -1 ? 0 : index + 1; // 转换为1-based索引
    }
}