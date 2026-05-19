package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;

public class DSqlConfig {

    private static ConfigState config = new ConfigState();

    public static boolean isCompareIgnoreCase() {
        return config.compareIgnoreCase;
    }

    public static boolean isOpenDSql() {
        return config.openDSql;
    }

    public static void init(DBFoundInitToken token, Boolean compareIgnoreCase, Boolean openDSql) {
        DBFoundConfig.checkInitToken(token);
        if (compareIgnoreCase != null) {
            config.compareIgnoreCase = compareIgnoreCase;
        }
        if (openDSql != null) {
            config.openDSql = openDSql;
        }
    }

    public static void reset(DBFoundInitToken token) {
        DBFoundConfig.checkInitToken(token);
        config = new ConfigState();
    }

    private static class ConfigState {
        private boolean compareIgnoreCase = true;
        private boolean openDSql = true;
    }
}
