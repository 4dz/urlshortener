package com.perry.urlshortener.persistence;

import com.perry.urlshortener.util.Utf8String;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class DiskBackupWriter implements SetModificationListener<Utf8String> {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final PrintWriter out;
    private final String filePath;

    public DiskBackupWriter(String filePath) throws IOException {
        try {
            this.filePath = filePath;
            FileOutputStream fos = new FileOutputStream(filePath, true);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, UTF8)));
        } catch(FileNotFoundException ex) {
            throw new FileNotFoundException("FATAL: Unable to create or write to disk backup file.");
        }
    }
    
    @Override
    public void add(long index, Utf8String entry) {
        out.println(entry.toString());
        out.flush();
    }
    
    @Override
    public void close() {
        out.close();
    }

    public BigOrderedRAMSet<Utf8String> restore(int pageSize) throws IOException {
        BigOrderedRAMSet<Utf8String> set = new BigOrderedRAMSet<>(pageSize);

        File file = new File(filePath);
        if(file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF8));
                String line;
                while ((line = br.readLine()) != null) {
                    set.add(new Utf8String(line));
                }
                br.close();
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("FATAL: Unable to read disk backup file.");
            }
        }

        set.setSynchronizedListener(this);
        return set;
    }
}
