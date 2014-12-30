package com.perry.urlshortener.persistence;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DiskBackupWriter<E> implements SetModificationListener<E> {

    private final PrintWriter out;
    
    public DiskBackupWriter(String filePath) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, "UTF-8")));
        } catch(FileNotFoundException ex) {
            throw new FileNotFoundException("FATAL: Unable to create or write to disk backup file.");
        }
    }
    
    @Override
    public void add(long index, E entry) {
        out.println(entry.toString());
        out.flush();
    }
}
