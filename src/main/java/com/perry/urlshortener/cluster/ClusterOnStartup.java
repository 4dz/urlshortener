package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.lifecycle.OnStartup;
import org.jgroups.JChannel;

public class ClusterOnStartup implements OnStartup {

    @Override
    public void onStart(MutableScope scope) {
        System.out.println("STARTED!");

        // TODO this should be controlled by a flag and/or set in the java command line
        // which starts tomcat.
        System.setProperty("java.net.preferIPv4Stack","true");
        MessageReceiver receiver = new MessageReceiver();
        try {

            JChannel channel = new JChannel();
            channel.setReceiver(receiver);
            channel.connect("urlshortener");

            scope.setCluster(channel);
            scope.getDatabase().addSynchronizedListener(new NotifyClusterOnAddToDatabase(channel));
        } catch (Exception e) {
            // TODO!
            e.printStackTrace();
        }
    }

}
