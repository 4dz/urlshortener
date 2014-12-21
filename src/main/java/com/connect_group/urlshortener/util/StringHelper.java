package com.connect_group.urlshortener.util;

/**
 * Simple string util helper class.
 * This was added primarily to keep dependancies down (as Apache have a great string utils library!)
 */
public class StringHelper {
    public static boolean isEmpty(String str) {
        return (str==null || str.length()==0);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
