package com.connect_group.urlshortener;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShortenerServiceImplTest {
    
    @Test
    public void shouldConvertUrlToToken() throws MalformedURLException {
        ShortenerServiceImpl service = new ShortenerServiceImpl();
        String shortUrl = service.shorten(new URL("http://test.com/url?param=value"));
        assertThat(shortUrl, equalTo("-"));
    }
    
    @Test
    public void shouldConvertTokenToUrl() throws UnrecognisedTokenException, MalformedURLException {
        String url = "http://test.com/url?param=value";
        ShortenerServiceImpl service = new ShortenerServiceImpl();
        String token = service.shorten(new URL(url));
        assertThat(service.expand(token), equalTo(url));
    }



}