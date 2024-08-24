package com.nfwork.dbfound.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
}
