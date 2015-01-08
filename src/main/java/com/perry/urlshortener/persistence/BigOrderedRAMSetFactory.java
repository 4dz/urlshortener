package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.util.StringHelper;
import com.perry.urlshortener.util.Utf8String;

import java.io.IOException;

public class BigOrderedRAMSetFactory implements BigOrderedSetFactory {
    
    @Override
    public BigOrderedSet<Utf8String> newSet(Configuration config) throws IOException {
        String filePath = config.get(Configuration.Key.DISK_BACKUP_FILEPATH);
        
        BigOrderedRAMSet<Utf8String> database;
        if (StringHelper.isNotEmpty(filePath)) {
            DiskBackupWriter diskWriter = new DiskBackupWriter(filePath);
            
            database = diskWriter.restore(BigOrderedRAMSet.DEFAULT_PAGE_SIZE);
        } else {
            database = new BigOrderedRAMSet<>(BigOrderedRAMSet.DEFAULT_PAGE_SIZE);
        }
        return database;
    }
}
