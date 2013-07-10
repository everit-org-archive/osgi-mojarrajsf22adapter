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

import java.io.IOException;
import java.lang.reflect.Proxy;

import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.everit.osgi.mojarrajsf22adapter.internal.DelegatingClassLoader;
import org.everit.osgi.mojarrajsf22adapter.internal.DelegatingClassLoaderInvocationHandler;

import com.sun.faces.spi.InjectionProvider;

public class OSGiFacesServlet implements Servlet {

    private Servlet target;

    public void destroy() {
        target.destroy();
    }

    public ServletConfig getServletConfig() {
        return target.getServletConfig();
    }

    public String getServletInfo() {
        return target.getServletInfo();
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        FacesServlet facesServlet = new FacesServlet();

        DelegatingClassLoader delegatingClassLoader = (DelegatingClassLoader) servletConfig.getServletContext()
                .getAttribute(OSGiConfigureListener.EVERIT_JSF_DELEGATING_CLASSLOADER);

        DelegatingClassLoaderInvocationHandler invocationHandler = new DelegatingClassLoaderInvocationHandler(
                facesServlet, delegatingClassLoader);
        Object newProxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[] { Servlet.class }, invocationHandler);

        target = (Servlet) newProxyInstance;
        target.init(servletConfig);
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        target.service(req, res);
    }

}
