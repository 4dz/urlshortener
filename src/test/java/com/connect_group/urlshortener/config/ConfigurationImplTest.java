package com.connect_group.urlshortener.config;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ConfigurationImplTest {

    @Test
    public void shouldGetValueFromPropertiesFile() {
        assertThat(ConfigurationImpl.INSTANCE.get(Configuration.Key.BASE_URL), equalTo("http://test.me/"));
    }
    

}