package com.perry.urlshortener.lifecycle;

public class SuccessfullLifecycleHandler implements OnStartup, OnStop {
    public static final String SYSTEM_PROPERTY_NAME = "com.perry.urlshortener.lifecycle";
    public SuccessfullLifecycleHandler() {
        System.setProperty(SYSTEM_PROPERTY_NAME, "");
    }
    
    @Override
    public void onStart(MutableScope scope) {
        System.setProperty(SYSTEM_PROPERTY_NAME, "Started");
    }

    @Override
    public void onStop(Scope scope) {
        System.setProperty(SYSTEM_PROPERTY_NAME, "Stopped");
    }
}
