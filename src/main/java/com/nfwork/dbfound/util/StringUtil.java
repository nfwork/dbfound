package com.nfwork.dbfound.util;

import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;

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

    public static String sqlFullTrim(String value){
        if(value == null){
            return null;
        }
        char [] chars = value.toCharArray();
        if(chars.length == 0){
            return value;
        }

        boolean lastIsBlank = true;
        int dyh = 0;
        int syh = 0;

        boolean commentBasic = false;
        boolean commentMulti = false;

        StringBuilder buffer = new StringBuilder();

        for(int i=0; i< chars.length; i++){

            // 注释处理
            if(commentBasic){
                if(chars[i] == '\n'){
                    commentBasic = false;
                }else{
                    continue;
                }
            }
            if(commentMulti){
                if(chars[i] == '/' && chars[i-1] == '*'){
                    commentMulti = false;
                    if(!lastIsBlank){
                        buffer.append(" ");
                        lastIsBlank = true;
                    }
                }
                continue;
            }

            if (chars[i] == '\'' && (i==0 || chars[i-1] != '\\') && syh==0) {
                dyh = dyh ^ 1;
            }else if (chars[i] == '\"' && (i==0 || chars[i-1] != '\\') && dyh==0) {
                syh = syh ^ 1;
            }else if (dyh == 0 && syh ==0) {

                // 注释处理
                if (chars[i] == '-' && i < chars.length - 2 && chars[i + 1] == '-' && (chars[i + 2] == ' ' || chars[i+2] == '\t' || chars[i+2] == '\n')) {
                    commentBasic = true;
                    continue;
                } else if (chars[i] == '/' && i < chars.length -1 && chars[i + 1] == '*') {
                    commentMulti = true;
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
        if(lastIsBlank && buffer.length() > 0){
            buffer.deleteCharAt(buffer.length()-1);
        }
        return buffer.toString();
    }

    public static String getParamSql(String sql, List<Object> exeParam){
        char [] chars = sql.toCharArray();
        int dyh = 0;
        int syh = 0;

        int paramIndex = 0;
        int start = 0;
        StringBuilder buffer = new StringBuilder();
        for(int i=0; i< chars.length; i++){
            if (chars[i] == '\'') {
                if((i==0 || chars[i-1] != '\\') && syh==0) {
                    dyh = dyh ^ 1;
                }
            }else if (chars[i] == '\"') {
                if((i==0 || chars[i-1] != '\\') && dyh==0) {
                    syh = syh ^ 1;
                }
            }else if(chars[i] == '?'){
                if (dyh == 0 && syh ==0) {
                    buffer.append(chars, start, i - start);
                    start = i + 1;

                    Object value = exeParam.get(paramIndex++);
                    if(value == null){
                        buffer.append("null");
                    }else if (value instanceof Number){
                        buffer.append(value);
                    }else if(value instanceof String){
                        String sValue = (String) value;
                        if(sValue.contains("'")){
                            sValue = sValue.replace("'","\\'");
                        }
                        buffer.append("'").append(sValue).append("'");
                    } else if (value instanceof Date) {
                        buffer.append("'").append(LocalDateUtil.formatDate((Date) value)).append("'");
                    } else if (value instanceof Temporal) {
                        buffer.append("'").append(LocalDateUtil.formatTemporal((Temporal) value)).append("'");
                    } else if(value instanceof Boolean){
                        buffer.append(value);
                    } else{
                        buffer.append("?");
                    }
                }
            }
        }
        if(start < chars.length){
            buffer.append(chars,start, chars.length-start);
        }
        return buffer.toString();
    }

    public static String addWhere(String sql){
        if(isBeginAnd(sql)){
            return "where" + sql.substring(3);
        }
        return "where " + sql;
    }

    public static String addAnd(String sql){
        if(isBeginAnd(sql)){
            return sql;
        }
        return "and " + sql;
    }

    private static boolean isBeginAnd(String value){
        if(value.length() < 3){
            return false;
        }
        char a0 = value.charAt(0);
        if(a0 !='a' && a0 != 'A'){
            return false;
        }
        char a1 = value.charAt(1);
        if(a1 !='n' && a1 != 'N'){
            return false;
        }
        char a2 = value.charAt(2);
        if(a2 !='d' && a2 != 'D'){
            return false;
        }
        if(value.length()==3){
            return true;
        }
        char a3 = value.charAt(3);
        return a3 == ' ' || a3 == '(';
    }
}
