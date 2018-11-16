package com.reign.framework.startup;

import org.apache.log4j.*;
import org.apache.commons.logging.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

public class Bootstrap
{
    private static Bootstrap daemon;
    protected static final String START_HOME_TOKEN = "${start.home}";
    protected static final String SDATA_PATH_TOKEN = "${sdata.path}";
    protected static final Integer IS_DIR;
    protected static final Integer IS_JAR;
    protected static final Integer IS_GLOB;
    protected static final Integer IS_URL;
    private static ClassLoader standardClassLoader;
    private static Properties properties;
    private static String password;
    private static Log log;
    private static Object startObj;
    
    static {
        Bootstrap.daemon = null;
        IS_DIR = new Integer(0);
        IS_JAR = new Integer(1);
        IS_GLOB = new Integer(2);
        IS_URL = new Integer(3);
        Bootstrap.standardClassLoader = null;
        Bootstrap.properties = null;
        Bootstrap.password = null;
        Bootstrap.log = null;
        Bootstrap.startObj = null;
    }
    
    public static void main(final String[] args) {
        if (Bootstrap.daemon == null) {
            Bootstrap.daemon = new Bootstrap();
            try {
                Bootstrap.daemon.init();
            }
            catch (Throwable t) {
                t.printStackTrace();
                return;
            }
        }
        try {
            Bootstrap.daemon.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init() {
        this.setStartHome();
        final File file = new File(getFile("log4j.properties"));
        PropertyConfigurator.configureAndWatch(file.getAbsolutePath(), 3000L);
        (Bootstrap.log = LogFactory.getLog("bootstrap")).info("init start");
        try {
            this.loadProperties();
            clearTempDir();
            this.initClassLoaders();
            Thread.currentThread().setContextClassLoader(Bootstrap.standardClassLoader);
            Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        }
        catch (Exception e) {
            e.printStackTrace();
            Bootstrap.log.error("init context error", e);
        }
        Bootstrap.log.info("init end");
    }
    
    private void loadProperties() throws FileNotFoundException, IOException {
        Bootstrap.log.info("load properties " + getFile("app.properties"));
        (Bootstrap.properties = new Properties()).load(new FileInputStream(new File(getFile("app.properties"))));
    }
    
    private void getPassword(final Properties prop) throws Exception {
        final String path = prop.getProperty("file.path");
        final String key = prop.getProperty("file.key");
        final String encrypt = prop.getProperty("file.encrypt");
        if (path != null && !"".equals(path.trim())) {
            final Properties properties = new Properties();
            properties.load(new FileInputStream(new File(getFile(path))));
            Bootstrap.password = properties.getProperty(key);
            "true".equalsIgnoreCase(encrypt);
        }
    }
    
    private void start() throws Exception {
        final Class<?> clazz = Bootstrap.standardClassLoader.loadClass(Bootstrap.properties.getProperty("startup.class"));
        Bootstrap.startObj = clazz.newInstance();
        final Method method = clazz.getMethod("startup", (Class<?>[])null);
        method.invoke(Bootstrap.startObj, (Object[])null);
    }
    
    private void initClassLoaders() throws Exception {
        Bootstrap.standardClassLoader = this.createClassLoader("common", Bootstrap.class.getClassLoader());
        Bootstrap.log.info("create common classLoader " + Bootstrap.standardClassLoader);
    }
    
    private ClassLoader createClassLoader(final String name, final ClassLoader parent) throws Exception {
        final String value = Bootstrap.properties.getProperty(String.valueOf(name) + ".loader");
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
            if (replace && Bootstrap.log.isDebugEnabled()) {
                Bootstrap.log.debug("Expanded " + before + " to " + repository);
            }
            try {
                final URL url = new URL(repository);
                repositoryLocations.add(repository);
                repositoryTypes.add(Bootstrap.IS_URL);
            }
            catch (MalformedURLException ex) {
                if (repository.endsWith("*.jar")) {
                    repository = repository.substring(0, repository.length() - "*.jar".length());
                    repositoryLocations.add(repository);
                    repositoryTypes.add(Bootstrap.IS_GLOB);
                }
                else if (repository.endsWith(".jar")) {
                    repositoryLocations.add(repository);
                    repositoryTypes.add(Bootstrap.IS_JAR);
                }
                else {
                    repositoryLocations.add(repository);
                    repositoryTypes.add(Bootstrap.IS_DIR);
                }
            }
        }
        final String[] locations = repositoryLocations.toArray(new String[0]);
        final Integer[] types = repositoryTypes.toArray(new Integer[0]);
        final StandardClassLoader classLoader = (StandardClassLoader)createClassLoader(locations, types, parent);
        final String jarPath = Bootstrap.properties.getProperty("jar.path");
        String[] split;
        for (int length = (split = jarPath.split(",")).length, j = 0; j < length; ++j) {
            final String str = split[j];
            classLoader.parse(new URL(str));
        }
        final String sdataPath = Bootstrap.properties.getProperty("sdata.path");
        if (sdataPath != null) {
            classLoader.parseSdata(new URL(sdataPath));
            System.setProperty("${sdata.path}", sdataPath);
        }
        return classLoader;
    }
    
    public static ClassLoader createClassLoader(final String[] locations, final Integer[] types, final ClassLoader parent) throws Exception {
        Bootstrap.log.debug("Creating new class loader");
        final List<URL> list = new ArrayList<URL>();
        if (locations != null && types != null && locations.length == types.length) {
            for (int i = 0; i < locations.length; ++i) {
                final String location = locations[i];
                if (types[i] == Bootstrap.IS_URL) {
                    final URL url = new URL(location);
                    Bootstrap.log.debug("  Including URL " + url);
                    list.add(url);
                }
                else if (types[i] == Bootstrap.IS_DIR) {
                    File directory = new File(location);
                    directory = new File(directory.getCanonicalPath());
                    if (directory.exists() && directory.isDirectory()) {
                        if (directory.canRead()) {
                            final URL url2 = directory.toURI().toURL();
                            Bootstrap.log.debug("  Including directory " + url2);
                            list.add(url2);
                        }
                    }
                }
                else if (types[i] == Bootstrap.IS_JAR) {
                    File file = new File(location);
                    file = new File(file.getCanonicalPath());
                    if (file.exists()) {
                        if (file.canRead()) {
                            final URL url2 = file.toURI().toURL();
                            Bootstrap.log.debug("  Including jar file " + url2);
                            list.add(url2);
                        }
                    }
                }
                else if (types[i] == Bootstrap.IS_GLOB) {
                    final File directory = new File(location);
                    if (directory.exists() && directory.isDirectory()) {
                        if (directory.canRead()) {
                            Bootstrap.log.debug("  Including directory glob " + directory.getAbsolutePath());
                            final String[] filenames = directory.list();
                            for (int j = 0; j < filenames.length; ++j) {
                                final String filename = filenames[j].toLowerCase();
                                if (filename.endsWith(".jar")) {
                                    File file2 = new File(directory, filenames[j]);
                                    file2 = new File(file2.getCanonicalPath());
                                    if (file2.exists()) {
                                        if (file2.canRead()) {
                                            Bootstrap.log.debug("    Including glob jar file " + file2.getAbsolutePath());
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
        if (Bootstrap.log.isDebugEnabled()) {
            for (int k = 0; k < array.length; ++k) {
                Bootstrap.log.debug("  location " + k + " is " + array[k]);
            }
        }
        final LibClassLoader libClassLoader = new LibClassLoader(array, parent);
        StandardClassLoader classLoader = null;
        classLoader = new StandardClassLoader(new URL[0], libClassLoader, Bootstrap.properties.getProperty("scan.path"));
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
            Bootstrap.log.info("shut down compelete");
        }
    }
    
    protected static class MySignalHandler implements SignalHandler
    {
        private SignalHandler oldHandler;
        
        @Override
        public void handle(final Signal signal) {
            try {
                Bootstrap.log.info("handle signal " + signal.toString() + ":" + signal.getNumber());
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
            Bootstrap.log.info("shut down compelete");
        }
        
        public static SignalHandler install(final String signalName) {
            final Signal signal = new Signal(signalName);
            final MySignalHandler handler = new MySignalHandler();
            handler.oldHandler = Signal.handle(signal, handler);
            return handler;
        }
    }
}
