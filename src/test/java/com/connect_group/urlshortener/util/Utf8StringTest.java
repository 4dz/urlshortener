package com.connect_group.urlshortener.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public class Utf8StringTest {

    @Test
    public void shouldIdentifyEqualStrings() {
        Utf8String s1 = new Utf8String("ABC");
        Utf8String s2 = new Utf8String("ABC");
        
        assertThat(s1, equalTo(s1));
        assertThat(s1, equalTo(s2));
    }

    @Test
    public void shouldIdentifyUnequalStrings() {
        Utf8String s1 = new Utf8String("ABC");
        Utf8String s2 = new Utf8String("ABX");
        Utf8String s3 = new Utf8String("ABX4");
        
        assertThat(s1, not(equalTo(s2)));
        assertThat(s1, not(equalTo(s3)));
    }

    @Test
    public void shouldResultInOriginalString() {
        assertThat(new Utf8String("fréd").toString(), equalTo("fréd"));
    }
}