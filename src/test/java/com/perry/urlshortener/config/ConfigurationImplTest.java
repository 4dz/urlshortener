package com.perry.urlshortener.config;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ConfigurationImplTest {

    @Test
    public void shouldGetValueFromPropertiesFile() {
        assertThat(ConfigurationImpl.getInstance().get(Configuration.Key.BASE_URL), equalTo("http://test.me/"));
    }
    
    @Test(expected = ExceptionInInitializerError.class)
    public void shouldFail_WhenPropertiesFileNotFound() {
        ConfigurationImpl.load("/notfound.properties");
    }
}