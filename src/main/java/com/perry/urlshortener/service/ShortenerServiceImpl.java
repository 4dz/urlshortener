package com.perry.urlshortener.service;

import com.perry.urlshortener.UnrecognisedTokenException;
import com.perry.urlshortener.baseconversion.BaseN;
import com.perry.urlshortener.persistence.BigOrderedRAMSet;
import com.perry.urlshortener.persistence.BigOrderedSet;
import com.perry.urlshortener.util.Utf8String;

import java.net.URL;

public class ShortenerServiceImpl implements ShortenerService {
    public static final String SAFE_ORDERED_ALPHABET ="-23456789BCDFGHJKLMNPQRSTVWXYZ_bcdfghjklmnpqrstvwxyz";
    
    private final BaseN tokenConverter;
    private final BigOrderedSet<Utf8String> database;

    public ShortenerServiceImpl() {
        this(new BaseN(SAFE_ORDERED_ALPHABET), new BigOrderedRAMSet<Utf8String>());
    }

    public ShortenerServiceImpl(BaseN tokenConverter, BigOrderedSet<Utf8String> database) {
        this.tokenConverter = tokenConverter;
        this.database = database;
    }
    
    @Override
    public String shorten(URL url) {
        long id = database.add(new Utf8String(url.toString()));
        return tokenConverter.encode(id);
    }

    @Override
    public String expand(String token) throws UnrecognisedTokenException {
        try {
            long id = tokenConverter.decode(token);
            return database.get(id).toString();
        } catch(IllegalArgumentException ex) {
            throw new UnrecognisedTokenException(ex.toString());
        }
    }
}
