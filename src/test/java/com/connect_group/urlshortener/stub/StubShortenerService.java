package com.connect_group.urlshortener.stub;

import com.connect_group.urlshortener.ShortenerService;
import com.connect_group.urlshortener.UnrecognisedTokenException;

import java.net.URL;

public class StubShortenerService implements ShortenerService {
    private int counter = 0;

    @Override
    public String shorten(URL url) {
        counter++;
        return "" + counter;
    }

    @Override
    public String expand(String token) throws UnrecognisedTokenException {
        if("/valid".equals(token)) {
            return "expanded_url";
        } else {
            throw new UnrecognisedTokenException("shortened token was not recognised");
        }
    }
}
