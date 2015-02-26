package com.perry.urlshortener.cluster;

import com.perry.urlshortener.persistence.SetEntry;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.Message;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class MessageReceiverTest {

    @Test
    public void shouldAddEntryToDatabase_WhenMessageContainsSetEntry() {
        BigOrderedDummySet database = new BigOrderedDummySet();
        MessageReceiver receiver = new MessageReceiver(database);
        SetEntry<Utf8String> received = new SetEntry<>(99, new Utf8String("fred"));
        receiver.receive(new Message(null,null,received));
        
        assertThat(database.get(99), equalTo(new Utf8String("fred")));
    }
}