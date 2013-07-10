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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Classloader that tries multiple classloaders to load a class.
 */
public class DelegatingClassLoader extends ClassLoader {

    private final ClassLoader[] targets;

    public DelegatingClassLoader(ClassLoader... targets) {
        this.targets = targets;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> result = null;
        for (int i = 0, n = targets.length; i < n && result == null; i++) {
            try {
            result = targets[i].loadClass(name);
            } catch (ClassNotFoundException e) {
                // Do nothing
            }
        }
        return (result != null) ? result : super.findClass(name);
    }

    @Override
    protected URL findResource(String name) {
        URL result = null;
        for (int i = 0, n = targets.length; i < n && result == null; i++) {
            result = targets[i].getResource(name);
        }
        return (result != null) ? result : super.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        Set<URL> result = new HashSet<URL>();
        
        for (int i = 0, n = targets.length; i < n; i++) {
            Enumeration<URL> subResult = targets[i].getResources(name);
            while (subResult.hasMoreElements()) {
                result.add(subResult.nextElement());
            }
        }
        
        return Collections.enumeration(result);
    }
}
