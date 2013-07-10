package org.everit.osgi.mojarrajsf22adapter;

/*
 * Copyright (c) 2011, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

import java.lang.reflect.Proxy;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.everit.osgi.mojarrajsf22adapter.internal.DelegatingClassLoader;
import org.everit.osgi.mojarrajsf22adapter.internal.DelegatingClassLoaderInvocationHandler;

import com.sun.faces.config.ConfigureListener;
import com.sun.faces.spi.InjectionProvider;

public class OSGiConfigureListener implements ServletRequestListener,
        HttpSessionListener,
        ServletRequestAttributeListener,
        HttpSessionAttributeListener,
        ServletContextAttributeListener,
        ServletContextListener {

    public static final String EVERIT_JSF_DELEGATING_CLASSLOADER = "EVERIT_JSF_DELEGATING_CLASSLOADER";

    private final Object target;

    private final DelegatingClassLoaderInvocationHandler delegatingInvocationHandler;

    public OSGiConfigureListener() {
        ConfigureListener configureListener = new ConfigureListener();
        ClassLoader jsfApiClassLoader = FacesServlet.class.getClassLoader();
        ClassLoader jsfImplClassLoader = InjectionProvider.class.getClassLoader();
        delegatingInvocationHandler = new DelegatingClassLoaderInvocationHandler(
                configureListener, new ClassLoader[] { jsfApiClassLoader, jsfImplClassLoader });

        Object newProxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[] { ServletRequestListener.class, HttpSessionListener.class,
                        ServletRequestAttributeListener.class, HttpSessionAttributeListener.class,
                        ServletContextAttributeListener.class, ServletContextListener.class },
                delegatingInvocationHandler);

        target = newProxyInstance;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DelegatingClassLoader delegatingClassLoader = delegatingInvocationHandler.getDelegatingClassLoader();
        sce.getServletContext().setAttribute(EVERIT_JSF_DELEGATING_CLASSLOADER, delegatingClassLoader);
        ((ServletContextListener) target).contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ((ServletContextListener) target).contextDestroyed(sce);
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        ((ServletContextAttributeListener) target).attributeAdded(event);
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        ((ServletContextAttributeListener) target).attributeRemoved(event);

    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        ((ServletContextAttributeListener) target).attributeReplaced(event);
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        ((HttpSessionAttributeListener) target).attributeAdded(event);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        ((HttpSessionAttributeListener) target).attributeRemoved(event);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        ((HttpSessionAttributeListener) target).attributeReplaced(event);
    }

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        ((ServletRequestAttributeListener) target).attributeAdded(srae);

    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        ((ServletRequestAttributeListener) target).attributeRemoved(srae);
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        ((ServletRequestAttributeListener) target).attributeReplaced(srae);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        ((HttpSessionListener) target).sessionCreated(se);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ((HttpSessionListener) target).sessionDestroyed(se);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ((ServletRequestListener) target).requestDestroyed(sre);
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ((ServletRequestListener) target).requestInitialized(sre);
    }

}
