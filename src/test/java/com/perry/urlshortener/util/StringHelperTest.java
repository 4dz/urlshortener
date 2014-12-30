package com.perry.urlshortener.util;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class StringHelperTest {

    @Test
    public void shouldConstructHelper() {
        /*
         * OCD Test coverage. By doing this you test the (default) constructor.
         */
        new StringHelper() {};
    }

    @Test
    public void shouldIdentifyEmptyString_WhenStringIsNull() {
        assertThat(StringHelper.isEmpty(null), equalTo(true));
    }

    @Test
    public void shouldIdentifyEmptyString_WhenStringLengthIsZero() {
        assertThat(StringHelper.isEmpty(""), equalTo(true));
    }

    @Test
    public void shouldIdentifyNonEmptyString_WhenStringLengthIsNotZero() {
        assertThat(StringHelper.isNotEmpty(" "), equalTo(true));
    }

}