package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.util.StringHelper;
import com.perry.urlshortener.util.Utf8String;

import java.io.IOException;

public class BigOrderedMapDBSetFactory implements BigOrderedSetFactory {
    @Override
    public BigOrderedSet<Utf8String> newSet(Configuration config) throws IOException {
        String filePath = config.get(Configuration.Key.DISK_BACKUP_FILEPATH);

        BigOrderedMapDBSet<Utf8String> database;
        if (StringHelper.isNotEmpty(filePath)) {
            database = new BigOrderedMapDBSet<>(filePath);
        } else {
            throw new RuntimeException("MapDB implementation requires a DISK_BACKUP_FILEPATH property");
        }
        return database;
    }
}
