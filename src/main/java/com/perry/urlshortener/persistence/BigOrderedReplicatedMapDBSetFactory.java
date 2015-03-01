package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.util.StringHelper;
import com.perry.urlshortener.util.Utf8String;

public class BigOrderedReplicatedMapDBSetFactory implements BigOrderedSetFactory {

    @Override
    public BigOrderedSet<Utf8String> newSet(Configuration config) throws Exception {
        String filePath = config.get(Configuration.Key.DISK_BACKUP_FILEPATH);
        String clusterName = config.get(Configuration.Key.REPLICATION_CLUSTER_NAME);
        String timeout = config.get(Configuration.Key.REPLICATION_TIMEOUT_MS);
        
        if (StringHelper.isEmpty(filePath)) {
            throw new RuntimeException("Replicated MapDB implementation requires a DISK_BACKUP_FILEPATH property");
        } else if(StringHelper.isEmpty(clusterName)) {
            throw new RuntimeException("Replicated MapDB implementation requires a REPLICATION_CLUSTER_NAME property");
        } else if(StringHelper.isEmpty(timeout)) {
            throw new RuntimeException("Replicated MapDB implementation requires a REPLICATION_TIMEOUT_MS property");
        }

        try {
            long timeoutMs = Long.parseLong(timeout);
            BigOrderedReplicatedMapDBSet<Utf8String> database = new BigOrderedReplicatedMapDBSet<>(filePath, clusterName, timeoutMs);
            return database;
        } catch(NumberFormatException ex) {
            throw new NumberFormatException("Replicated MapDB implementation requires a REPLICATION_TIMEOUT_MS property to be a valid long value");
        }
    }
}
