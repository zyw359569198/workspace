package com.reign.framework.startup;

import org.apache.log4j.*;
import org.apache.commons.logging.*;
import java.text.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

public class BootstrapHudson
{
    private static BootstrapHudson daemon;
    protected static final String START_HOME_TOKEN = "${start.home}";
    protected static final String SDATA_PATH_TOKEN = "${sdata.path}";
    protected static final String DEPENDENT_FILE = "dependent.xml";
    protected static final Integer IS_DIR;
    protected static final Integer IS_JAR;
    protected static final Integer IS_GLOB;
    protected static final Integer IS_URL;
    private static ClassLoader standardClassLoader;
    private static Properties properties;
    private static Map<String, DependentItem> jarNameMap;
    private static Log log;
    private static Object startObj;
    
    static {
        BootstrapHudson.daemon = null;
        IS_DIR = new Integer(0);
        IS_JAR = new Integer(1);
        IS_GLOB = new Integer(2);
        IS_URL = new Integer(3);
        BootstrapHudson.standardClassLoader = null;
        BootstrapHudson.properties = null;
        BootstrapHudson.log = null;
        BootstrapHudson.startObj = null;
    }
    
    public static void main(final String[] args) {
        if (BootstrapHudson.daemon == null) {
            BootstrapHudson.daemon = new BootstrapHudson();
            try {
                BootstrapHudson.daemon.init();
            }
            catch (Throwable t) {
                t.printStackTrace();
                return;
            }
        }
        try {
            BootstrapHudson.daemon.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() {
        this.setStartHome();
        final File file = new File(getFile("log4j.properties"));
        PropertyConfigurator.configureAndWatch(file.getAbsolutePath(), 3000L);
        (BootstrapHudson.log = LogFactory.getLog("bootstrap")).info("init start");
        try {
            this.loadProperties();
            this.loadSupportInfo();
            clearTempDir();
            this.initClassLoaders();
            Thread.currentThread().setContextClassLoader(BootstrapHudson.standardClassLoader);
            Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        }
        catch (Exception e) {
            e.printStackTrace();
            BootstrapHudson.log.error("init context error", e);
        }
        BootstrapHudson.log.info("init end");
    }
    
    private void loadSupportInfo() throws FileNotFoundException, IOException {
        BootstrapHudson.log.info("load jarInfo " + getFile("dependent.xml"));
        BootstrapHudson.jarNameMap = new HashMap<String, DependentItem>();
        System.setProperty("sdata.url", "");
        final String basePath = BootstrapHudson.properties.getProperty("jar.path");
        final XML xml = new XML(getFile("dependent.xml"));
        for (final XML.XMLNode node : xml.getList("item")) {
            if (node.get("type").getValue().equals("jar")) {
                final DependentItem dependentItem = new DependentItem(node);
                BootstrapHudson.jarNameMap.put(dependentItem.getName(), dependentItem);
            }
            else {
                if (!node.get("type").getValue().equals("sdata")) {
                    continue;
                }
                final DependentItem dependentItem = new DependentItem(node);
                System.setProperty("${sdata.path}", MessageFormat.format("{0}/{1}/{2}/{3}.zip", basePath, dependentItem.getName(), dependentItem.getVersion(), dependentItem.getJarName()));
            }
        }
    }
    
    private void loadProperties() throws FileNotFoundException, IOException {
        BootstrapHudson.log.info("load properties " + getFile("app.properties"));
        (BootstrapHudson.properties = new Properties()).load(new FileInputStream(new File(getFile("app.properties"))));
    }
    
    private void start() throws Exception {
        final Class<?> clazz = BootstrapHudson.standardClassLoader.loadClass(BootstrapHudson.properties.getProperty("startup.class"));
        BootstrapHudson.startObj = clazz.newInstance();
        final Method method = clazz.getMethod("startup", (Class<?>[])null);
        method.invoke(BootstrapHudson.startObj, (Object[])null);
    }
    
    private void initClassLoaders() throws Exception {
        BootstrapHudson.standardClassLoader = this.createClassLoader("common", Bootstrap.class.getClassLoader());
        BootstrapHudson.log.info("create common classLoader " + BootstrapHudson.standardClassLoader);
    }
    
    private ClassLoader createClassLoader(final String name, final ClassLoader parent) throws Exception {
        final String value = BootstrapHudson.properties.getProperty(String.valueOf(name) + ".loader");
        if (value == null || value.equals("")) {
            return parent;
        }
        final List<String> repositoryLocations = new ArrayList<String>();
        final List<Integer> repositoryTypes = new ArrayList<Integer>();
        final StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreElements()) {
            String repository = tokenizer.nextToken();
            boolean replace = false;
            final String before = repository;
            int i;
            while ((i = repository.indexOf("${start.home}")) >= 0) {
                replace = true;
                if (i > 0) {
                    repository = String.valueOf(repository.substring(0, i)) + getStartHome() + repository.substring(i + "${start.home}".length());
                }
                else {
                    repository = String.valueOf(getStartHome()) + repository.substring("${start.home}".length());
                }
            }
            if (replace && BootstrapHudson.log.isDebugEnabled()) {
                BootstrapHudson.log.debug("Expanded " + before + " to " + repository);
            }
            try {
                final URL url = new URL(repository);
                repositoryLocations.add(repository);
                repositoryTypes.add(BootstrapHudson.IS_URL);
            }
            catch (MalformedURLException ex) {
                if (repository.endsWith("*.jar")) {
                    repository = repository.substring(0, repository.length() - "*.jar".length());
                    repositoryLocations.add(repository);
                    repositoryTypes.add(BootstrapHudson.IS_GLOB);
                }
                else if (repository.endsWith(".jar")) {
                    repositoryLocations.add(repository);
                    repositoryTypes.add(BootstrapHudson.IS_JAR);
                }
                else {
                    repositoryLocations.add(repository);
                    repositoryTypes.add(BootstrapHudson.IS_DIR);
                }
            }
        }
        final String[] locations = repositoryLocations.toArray(new String[0]);
        final Integer[] types = repositoryTypes.toArray(new Integer[0]);
        final StandardClassLoader classLoader = (StandardClassLoader)createClassLoader(locations, types, parent);
        final String basePath = BootstrapHudson.properties.getProperty("jar.path");
        for (final String str : BootstrapHudson.jarNameMap.keySet()) {
            final DependentItem dependentItem = BootstrapHudson.jarNameMap.get(str);
            final String jarPath = MessageFormat.format("{0}/{1}/{2}/{3}.jar", basePath, str, dependentItem.getVersion(), dependentItem.getJarName());
            BootstrapHudson.log.info("start load " + jarPath);
            classLoader.parse(new URL(jarPath));
        }
        return classLoader;
    }
    
    public static ClassLoader createClassLoader(final String[] locations, final Integer[] types, final ClassLoader parent) throws Exception {
        BootstrapHudson.log.debug("Creating new class loader");
        final List<URL> list = new ArrayList<URL>();
        if (locations != null && types != null && locations.length == types.length) {
            for (int i = 0; i < locations.length; ++i) {
                final String location = locations[i];
                if (types[i] == BootstrapHudson.IS_URL) {
                    final URL url = new URL(location);
                    BootstrapHudson.log.debug("  Including URL " + url);
                    list.add(url);
                }
                else if (types[i] == BootstrapHudson.IS_DIR) {
                    File directory = new File(location);
                    directory = new File(directory.getCanonicalPath());
                    if (directory.exists() && directory.isDirectory()) {
                        if (directory.canRead()) {
                            final URL url2 = directory.toURI().toURL();
                            BootstrapHudson.log.debug("  Including directory " + url2);
                            list.add(url2);
                        }
                    }
                }
                else if (types[i] == BootstrapHudson.IS_JAR) {
                    File file = new File(location);
                    file = new File(file.getCanonicalPath());
                    if (file.exists()) {
                        if (file.canRead()) {
                            final URL url2 = file.toURI().toURL();
                            BootstrapHudson.log.debug("  Including jar file " + url2);
                            list.add(url2);
                        }
                    }
                }
                else if (types[i] == BootstrapHudson.IS_GLOB) {
                    final File directory = new File(location);
                    if (directory.exists() && directory.isDirectory()) {
                        if (directory.canRead()) {
                            BootstrapHudson.log.debug("  Including directory glob " + directory.getAbsolutePath());
                            final String[] filenames = directory.list();
                            for (int j = 0; j < filenames.length; ++j) {
                                final String filename = filenames[j].toLowerCase();
                                if (filename.endsWith(".jar")) {
                                    File file2 = new File(directory, filenames[j]);
                                    file2 = new File(file2.getCanonicalPath());
                                    if (file2.exists()) {
                                        if (file2.canRead()) {
                                            BootstrapHudson.log.debug("    Including glob jar file " + file2.getAbsolutePath());
                                            final URL url3 = file2.toURI().toURL();
                                            list.add(url3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        final URL[] array = list.toArray(new URL[list.size()]);
        if (BootstrapHudson.log.isDebugEnabled()) {
            for (int k = 0; k < array.length; ++k) {
                BootstrapHudson.log.debug("  location " + k + " is " + array[k]);
            }
        }
        final LibClassLoader libClassLoader = new LibClassLoader(array, parent);
        StandardClassLoader classLoader = null;
        classLoader = new StandardClassLoader(new URL[0], libClassLoader, BootstrapHudson.properties.getProperty("scan.path"));
        return classLoader;
    }
    
    private void setStartHome() {
        if (System.getProperty("start.home") != null) {
            return;
        }
        final File bootstrapJar = new File(System.getProperty("user.dir"), "bootstrap.jar");
        if (bootstrapJar.exists()) {
            try {
                System.setProperty("start.home", new File(System.getProperty("user.dir")).getCanonicalPath());
            }
            catch (Exception e) {
                System.setProperty("start.home", System.getProperty("user.dir"));
            }
        }
        else {
            System.setProperty("start.home", System.getProperty("user.dir"));
        }
    }
    
    private static String getStartHome() {
        return System.getProperty("start.home", System.getProperty("user.dir"));
    }
    
    private static String getFile(final String fileName) {
        final String path = String.valueOf(getStartHome()) + File.separator + "apps" + File.separator + fileName;
        return path;
    }
    
    private static String getDirectory(final String name, final boolean create) {
        final String path = String.valueOf(getStartHome()) + File.separator + name;
        if (create) {
            final File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.mkdir();
            }
        }
        return path;
    }
    
    private static void appExit() {
        clearTempDir();
    }
    
    private static void clearTempDir() {
        final File file = new File(getDirectory("temp", false));
        if (file.exists()) {
            final File[] files = file.listFiles();
            File[] array;
            for (int length = (array = files).length, i = 0; i < length; ++i) {
                final File f = array[i];
                f.delete();
            }
        }
    }
    
    protected class ShutdownHook extends Thread
    {
        @Override
        public void run() {
            appExit();
            BootstrapHudson.log.info("shut down compelete");
        }
    }
    
    protected static class MySignalHandler implements SignalHandler
    {
        private SignalHandler oldHandler;
        
        @Override
        public void handle(final Signal signal) {
            try {
                BootstrapHudson.log.info("handle signal " + signal.toString() + ":" + signal.getNumber());
                this.signalAction(signal);
                if (this.oldHandler != SignalHandler.SIG_DFL && this.oldHandler != SignalHandler.SIG_IGN) {
                    this.oldHandler.handle(signal);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void signalAction(final Signal signal) {
            appExit();
            BootstrapHudson.log.info("shut down compelete");
        }
        
        public static SignalHandler install(final String signalName) {
            final Signal signal = new Signal(signalName);
            final MySignalHandler handler = new MySignalHandler();
            handler.oldHandler = Signal.handle(signal, handler);
            return handler;
        }
    }
    
    private class DependentItem
    {
        private String name;
        private String version;
        private String jarName;
        
        public DependentItem(final XML.XMLNode node) {
            this.name = node.get("name").getValue();
            this.version = node.get("version").getValue();
            this.jarName = node.get("jarName").getValue();
            this.version = this.version.replace('.', '-');
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getVersion() {
            return this.version;
        }
        
        public String getJarName() {
            return this.jarName;
        }
    }
}
