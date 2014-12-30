package com.perry.urlshortener;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TokenTest {
    
    @Test
    public void shouldSpecifyRedirectInToString_WhenTokenDoesNotEndWithPlus() throws UnrecognisedTokenException {
        Token token = new Token("token");
        assertThat(token.toString(), equalTo("redirect token"));
    }

    @Test
    public void shouldSpecifyDisplayInToString_WhenTokenEndsWithPlus() throws UnrecognisedTokenException {
        Token token = new Token("token+");
        assertThat(token.toString(), equalTo("display token"));
    }


}