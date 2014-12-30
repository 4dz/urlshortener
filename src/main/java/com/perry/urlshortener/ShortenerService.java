package com.perry.urlshortener;

import java.net.URL;

public interface ShortenerService {
    public String shorten(URL url) throws ShortenerServiceException;

    public String expand(String token) throws ShortenerServiceException;
}
