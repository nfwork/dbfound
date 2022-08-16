package com.nfwork.dbfound.model.dsql;

public class DSqlConfig {

    private static boolean compareIgnoreCase = true;

    private static boolean useDSql = true;

    public static boolean isCompareIgnoreCase() {
        return compareIgnoreCase;
    }

    public static void setCompareIgnoreCase(boolean compareIgnoreCase) {
        DSqlConfig.compareIgnoreCase = compareIgnoreCase;
    }

    public static boolean isUseDSql() {
        return useDSql;
    }

    public static void setUseDSql(boolean userDSql) {
        DSqlConfig.useDSql = userDSql;
    }
}
