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

        boolean noteBasic = false;
        boolean noteMulti = false;
        int noteMaxIndex = chars.length - 1;

        StringBuilder buffer = new StringBuilder();
        if(chars[0] == '\''){
            dyh = dyh ^ 1;
        }else if(chars[0] == '\"'){
            syh = syh ^ 1;
        }
        buffer.append(chars[0]);

        for(int i=1; i< chars.length; i++){

            // 注释处理
            if(noteBasic){
                if(chars[i] == '\n'){
                    noteBasic = false;
                }else{
                    continue;
                }
            }
            if(noteMulti){
                if(chars[i] == '/' && chars[i-1] == '*'){
                    noteMulti = false;
                    if(!lastIsBlank){
                        buffer.append(" ");
                        lastIsBlank = true;
                    }
                }
                continue;
            }

            if (chars[i] == '\'' && chars[i-1] != '\\' && syh==0) {
                dyh = dyh ^ 1;
            }else if (chars[i] == '\"' && chars[i-1] != '\\' && dyh==0) {
                syh = syh ^ 1;
            }else if (dyh == 0 && syh ==0) {

                // 注释处理
                if (chars[i] == '-' && i < noteMaxIndex && chars[i + 1] == '-') {
                    noteBasic = true;
                    continue;
                } else if (chars[i] == '/' && i < noteMaxIndex && chars[i + 1] == '*') {
                    noteMulti = true;
                    continue;
                }

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
