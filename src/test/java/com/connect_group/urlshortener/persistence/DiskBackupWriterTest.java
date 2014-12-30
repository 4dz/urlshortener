package com.connect_group.urlshortener.persistence;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class DiskBackupWriterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void shouldCreateOutputFile_WhenFileDoesNotExist() throws IOException {
        File dir = folder.newFolder();
        String filename = dir.getAbsolutePath() + "/test.txt";
        File file = new File(filename);
        
        assertThat(file.exists(), equalTo(false));
        new DiskBackupWriter<>(filename);
        assertThat(file.exists(), equalTo(true));
    }

    @Test
    public void shouldLogSetEntry() throws IOException {
        File dir = folder.newFolder();
        String filename = dir.getAbsolutePath() + "/test.txt";
        DiskBackupWriter<String> writer = new DiskBackupWriter<>(filename);
        
        writer.add(0, "expected entry");

        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        String fileContents = new String(encoded, "UTF-8");
        assertThat(fileContents, equalTo("expected entry\n"));
    }

}