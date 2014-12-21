package com.connect_group.urlshortener;

import com.connect_group.urlshortener.util.StringHelper;

public class Token {

    private final String token;
    private final boolean displayOnly;

    public Token(String token) throws UnrecognisedTokenException {

        if(StringHelper.isEmpty(token)) {
            throw new UnrecognisedTokenException("No shorten parameter was supplied");
        }

        displayOnly = token.endsWith("+");

        if(displayOnly) {
            token = token.substring(0, token.length());
        }

        this.token = token;
    }

    public boolean isDisplayOnly() {
        return displayOnly;
    }

    public String getTokenString() {
        return token;
    }

}
