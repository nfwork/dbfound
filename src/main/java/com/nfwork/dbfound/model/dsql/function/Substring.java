package com.nfwork.dbfound.model.dsql.function;

import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import java.util.List;

public class Substring extends DSqlFunction {

    @Override
    public boolean isSupported(SqlDialect sqlDialect) {
        return true;
    }

    @Override
    public Object apply(List<Object> list, SqlDialect sqlDialect) {
        if (list.size() < 2 || list.size() > 3) {
            throw new DSqlNotSupportException();
        }
        String str = (String) list.get(0);
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

        int start = pos > 0 ? pos - 1 : str.length() + pos;
        start = Math.max(0, Math.min(start, str.length()));

        int end =  Math.min(start + len, str.length());
        end = Math.max(end, start);

        return str.substring(start, end);
    }

}
