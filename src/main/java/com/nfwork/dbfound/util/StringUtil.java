package com.nfwork.dbfound.util;

public class StringUtil {

    public static String underscoreToCamelCase(String underscore){
        if(DataUtil.isNull(underscore)){
            return underscore;
        }
        String[] ss = underscore.split("_");
        if(ss.length ==1){
            return underscore;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            sb.append(upperFirstCase(ss[i]));
        }
        return sb.toString();
    }

    public static String camelCaseToUnderscore(String name) {
        if(DataUtil.isNull(name)){
            return name;
        }
        StringBuilder result = new StringBuilder();
        result.append(name.charAt(0));
        for (int i = 1; i < name.length(); i++) {
            char s = name.charAt(i);
            if (s >= 'A' && s <= 'Z') {
                result.append("_");
                result.append( (char)(s + 32));
            }else {
                result.append(s);
            }
        }
        return result.toString();
    }

    private static String upperFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    public static String fullTrim(String value){
        if(value == null){
            return null;
        }
        value = value.trim();
        char [] chars = value.toCharArray();
        if(chars.length == 0){
            return value;
        }

        boolean lastIsBlank = false;
        int dyh = 0;
        int syh = 0;

        StringBuilder buffer = new StringBuilder();
        if(chars[0] == '\''){
            dyh++;
        }else if(chars[0] == '\"'){
            syh++;
        }
        buffer.append(chars[0]);

        for(int i=1; i< chars.length; i++){
            if (chars[i] == '\'' && chars[i-1] != '\\' && syh % 2==0) {
                dyh++;
            }else if (chars[i] == '\"' && chars[i-1] != '\\' && dyh % 2==0) {
                syh++;
            }else if (dyh % 2 == 0 && syh % 2 ==0) {
                if (chars[i] == ' ' || chars[i] == '\t' || chars[i] == '\n') {
                    if (!lastIsBlank) {
                        buffer.append(' ');
                        lastIsBlank = true;
                    }
                    continue;
                }
            }
            buffer.append(chars[i]);
            lastIsBlank = false;
        }
        return buffer.toString();
    }
}
