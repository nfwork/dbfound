package com.nfwork.dbfound.util;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionUtil {

    public static Set<String> asSet(String... items){
        return Arrays.stream(items).collect(Collectors.toSet());
    }

    public static Set<Integer> asSet(Integer... items){
        return Arrays.stream(items).collect(Collectors.toSet());
    }

    public static List<String> asList(String... items){
        return Arrays.stream(items).collect(Collectors.toList());
    }

    public static List<Integer> asList(Integer... items){
        return Arrays.stream(items).collect(Collectors.toList());
    }

    public static List<Object> asList(Object... items){
        return Arrays.stream(items).collect(Collectors.toList());
    }

    public static Map<String,Object> asMap(String key1, Object value1){
        Map<String,Object> map = new HashMap<>();
        map.put(key1, value1);
        return map;
    }
    public static Map<String,Object> asMap(String key1, Object value1, String key2, Object value2){
        Map<String, Object> map = asMap(key1, value1);
        map.put(key2, value2);
        return map;
    }
    public static Map<String,Object> asMap(String key1, Object value1, String key2, Object value2, String key3, Object value3){
        Map<String, Object> map = asMap(key1, value1, key2, value2);
        map.put(key3, value3);
        return map;
    }
}
