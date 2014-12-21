package com.connect_group.urlshortener;

import com.connect_group.urlshortener.util.StringHelper;

public class Token {

    private final String token;
    private final boolean displayOnly;

    public Token(String token) throws UnrecognisedTokenException {
        boolean displayOnly = false;

        if(token!=null) {
            displayOnly = token.endsWith("+");

            if (displayOnly) {
                token = token.substring(0, token.length() - 1);
            }

            if (token.startsWith("/")) {
                token = token.substring(1);
            }
        }

        if(StringHelper.isEmpty(token)) {
            throw new UnrecognisedTokenException("No token was supplied");
        }

        this.displayOnly = displayOnly;
        this.token = token;
    }

    public boolean isDisplayOnly() {
        return displayOnly;
    }

    public String getTokenString() {
        return token;
    }

}
