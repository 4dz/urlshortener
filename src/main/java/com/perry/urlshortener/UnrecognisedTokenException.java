package com.perry.urlshortener;

public class UnrecognisedTokenException extends ShortenerServiceException {
    public UnrecognisedTokenException(String message) {
        super(message);
    }
}
