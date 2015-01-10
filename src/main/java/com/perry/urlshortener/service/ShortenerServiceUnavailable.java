package com.perry.urlshortener.service;

import java.net.URL;

public class ShortenerServiceUnavailable implements ShortenerService {
    private final ShortenerServiceException ex;
    
    public ShortenerServiceUnavailable(String message) {
        ex = new ShortenerServiceException(message);
    }
    
    @Override
    public String shorten(URL url) throws ShortenerServiceException {
        throw ex;
    }

    @Override
    public String expand(String token) throws ShortenerServiceException {
        throw ex;
    }
}
