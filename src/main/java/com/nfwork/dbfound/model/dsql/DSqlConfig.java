package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;

public class DSqlConfig {

    private static boolean compareIgnoreCase = true;

    private static boolean openDSql = true;

    public static boolean isCompareIgnoreCase() {
        return compareIgnoreCase;
    }

    public static boolean isOpenDSql() {
        return openDSql;
    }

    public static void init(DBFoundInitToken token, Boolean compareIgnoreCase, Boolean openDSql) {
        DBFoundConfig.checkInitToken(token);
        if (compareIgnoreCase != null) {
            DSqlConfig.compareIgnoreCase = compareIgnoreCase;
        }
        if (openDSql != null) {
            DSqlConfig.openDSql = openDSql;
        }
    }
}
