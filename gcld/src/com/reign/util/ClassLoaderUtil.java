package com.reign.util;

import java.lang.reflect.*;
import java.util.*;
import sun.misc.*;
import java.io.*;
import java.net.*;

public class ClassLoaderUtil
{
    private static Field classes;
    private static Method addURL;
    private static URLClassLoader system;
    private static URLClassLoader ext;
    
    static {
        try {
            ClassLoaderUtil.classes = ClassLoader.class.getDeclaredField("classes");
            ClassLoaderUtil.addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        ClassLoaderUtil.classes.setAccessible(true);
        ClassLoaderUtil.addURL.setAccessible(true);
        ClassLoaderUtil.system = (URLClassLoader)getSystemClassLoader();
        ClassLoaderUtil.ext = (URLClassLoader)getExtClassLoader();
    }
    
    public static ClassLoader getSystemClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }
    
    public static ClassLoader getExtClassLoader() {
        return getSystemClassLoader().getParent();
    }
    
    public static List getClassesLoadedBySystemClassLoader() {
        return getClassesLoadedByClassLoader(getSystemClassLoader());
    }
    
    public static List getClassesLoadedByExtClassLoader() {
        return getClassesLoadedByClassLoader(getExtClassLoader());
    }
    
    public static List getClassesLoadedByClassLoader(final ClassLoader cl) {
        try {
            return (List)ClassLoaderUtil.classes.get(cl);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static URL[] getBootstrapURLs() {
        return Launcher.getBootstrapClassPath().getURLs();
    }
    
    public static URL[] getSystemURLs() {
        return ClassLoaderUtil.system.getURLs();
    }
    
    public static URL[] getExtURLs() {
        return ClassLoaderUtil.ext.getURLs();
    }
    
    private static void list(final PrintStream ps, final URL[] classPath) {
        for (int i = 0; i < classPath.length; ++i) {
            ps.println(classPath[i]);
        }
    }
    
    public static void listBootstrapClassPath() {
        listBootstrapClassPath(System.out);
    }
    
    public static void listBootstrapClassPath(final PrintStream ps) {
        ps.println("BootstrapClassPath:");
        list(ps, getBootstrapClassPath());
    }
    
    public static void listSystemClassPath() {
        listSystemClassPath(System.out);
    }
    
    public static void listSystemClassPath(final PrintStream ps) {
        ps.println("SystemClassPath:");
        list(ps, getSystemClassPath());
    }
    
    public static void listExtClassPath() {
        listExtClassPath(System.out);
    }
    
    public static void listExtClassPath(final PrintStream ps) {
        ps.println("ExtClassPath:");
        list(ps, getExtClassPath());
    }
    
    public static URL[] getBootstrapClassPath() {
        return getBootstrapURLs();
    }
    
    public static URL[] getSystemClassPath() {
        return getSystemURLs();
    }
    
    public static URL[] getExtClassPath() {
        return getExtURLs();
    }
    
    public static void addURL2SystemClassLoader(final URL url) {
        try {
            ClassLoaderUtil.addURL.invoke(ClassLoaderUtil.system, url);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void addURL2ExtClassLoader(final URL url) {
        try {
            ClassLoaderUtil.addURL.invoke(ClassLoaderUtil.ext, url);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void addClassPath(final String path) {
        addClassPath(new File(path));
    }
    
    public static void addExtClassPath(final String path) {
        addExtClassPath(new File(path));
    }
    
    public static void addClassPath(final File dirOrJar) {
        try {
            addURL2SystemClassLoader(dirOrJar.toURI().toURL());
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void addClassPath(final URL url) {
        addURL2SystemClassLoader(url);
    }
    
    public static void addExtClassPath(final File dirOrJar) {
        try {
            addURL2ExtClassLoader(dirOrJar.toURI().toURL());
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(final String[] args) {
        listSystemClassPath();
    }
    
    public static void addExtClassPath(final URL url) {
        addURL2ExtClassLoader(url);
    }
}
