package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.lifecycle.OnStartup;
import com.perry.urlshortener.util.Utf8String;

public class DatabaseOnStartup implements OnStartup {

    @Override
    public void onStart(MutableScope scope) {
        try {
            scope.setDatabase(createDatabase(scope.getConfiguration()));
        } catch(Exception e) {
            scope.setError(e.getMessage());
        }
    }

    private static BigOrderedSet<Utf8String> createDatabase(Configuration config) throws Exception {
        String className = config.get(Configuration.Key.DATABASE_FACTORY_CLASSPATH);
        try {
            Object oFactory = Class.forName(className).newInstance();
            if(oFactory instanceof BigOrderedSetFactory) {
                BigOrderedSetFactory factory = (BigOrderedSetFactory)oFactory;
                return factory.newSet(config);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate database " + className, e);
        }

        throw new RuntimeException("Failed to instantiate database " + className + " because it did not implement BigOrderedSetFactory");
    }
}
