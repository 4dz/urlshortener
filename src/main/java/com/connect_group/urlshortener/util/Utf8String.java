package com.connect_group.urlshortener.util;


import java.nio.charset.Charset;

public class Utf8String {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final byte[] value;
    private final int hashCode;
    
    public Utf8String(String str) {
        this.value = str.getBytes(UTF8);
        this.hashCode = str.hashCode();
    }
    
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Utf8String) {
            Utf8String anotherString = (Utf8String) anObject;
            int n = length();
            if (n == anotherString.length()) {
                byte v1[] = value;
                byte v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return new String(value, UTF8);
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    public final int length() {
        return value.length;
    }
    
}
