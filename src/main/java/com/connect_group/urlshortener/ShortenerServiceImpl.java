package com.connect_group.urlshortener;

import com.connect_group.urlshortener.baseconversion.BaseN;
import com.connect_group.urlshortener.util.BigOrderedRAMSet;
import com.connect_group.urlshortener.util.BigOrderedSet;

import java.net.URL;

public class ShortenerServiceImpl implements ShortenerService {
    private static final String SAFE_ORDERED_ALPHABET ="-23456789BCDFGHJKLMNPQRSTVWXYZ_bcdfghjklmnpqrstvwxyz";
    
    private final BaseN tokenConverter;
    private final BigOrderedSet<String> database;
    private final String baseUrl;

    public ShortenerServiceImpl(String baseUrl) {
        this(new BaseN(SAFE_ORDERED_ALPHABET), new BigOrderedRAMSet<String>(), baseUrl);
    }

    public ShortenerServiceImpl(BaseN tokenConverter, BigOrderedSet<String> database, String baseUrl) {
        this.tokenConverter = tokenConverter;
        this.database = database;
        this.baseUrl = baseUrl;
    }
    
    @Override
    public String shorten(URL url) {
        long id = database.add(url.toString());
        String token = tokenConverter.encode(id);
        return baseUrl + token;
    }

    @Override
    public String expand(String token) throws UnrecognisedTokenException {
        return null;
    }
}
