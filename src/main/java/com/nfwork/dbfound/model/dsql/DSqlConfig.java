package com.nfwork.dbfound.model.dsql;

public class DSqlConfig {

    private static boolean compareIgnoreCase = true;

    private static boolean openDSql = true;

    public static boolean isCompareIgnoreCase() {
        return compareIgnoreCase;
    }

    public static void setCompareIgnoreCase(boolean compareIgnoreCase) {
        DSqlConfig.compareIgnoreCase = compareIgnoreCase;
    }

    public static boolean isOpenDSql() {
        return openDSql;
    }

    public static void setOpenDSql(boolean openDSql) {
        DSqlConfig.openDSql = openDSql;
    }
}
