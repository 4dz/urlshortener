package com.perry.urlshortener.persistence;

import com.perry.urlshortener.util.Utf8String;
import org.junit.After;
import org.junit.Before;
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
    private DiskBackupWriter writer;
    private String filename;
    
    @Before
    public void init() {
        writer = null;
        filename = null;
    }
    
    @After
    public void finished() {
        if(writer!=null) {
            writer.close();
        }
    }
    
    @Test
    public void shouldCreateOutputFile_WhenFileDoesNotExist() throws IOException {
        createTemporaryFilename();
        File file = new File(filename);
        
        assertThat(file.exists(), equalTo(false));
        writer = new DiskBackupWriter(filename);
        assertThat(file.exists(), equalTo(true));
    }

    @Test
    public void shouldRecordSetEntryToDisk() throws IOException {
        createDiskBackupWriter();
        
        writer.add(0, new Utf8String("expected entry"));

        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        String fileContents = new String(encoded, "UTF-8");
        assertThat(fileContents, equalTo("expected entry\n"));
    }

    private void createDiskBackupWriter() throws IOException {
        createTemporaryFilename();
        writer = new DiskBackupWriter(filename);
    }

    private void createTemporaryFilename() throws IOException {
        File dir = folder.newFolder();
        filename = dir.getAbsolutePath() + "/test.txt";
    }


}