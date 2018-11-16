package com.reign.framework.startup;

import java.net.*;
import java.io.*;
import java.util.*;

public class PluginInfo
{
    public Map<String, byte[]> clazzByteMap;
    public Map<String, File> fileMap;
    public Map<String, URL> urlMap;
    public String scanPackage;
    public String version;
    public String name;
    public String checkClass;
    public Vector<URL> urls;
    public Set<String> commands;
    public Set<String> components;
    public Set<String> sqls;
    public String sqlFile;
    
    public PluginInfo(final String name) {
        this.clazzByteMap = new LinkedHashMap<String, byte[]>();
        this.fileMap = new LinkedHashMap<String, File>();
        this.urlMap = new LinkedHashMap<String, URL>();
        this.name = name;
    }
    
    public byte[] findClass(final String name) {
        return this.clazzByteMap.get(name);
    }
    
    public File findResource(final String name) {
        return this.fileMap.get(name);
    }
    
    public Vector<URL> findResources(final String className) {
        Vector<URL> vector = null;
        if (className.startsWith(this.scanPackage)) {
            for (final Map.Entry<String, URL> entry : this.urlMap.entrySet()) {
                if (entry.getKey().startsWith(className)) {
                    if (vector == null) {
                        vector = new Vector<URL>();
                    }
                    vector.add(entry.getValue());
                }
            }
        }
        return vector;
    }
    
    public void init() throws Exception {
        final File file = this.findResource("plugin.xml");
        if (file == null) {
            throw new RuntimeException(String.valueOf(this.name) + " init failed, plugin.xml not found");
        }
        final XML xml = new XML(new FileInputStream(file));
        this.version = xml.get("version").getValue();
        this.scanPackage = xml.get("scanPackage").getValue();
        this.urls = this.findResources(this.scanPackage);
        this.sqlFile = xml.get("sqlResources").getValue();
        this.checkClass = xml.get("checkClass").getValue();
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public Enumeration<URL> getScanPackage() {
        return this.urls.elements();
    }
    
    public Set<String> getCommands() {
        return this.commands;
    }
    
    public void addCommands(final String command) {
        if (this.commands == null) {
            this.commands = new HashSet<String>();
        }
        this.commands.add(command);
    }
    
    public void addSpringComponents(final String component) {
        if (this.components == null) {
            this.components = new HashSet<String>();
        }
        this.components.add(component);
    }
    
    public void addSqls(final String sqlId) {
        if (this.sqls == null) {
            this.sqls = new HashSet<String>();
        }
        this.sqls.add(sqlId);
    }
    
    public void clear() {
        this.clazzByteMap.clear();
        this.fileMap.clear();
        this.urlMap.clear();
        if (this.urls != null) {
            this.urls.clear();
        }
        if (this.commands != null) {
            this.commands.clear();
        }
        if (this.components != null) {
            this.components.clear();
        }
        if (this.sqls != null) {
            this.sqls.clear();
        }
    }
}
