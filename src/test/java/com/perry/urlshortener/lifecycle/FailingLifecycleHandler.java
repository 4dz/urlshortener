package com.perry.urlshortener.lifecycle;

public class FailingLifecycleHandler implements OnStartup, OnStop {
    
    public FailingLifecycleHandler() throws Exception {
        throw new Exception();    
    }
    
    @Override
    public void onStart(MutableScope scope) {
        
    }

    @Override
    public void onStop(Scope scope) {

    }
}
