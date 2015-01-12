package com.perry.urlshortener.cluster;

import com.perry.urlshortener.persistence.SetEntry;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class NotifyClusterOnAddToDatabaseTest {
    
    @Test
    public void shouldBroadcastSetAddition() throws Exception {
        final SetEntry<Utf8String> expectedEntry = new SetEntry<>(99, new Utf8String("Expected"));
        JChannel mockChannel = mock(JChannel.class);
        
        final Message[] messageContainer = new Message[]{null};
        
        
        willAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Message messageToSend = (Message) invocationOnMock.getArguments()[0];
                messageContainer[0] = messageToSend;
                return null;
            }
        }).given(mockChannel).send(Matchers.<Message>anyObject());
        
        
        NotifyClusterOnAddToDatabase notifier = new NotifyClusterOnAddToDatabase(mockChannel);
        notifier.add(expectedEntry);

        assertThat(messageContainer[0].getObject(), instanceOf(SetEntry.class));

        @SuppressWarnings("unchecked")
        SetEntry<Utf8String> entry = (SetEntry<Utf8String>) messageContainer[0].getObject();
        assertThat(entry, equalTo(expectedEntry));
    }
    
    @Test
    public void shouldNotFail_WhenMessageFailsToSend() throws Exception {
        final SetEntry<Utf8String> expectedEntry = new SetEntry<>(99, new Utf8String("Expected"));
        JChannel mockChannel = mock(JChannel.class);
        
        willThrow(new Exception()).given(mockChannel).send(Matchers.<Message>anyObject());

        NotifyClusterOnAddToDatabase notifier = new NotifyClusterOnAddToDatabase(mockChannel);
        notifier.add(expectedEntry);
        
        // Exception was not thrown.
    }

    
    @Test
    public void shouldNotCloseChannel_WhenClosingDatabase() {

        JChannel mockChannel = mock(JChannel.class);
        NotifyClusterOnAddToDatabase notifier = new NotifyClusterOnAddToDatabase(mockChannel);
        notifier.close();
        verify(mockChannel, never()).close();
        
    }
}