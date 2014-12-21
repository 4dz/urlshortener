package com.connect_group.urlshortener;

import java.net.URL;

public class ShortenerServiceImpl implements ShortenerService {

    @Override
    public String shorten(URL url) {
        return url.toString();
    }

    @Override
    public String expand(String token) throws UnrecognisedTokenException {
        return null;
    }
}
