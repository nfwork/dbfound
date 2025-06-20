package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;
import com.nfwork.dbfound.util.DataUtil;

import java.util.List;

public class SubstringIndex extends DSqlFunction {

    @Override
    public Object apply(List<Object> list, SqlDialect sqlDialect) {
        if (list.size() != 3) {
            throw new DSqlNotSupportException();
        }
        String str = DataUtil.stringValue(list.get(0));
        String delim = DataUtil.stringValue(list.get(1));
        int count = ((Number)list.get(2)).intValue();

        if (str == null || delim == null ) {
            return null;
        }

        if (count == 0 || delim.isEmpty()) {
            return "";
        }

        if (count > 0) {
            // 正数情况：从左开始查找
            int index = -1;
            for (int i = 0; i < count; i++) {
                index = str.indexOf(delim, index + 1);
                if (index == -1) {
                    return str; // 没找到足够的分隔符，返回整个字符串
                }
            }
            return str.substring(0, index);
        } else {
            // 负数情况：从右开始查找
            count = -count;
            int index = str.length();
            for (int i = 0; i < count; i++) {
                index = str.lastIndexOf(delim, index - 1);
                if (index == -1) {
                    return str; // 没找到足够的分隔符，返回整个字符串
                }
            }
            return str.substring(index + delim.length());
        }
    }
}
