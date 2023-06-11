package com.nfwork.dbfound.el;

import com.nfwork.dbfound.cache.JvmCache;
import com.nfwork.dbfound.util.StringUtil;

public class PropertyTransfer {

    private static final JvmCache<String, String> camelCaseCache = new JvmCache<>(StringUtil::underscoreToCamelCase);

    private static final JvmCache<String, String> underscoreCache = new JvmCache<>(StringUtil::camelCaseToUnderscore);

    protected static String underscoreToCamelCase(String underscore){
       return camelCaseCache.get(underscore);
    }
     
    protected static String camelCaseToUnderscore(String name) {
       return underscoreCache.get(name);
    }
}