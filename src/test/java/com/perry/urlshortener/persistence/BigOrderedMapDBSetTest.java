package com.perry.urlshortener.persistence;

import com.perry.urlshortener.util.Utf8String;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BigOrderedMapDBSetTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private BigOrderedSet<Utf8String> set;
    private String dbFilename;
    @Before
    public void init() throws IOException {
        File tmp = folder.newFile();
        dbFilename = tmp.getAbsolutePath();
        set = new BigOrderedMapDBSet<>(dbFilename);
    }
    
    @Test
    public void shouldPersistData() {
        Utf8String entry1 = new Utf8String("Entry1: " + Math.random());
        Utf8String entry2 = new Utf8String("Entry2: " + Math.random());
        set.add(entry1);
        set.add(entry2);
        set.close();

        assertThat(new File(dbFilename).exists(), equalTo(true));
        
        BigOrderedSet<Utf8String> persistedSet = new BigOrderedMapDBSet<>(dbFilename);
        assertThat(persistedSet.get(0L), equalTo(entry1));
        assertThat(persistedSet.get(1L), equalTo(entry2));
    }
    

    @After
    public void finished() {
        try {
            set.close();
        } catch(Throwable t) {
            // Would occur if test also closed the set
        }
    }
}