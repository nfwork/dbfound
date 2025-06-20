package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;
import com.nfwork.dbfound.util.DataUtil;

import java.util.List;

public class Locate extends DSqlFunction {

    @Override
    public Object apply(List<Object> args, SqlDialect sqlDialect) {
        // 参数校验：至少需要 substr 和 str，最多加上 pos
        if (args.size() < 2 || args.size() > 3) {
            throw new DSqlNotSupportException();
        }

        // 解析参数
        String substr = DataUtil.stringValue(args.get(0));
        String str = DataUtil.stringValue(args.get(1));
        int pos = args.size() == 3 ? ((Number)args.get(2)).intValue() : 1; // 默认从第1个字符开始

        // 处理null值（如果任一参数为null，返回null）
        if (substr == null || str == null) {
            return null;
        }

        // 调用核心逻辑
        return locate(substr, str, pos);
    }

    /**
     * 模拟 MySQL LOCATE(substr, str, [pos]) 函数
     * @param substr 要查找的子字符串
     * @param str    主字符串
     * @param pos    起始位置（1-based，小于1时视为1）
     * @return 子字符串的位置（1-based），找不到返回0
     */
    private int locate(String substr, String str, int pos) {
        if (pos < 1) {
            return 0;
        }
        // 边界条件处理
        if (substr.isEmpty()) {
            return pos <= str.length() ? pos : 0; // MySQL特性：空子串在有效pos时返回pos
        }
        if (str.isEmpty() || pos > str.length()) {
            return 0;
        }

        // 调整pos为Java的0-based索引（且不小于0）
        int startIndex = Math.max(0, pos - 1);
        int index = str.indexOf(substr, startIndex);

        // 返回1-based位置（找不到时返回0）
        return index >= 0 ? index + 1 : 0;
    }
}