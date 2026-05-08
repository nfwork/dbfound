package com.nfwork.dbfound.util;

import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
            if (!ss[i].isEmpty()) {
                sb.append(upperFirstCase(ss[i]));
            }
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

    public static List<String> splitToList(String value){
        value = value.replaceAll("[\\s,;]+", ",");
        return Arrays.stream(value.split(",")).filter(v-> !v.isEmpty()).collect(Collectors.toList());
    }

    public static String sqlFullTrim(String value){
        if(value == null){
            return null;
        }
        int len = value.length();
        if(len == 0){
            return value;
        }

        boolean lastIsBlank = true;
        int dyh = 0;
        int syh = 0;
        int backslashCount = 0;

        boolean commentBasic = false;
        boolean commentMulti = false;

        StringBuilder buffer = new StringBuilder(len);

        for(int i=0; i< len; i++){
            char c = value.charAt(i);

            // 注释处理
            if(commentBasic){
                if(c == '\n' || c == '\r'){
                    commentBasic = false;
                    if(!lastIsBlank){
                        buffer.append(' ');
                        lastIsBlank = true;
                    }
                }
                continue;
            }
            if(commentMulti){
                if(c == '/' && value.charAt(i-1) == '*'){
                    commentMulti = false;
                    if(!lastIsBlank){
                        buffer.append(' ');
                        lastIsBlank = true;
                    }
                }
                continue;
            }

            boolean escaped = c != '\\' && (backslashCount % 2 != 0);
            if (c == '\\') {
                backslashCount++;
            } else {
                backslashCount = 0;
            }

            if (c == '\'' && !escaped && syh==0) {
                dyh = dyh ^ 1;
            }else if (c == '\"' && !escaped && dyh==0) {
                syh = syh ^ 1;
            }else if (dyh == 0 && syh ==0) {

                // 注释处理
                if (c == '-' && i < len - 2 && value.charAt(i + 1) == '-' && (value.charAt(i + 2) == ' ' || value.charAt(i+2) == '\t' || value.charAt(i+2) == '\n')) {
                    commentBasic = true;
                    continue;
                } else if (c == '/' && i < len -1 && value.charAt(i + 1) == '*') {
                    commentMulti = true;
                    continue;
                }

                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    if (!lastIsBlank) {
                        buffer.append(' ');
                        lastIsBlank = true;
                    }
                    continue;
                }
            }
            buffer.append(c);
            lastIsBlank = false;
        }
        if(lastIsBlank && buffer.length() > 0){
            buffer.setLength(buffer.length()-1);
        }
        return buffer.toString();
    }

    public static String getParamSql(String sql, List<Object> exeParam){
        int len = sql.length();
        int dyh = 0;
        int syh = 0;
        int backslashCount = 0;

        int paramIndex = 0;
        int paramSize = exeParam.size();
        int start = 0;
        StringBuilder buffer = new StringBuilder(len + 64);
        for(int i=0; i< len; i++){
            char c = sql.charAt(i);

            boolean escaped = c != '\\' && (backslashCount % 2 != 0);
            if (c == '\\') {
                backslashCount++;
            } else {
                backslashCount = 0;
            }

            if (c == '\'') {
                if(!escaped && syh==0) {
                    dyh = dyh ^ 1;
                }
            }else if (c == '\"') {
                if(!escaped && dyh==0) {
                    syh = syh ^ 1;
                }
            }else if(c == '?'){
                if (dyh == 0 && syh ==0) {
                    buffer.append(sql, start, i);
                    start = i + 1;

                    if(paramIndex >= paramSize){
                        buffer.append('?');
                        continue;
                    }
                    Object value = exeParam.get(paramIndex++);
                    if(value == null){
                        buffer.append("null");
                    }else if (value instanceof Number){
                        buffer.append(value);
                    }else if(value instanceof String){
                        buffer.append('\'');
                        appendEscapedSqlString(buffer, (String) value);
                        buffer.append('\'');
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
        if(start < len){
            buffer.append(sql, start, len);
        }
        return buffer.toString();
    }

    private static void appendEscapedSqlString(StringBuilder buffer, String value) {
        if (value.indexOf('\'') < 0 && value.indexOf('\\') < 0) {
            buffer.append(value);
            return;
        }
        for (int i = 0, len = value.length(); i < len; i++) {
            char c = value.charAt(i);
            if (c == '\'') {
                buffer.append('\\').append('\'');
            } else if (c == '\\') {
                buffer.append('\\').append('\\');
            } else {
                buffer.append(c);
            }
        }
    }

    public static boolean isBeginAnd(String value){
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
