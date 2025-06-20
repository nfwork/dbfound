package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.DSqlFunction;
import com.nfwork.dbfound.util.DataUtil;

import java.util.List;

public class Substring extends DSqlFunction {

    @Override
    public Object apply(List<Object> list, SqlDialect sqlDialect) {
        if (list.size() < 2 || list.size() > 3) {
            throw new DSqlNotSupportException();
        }
        String str = DataUtil.stringValue(list.get(0));
        if (str == null) return null;
        int pos = ((Number)list.get(1)).intValue();
        int len;
        if(list.size() == 3) {
            len = ((Number)list.get(2)).intValue();
        }else{
            len = str.length();
        }
        return substring(str, pos, len);
    }

    private static String substring(String str, int pos, int len) {
        if (Math.abs(pos) > str.length()) {
            return "";
        }
        int start = pos > 0 ? pos - 1 : str.length() + pos;

        int end =  Math.min(start + len, str.length());
        end = Math.max(end, start);

        return str.substring(start, end);
    }

}
