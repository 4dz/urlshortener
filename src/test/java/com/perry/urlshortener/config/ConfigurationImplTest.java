package com.perry.urlshortener.config;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ConfigurationImplTest {

    @Test
    public void shouldGetValueFromSystemProperties_WhenSystemPropertySet() {
        synchronized (this.getClass()) {
            String key = Configuration.Key.BASE_URL.toString();
            String originalPropertyValue = System.getProperty(key);
            String expectedSystemValue = "ExpectedValue";
            System.setProperty(key, expectedSystemValue);

            assertThat(ConfigurationImpl.getInstance().get(Configuration.Key.BASE_URL), equalTo(expectedSystemValue));

            if (originalPropertyValue == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, originalPropertyValue);
            }
        }
    }
    
    @Test
    public void shouldGetValueFromPropertiesFile() {
        assertThat(ConfigurationImpl.getInstance().get(Configuration.Key.BASE_URL), equalTo("http://test.me/"));
    }
    
    @Test(expected = ExceptionInInitializerError.class)
    public void shouldFail_WhenPropertiesFileNotFound() {
        ConfigurationImpl.load("/notfound.properties");
    }
}