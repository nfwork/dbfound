package com.nfwork.dbfound.model.dsql;

public class DSqlConfig {

    private static boolean equalsIgnoreCase = true;

    private static boolean userDSql = true;

    public static boolean isEqualsIgnoreCase() {
        return equalsIgnoreCase;
    }

    public static void setEqualsIgnoreCase(boolean equalsIgnoreCase) {
        DSqlConfig.equalsIgnoreCase = equalsIgnoreCase;
    }

    public static boolean isUserDSql() {
        return userDSql;
    }

    public static void setUserDSql(boolean userDSql) {
        DSqlConfig.userDSql = userDSql;
    }
}
