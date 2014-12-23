package com.connect_group.urlshortener;

import com.connect_group.urlshortener.baseconversion.BaseN;
import com.connect_group.urlshortener.util.BigOrderedRAMSet;
import com.connect_group.urlshortener.util.BigOrderedSet;

import java.net.URL;

public class ShortenerServiceImpl implements ShortenerService {
    private static final String SAFE_ORDERED_ALPHABET ="-23456789BCDFGHJKLMNPQRSTVWXYZ_bcdfghjklmnpqrstvwxyz";
    
    private final BaseN tokenConverter;
    private final BigOrderedSet<String> database;

    public ShortenerServiceImpl() {
        this(new BaseN(SAFE_ORDERED_ALPHABET), new BigOrderedRAMSet<String>());
    }

    public ShortenerServiceImpl(BaseN tokenConverter, BigOrderedSet<String> database) {
        this.tokenConverter = tokenConverter;
        this.database = database;
    }
    
    @Override
    public String shorten(URL url) {
        long id = database.add(url.toString());
        return tokenConverter.encode(id);
    }

    @Override
    public String expand(String token) throws UnrecognisedTokenException {
        try {
            long id = tokenConverter.decode(token);
            return database.get(id);
        } catch(IllegalArgumentException ex) {
            throw new UnrecognisedTokenException(ex.toString());
        }
    }
}
