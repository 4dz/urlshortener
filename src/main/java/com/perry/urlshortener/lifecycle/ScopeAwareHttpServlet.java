package com.perry.urlshortener.lifecycle;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public abstract class ScopeAwareHttpServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) {
        Scope scope = (Scope) config.getServletContext().getAttribute(LifecycleListener.SCOPE_ATTRIBUTE_NAME);
        init(scope);
    }
    
    protected abstract void init(Scope scope);

}
