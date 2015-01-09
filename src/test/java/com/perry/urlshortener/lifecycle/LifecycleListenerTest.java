package com.perry.urlshortener.lifecycle;

import com.perry.urlshortener.persistence.DatabaseOnStartup;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LifecycleListenerTest {
    LifecycleListener listener;
    ServletContext servletContext;
    List<OnStartup> onStartupHandlers;
    List<OnStop> onStopHandlers;
    
    @Before
    public void init() {
        onStartupHandlers = new ArrayList<>();
        onStopHandlers = new ArrayList<>();
        
        servletContext = mock(ServletContext.class);
        
        listener = new LifecycleListener() {
          
            @Override
            protected List<OnStartup> getOnStartupHandlers() {
                return onStartupHandlers;
            }

            @Override
            protected List<OnStop> getOnStopHandlers() {
                return onStopHandlers;
            }

        };
    }
    
    @Test
    public void shouldAddScopeToServletContext_WhenInitialising() {
        listener.contextInitialized(new ServletContextEvent(servletContext));

        verify(servletContext).setAttribute(eq(LifecycleListener.SCOPE_ATTRIBUTE_NAME), any(Scope.class));
    }
    
    @Test
    public void shouldActivateOnStartupHandlers_WhenInitialising() {
        final boolean called[] = {false, false};
        
        onStartupHandlers.add(new OnStartup() {
            @Override
            public void onStart(MutableScope scope) {
                called[0]=true;
            }
        });

        onStartupHandlers.add(new OnStartup() {
            @Override
            public void onStart(MutableScope scope) {
                called[1]=true;
            }
        });
        
        listener.contextInitialized(new ServletContextEvent(servletContext));
        
        assertThat(called[0], equalTo(true));
        assertThat(called[1], equalTo(true));
    }

    @Test(expected=RuntimeException.class)
    public void shouldThrowRuntimeException_WhenInitialising_AndStartupHandlerFailedToConstruct() {
        
        listener = new LifecycleListener() {
            @Override
            protected Class<?>[] getOnStartupClasses() {
                return new Class<?>[] {FailingLifecycleHandler.class};
            }
        };

        listener.contextInitialized(new ServletContextEvent(servletContext));
    }
    
    @Test
    public void shouldCreateOnStartupHandlers_WhenInitialising() {
        synchronized (this.getClass()) {
            System.setProperty(SuccessfullLifecycleHandler.SYSTEM_PROPERTY_NAME, "");
            
            listener = new LifecycleListener() {
                @Override
                protected Class<?>[] getOnStartupClasses() {
                    return new Class<?>[]{SuccessfullLifecycleHandler.class};
                }
            };

            listener.contextInitialized(new ServletContextEvent(servletContext));

            assertThat(System.getProperty(SuccessfullLifecycleHandler.SYSTEM_PROPERTY_NAME), equalTo("Started"));
        }
    }

    @Test
    public void shouldCreateOnStopHandlers_WhenStopping() {
        synchronized (this.getClass()) {
            System.setProperty(SuccessfullLifecycleHandler.SYSTEM_PROPERTY_NAME, "");
            
            listener = new LifecycleListener() {
                @Override
                protected Class<?>[] getOnStopClasses() {
                    return new Class<?>[]{SuccessfullLifecycleHandler.class};
                }
            };

            listener.contextDestroyed(new ServletContextEvent(servletContext));

            assertThat(System.getProperty(SuccessfullLifecycleHandler.SYSTEM_PROPERTY_NAME), equalTo("Stopped"));
        }
    }

    @Test
    public void shouldExecuteAllStopHandlers_WhenStopping_AndHandlerFailedToConstruct() {
        synchronized (this.getClass()) {
            System.setProperty(SuccessfullLifecycleHandler.SYSTEM_PROPERTY_NAME, "");
            
            listener = new LifecycleListener() {
                @Override
                protected Class<?>[] getOnStopClasses() {
                    return new Class<?>[]{FailingLifecycleHandler.class, SuccessfullLifecycleHandler.class};
                }
            };

            listener.contextDestroyed(new ServletContextEvent(servletContext));

            assertThat(System.getProperty(SuccessfullLifecycleHandler.SYSTEM_PROPERTY_NAME), equalTo("Stopped"));
        }
    }

    @Test
    public void shouldExecuteAllStopHandlers_WhenStopping_AndStopHandlerFailed() {
        final boolean called[] = {false, false};

        onStopHandlers.add(new OnStop() {
            @Override
            public void onStop(Scope scope) {
                throw new RuntimeException("eek");
            }
        });

        onStopHandlers.add(new OnStop() {
            @Override
            public void onStop(Scope scope) {
                called[1]=true;
            }
        });

        listener.contextDestroyed(new ServletContextEvent(servletContext));

        assertThat(called[0], equalTo(false));
        assertThat(called[1], equalTo(true));
    }
    
    @Test
    public void shouldStartDatabase_OnStartup() {
        listener = new LifecycleListener();
        Class dbClass = DatabaseOnStartup.class;
        assertThat(listener.getOnStartupClasses(), hasItemInArray(dbClass));
    }
    
    @Test
    public void shouldCreateService_OnStartup() {
        fail("TODO: new service package; ShortenerServiceOnStartup; Get service from scope in servlet");
        
    }
}