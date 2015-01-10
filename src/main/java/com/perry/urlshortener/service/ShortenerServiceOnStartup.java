package com.perry.urlshortener.service;

import com.perry.urlshortener.baseconversion.BaseN;
import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.lifecycle.OnStartup;
import com.perry.urlshortener.persistence.BigOrderedSet;
import com.perry.urlshortener.util.Utf8String;

public class ShortenerServiceOnStartup implements OnStartup {

    @Override
    public void onStart(MutableScope scope) {
        String errorMessage = scope.getErrorMessage();
        if(!scope.isError()) {
            try {
                BigOrderedSet<Utf8String> database = scope.getDatabase();
                scope.setShortenerService(new ShortenerServiceImpl(new BaseN(ShortenerServiceImpl.SAFE_ORDERED_ALPHABET), database));
                return;
            } catch (Exception ex) {
                errorMessage = ex.getMessage();
            }
        }

        scope.setShortenerService(new ShortenerServiceUnavailable(errorMessage));
    }
}
