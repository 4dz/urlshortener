package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.util.Utf8String;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class BigOrderedMapDBSetFactoryTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String filepath;
    
    @Before
    public void init() throws IOException {
        filepath = folder.newFile().getAbsolutePath();
    }
    
    @Test
    public void shouldCreateMapDBInstance() throws IOException {
        BigOrderedMapDBSetFactory factory = new BigOrderedMapDBSetFactory();
        BigOrderedSet<Utf8String> set = factory.newSet(new Configuration() {
            @Override
            public String get(Key key) {
                if(key==Key.DISK_BACKUP_FILEPATH) {
                    return filepath;
                }
                return "";
            }
        });
        
        assertThat(set, instanceOf(BigOrderedMapDBSet.class));
    }

    @Test(expected=RuntimeException.class)
    public void shouldFailWhenNoFilePathSupplied() throws IOException {
        BigOrderedMapDBSetFactory factory = new BigOrderedMapDBSetFactory();
        factory.newSet(new Configuration() {
            @Override
            public String get(Key key) {
                return "";
            }
        });
    }
}