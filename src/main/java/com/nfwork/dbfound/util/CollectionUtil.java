package com.nfwork.dbfound.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionUtil {

    public static Set<String> asSet(String... items){
        return Arrays.stream(items).collect(Collectors.toSet());
    }
}
