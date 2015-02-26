package com.perry.urlshortener.cluster;

import com.perry.urlshortener.persistence.MirroredSet;
import com.perry.urlshortener.persistence.SetEntry;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class MessageReceiver extends ReceiverAdapter {
    private final MirroredSet<Utf8String> database;
    
    public MessageReceiver(MirroredSet<Utf8String> database) {
        this.database=database;
        
    }

    public void receive(Message message) {
        System.out.println("Message received!");
        Object obj = message.getObject();
        if(obj instanceof SetEntry) {
            SetEntry entry = (SetEntry)obj;
            Object value = entry.getValue();
            if(value instanceof Utf8String) {
                database.mirror(entry.getKey(), (Utf8String)value);
            }
        }
    }
}
