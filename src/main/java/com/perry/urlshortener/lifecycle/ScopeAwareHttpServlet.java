package com.perry.urlshortener.lifecycle;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public class ScopeAwareHttpServlet extends HttpServlet {
    private Scope scope;
    
    @Override
    public void init(ServletConfig config) {
        scope = (Scope) config.getServletContext().getAttribute(LifecycleListener.SCOPE_ATTRIBUTE_NAME);
    }

    public Scope getScope() {
        return scope;
    }
}
