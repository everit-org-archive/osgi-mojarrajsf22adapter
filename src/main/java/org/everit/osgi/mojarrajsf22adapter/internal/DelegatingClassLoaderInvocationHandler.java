package org.everit.osgi.mojarrajsf22adapter.internal;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DelegatingClassLoaderInvocationHandler implements InvocationHandler {

    private final DelegatingClassLoader delegatingClassLoader;

    private final Object target;

    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            return (ClassLoader) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        public java.lang.Object run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    });
        }
    }

    private static void setContextClassLoader(final ClassLoader classLoader) {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        } else {
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        public java.lang.Object run() {
                            Thread.currentThread().setContextClassLoader(classLoader);
                            return null;
                        }
                    });
        }
    }

    public DelegatingClassLoaderInvocationHandler(Object target, ClassLoader[] targets) {
        this.target = target;
        ClassLoader contextClassLoader = getContextClassLoader();
        for (int i = 0, n = targets.length; i < n && contextClassLoader != null; i++) {
            if (targets[i].equals(contextClassLoader)) {
                contextClassLoader = null;
            }
        }
        List<ClassLoader> classLoaderList = new ArrayList<ClassLoader>(Arrays.asList(targets));
        if (contextClassLoader != null) {
            classLoaderList.add(0, contextClassLoader);
        }
        classLoaderList.add(DelegatingClassLoader.class.getClassLoader());
        delegatingClassLoader = new DelegatingClassLoader(classLoaderList.toArray(new ClassLoader[0]));
    }
    
    public DelegatingClassLoaderInvocationHandler(Object target, DelegatingClassLoader delegatingClassLoader) {
        this.target = target;
        this.delegatingClassLoader = delegatingClassLoader;
    }

    public DelegatingClassLoader getDelegatingClassLoader() {
        return delegatingClassLoader;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ClassLoader contextClassLoader = getContextClassLoader();
        setContextClassLoader(delegatingClassLoader);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } finally {
            setContextClassLoader(contextClassLoader);
        }
    }

}
