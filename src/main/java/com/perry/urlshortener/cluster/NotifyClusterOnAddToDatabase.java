package com.perry.urlshortener.cluster;

import com.perry.urlshortener.persistence.SetEntry;
import com.perry.urlshortener.persistence.SetModificationListener;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.JChannel;
import org.jgroups.Message;

public class NotifyClusterOnAddToDatabase implements SetModificationListener<Utf8String> {

    private final JChannel channel;
    
    public NotifyClusterOnAddToDatabase(JChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public void add(SetEntry<Utf8String> setEntry) {
        Message message = new Message(null,null,setEntry);
        try {
            channel.send(message);
        } catch (Exception e) {
            // TODO when you cant send a message, continue...?
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }
}
