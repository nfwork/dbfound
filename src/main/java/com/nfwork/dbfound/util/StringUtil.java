package com.nfwork.dbfound.util;

public class StringUtil {

    public static String underscoreToCamelCase(String underscore){
        if(underscore == null){
            return null;
        }
        String[] ss = underscore.split("_");
        if(ss.length ==1){
            return underscore;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            sb.append(upperFirstCase(ss[i]));
        }
        return sb.toString();
    }
    private static String upperFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }
}
