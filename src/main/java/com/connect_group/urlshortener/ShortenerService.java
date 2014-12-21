package com.connect_group.urlshortener;

import java.net.URL;

public interface ShortenerService {
    public String shorten(URL url);

    public String expand(String token) throws UnrecognisedTokenException;
}
