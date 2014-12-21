package com.connect_group.urlshortener.util;

public class StringHelper {
    public static boolean isEmpty(String str) {
        return (str==null || str.length()==0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
