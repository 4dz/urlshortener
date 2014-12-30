package com.perry.urlshortener;

public class UnrecognisedTokenException extends Exception {
    public UnrecognisedTokenException(String message) {
        super(message);
    }
}
