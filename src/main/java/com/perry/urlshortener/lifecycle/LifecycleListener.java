package com.perry.urlshortener.lifecycle;

import com.perry.urlshortener.cluster.ClusterOnStartup;
import com.perry.urlshortener.cluster.ClusterOnStop;
import com.perry.urlshortener.config.ConfigurationImpl;
import com.perry.urlshortener.persistence.DatabaseOnStartup;
import com.perry.urlshortener.service.ShortenerServiceOnStartup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.List;

/**
 * As we aren't using a nice IoC framework, like Spring/Guice,
 * provide a kind of JavaConfig alternative.
 */
@WebListener
public class LifecycleListener implements ServletContextListener {
    
    private static final Class<?>[] STARTUP = {DatabaseOnStartup.class, ClusterOnStartup.class, ShortenerServiceOnStartup.class};
    private static final Class<?>[] STOP = {ClusterOnStop.class};
    public static final String SCOPE_ATTRIBUTE_NAME="com.perry.urlshortener.lifecycle.LifecycleListener.Scope";
    
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        MutableScope scope = new MutableScope(ConfigurationImpl.getInstance());
        servletContextEvent.getServletContext().setAttribute(SCOPE_ATTRIBUTE_NAME, scope);
        
        for(OnStartup startupHandler : getOnStartupHandlers()) {
            startupHandler.onStart(scope);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Scope scope = (Scope) servletContextEvent.getServletContext().getAttribute(SCOPE_ATTRIBUTE_NAME);
        for(OnStop stopHandler : getOnStopHandlers()) {
            try {
                stopHandler.onStop(scope);
            } catch(Throwable t) {
                System.err.println("Failed onStop " + stopHandler.getClass().getSimpleName());
            }
        }
    }

    protected List<OnStartup> getOnStartupHandlers() {
        ArrayList<OnStartup> startupHandlers = new ArrayList<>();
        for(Class<?> clazz : getOnStartupClasses()) {
            try {
                Object o = clazz.newInstance();
                if(o instanceof OnStartup) {
                    startupHandlers.add((OnStartup)o);
                }
            } catch (Throwable t) {
                throw new RuntimeException("Unable to start application as failed to create handler " + clazz.getSimpleName());
            }
        }
        
        
        return startupHandlers;
    }
    
    protected List<OnStop> getOnStopHandlers() {
        ArrayList<OnStop> stopHandlers = new ArrayList<>();
        for(Class<?> clazz : getOnStopClasses()) {
            try {
                Object o = clazz.newInstance();
                if(o instanceof OnStop) {
                    stopHandlers.add((OnStop) o);
                }
            } catch (Throwable t) {
                System.err.println("Failed onStop " + clazz.getSimpleName());
            }
        }

        return stopHandlers;
    }
    
    protected Class<?>[] getOnStartupClasses() {
        return STARTUP;
    }

    protected Class<?>[] getOnStopClasses() {
        return STOP;
    }

}
