package com.connect_group.urlshortener;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShortenerServiceImplTest {
    
    @Test
    public void shouldConvertUrlToToken() throws MalformedURLException {
        ShortenerServiceImpl service = new ShortenerServiceImpl("http://t.ag/");
        String token = service.shorten(new URL("http://test.com/url?param=value"));
        assertThat(token, equalTo("http://t.ag/-"));
    }



}