package com.perry.urlshortener;

import com.perry.urlshortener.service.ShortenerServiceException;

public class UnrecognisedTokenException extends ShortenerServiceException {
    public UnrecognisedTokenException(String message) {
        super(message);
    }
}
