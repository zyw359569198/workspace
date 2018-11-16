package com.reign.framework.netty.util;

import org.apache.commons.logging.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.io.*;

public class Scans
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog(Scans.class);
    }
    
    public static Set<Class<?>> getClasses(final String pack) {
        final Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        final boolean recursive = true;
        String packageName = pack;
        final String packageDirName = pack.replace(".", "/");
        try {
            final Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                final URL url = dirs.nextElement();
                final String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    final String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                }
                else if ("jar".equals(protocol)) {
                    try {
                        final JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                        final Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            final JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                final int idx = name.lastIndexOf(47);
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx == -1 && !recursive) || !name.endsWith(".class") || entry.isDirectory()) {
                                    continue;
                                }
                                final String className = name.substring(packageName.length() + 1, name.length() - 6);
                                try {
                                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(String.valueOf(packageName) + "." + className));
                                }
                                catch (ClassNotFoundException e) {
                                    Scans.log.error("", e);
                                }
                            }
                        }
                    }
                    catch (IOException e2) {
                        Scans.log.error("", e2);
                    }
                }
                else {
                    if (!"class".equals(protocol)) {
                        continue;
                    }
                    try {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(url.getFile()));
                    }
                    catch (ClassNotFoundException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e4) {
            Scans.log.error("", e4);
        }
        return classes;
    }
    
    private static void findAndAddClassesInPackageByFile(final String packageName, final String filePath, final boolean recursive, final Set<Class<?>> classes) {
        final File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        final File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return (recursive && file.isDirectory()) || file.getName().endsWith(".class");
            }
        });
        File[] array;
        for (int length = (array = dirFiles).length, i = 0; i < length; ++i) {
            final File file = array[i];
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(String.valueOf(packageName) + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            }
            else {
                final String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(String.valueOf(packageName) + "." + className));
                }
                catch (ClassNotFoundException e) {
                    Scans.log.error("", e);
                }
            }
        }
    }
}
