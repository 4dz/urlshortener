package com.perry.urlshortener.cluster;

import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class MessageReceiver extends ReceiverAdapter {

    public MessageReceiver() {}

    public void receive(Message message) {
        System.out.println("Message received!");
    }
}
