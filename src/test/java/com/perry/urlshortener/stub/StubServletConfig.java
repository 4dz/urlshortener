package com.perry.urlshortener.stub;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Properties;

import static org.mockito.Mockito.mock;

public class StubServletConfig implements ServletConfig {
    public String servletName = "";
    public final Properties props = new Properties();
    public final ServletContext mockServletContext;
    
    public StubServletConfig() {
        mockServletContext = mock(ServletContext.class);
    }
    
    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return mockServletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return props.getProperty(s);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getInitParameterNames() {
        return (Enumeration<String>) props.propertyNames();
    }
    
    
}
