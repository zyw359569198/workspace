package com.reign.framework.startup;

import java.net.*;

public class LibClassLoader extends URLClassLoader
{
    public LibClassLoader(final URL[] urls, final ClassLoader parent) throws Exception {
        super(urls, parent);
    }
}
