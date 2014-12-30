package com.perry.urlshortener.stub;

import com.perry.urlshortener.ShortenerService;
import com.perry.urlshortener.UnrecognisedTokenException;

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
        if("valid".equals(token)) {
            return "expanded_url";
        } else if("return-null".equals(token)) {
            return null;
        } else {
            throw new UnrecognisedTokenException("shortened token was not recognised");
        }
    }
}
