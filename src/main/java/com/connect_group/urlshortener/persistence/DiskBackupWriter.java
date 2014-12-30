package com.connect_group.urlshortener.persistence;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DiskBackupWriter<E> implements SetModificationListener<E> {

    private final PrintWriter out;
    
    public DiskBackupWriter(String filePath) throws IOException {
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")));
    }
    
    @Override
    public void add(long index, E entry) {
        out.println(entry.toString());
        out.flush();
    }
}
