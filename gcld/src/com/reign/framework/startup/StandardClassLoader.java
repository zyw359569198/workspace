package com.reign.framework.startup;

import java.util.jar.*;
import org.apache.commons.lang.*;
import com.sun.org.apache.xml.internal.security.utils.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class StandardClassLoader extends URLClassLoader
{
    private Map<String, byte[]> clazzByteMap;
    private Map<String, byte[]> sdataByteMap;
    private Map<String, File> fileMap;
    private Map<String, URL> urlMap;
    private String classpath;
    private Map<String, PluginInfo> pluginMap;
    private Map<String, SubClassLoader> loaderMap;
    private Set<String> classes;
    
    public StandardClassLoader(final URL[] urls, final ClassLoader parent, final String classpath) throws Exception {
        super(urls, parent);
        this.clazzByteMap = new LinkedHashMap<String, byte[]>();
        this.sdataByteMap = new LinkedHashMap<String, byte[]>();
        this.fileMap = new LinkedHashMap<String, File>();
        this.urlMap = new LinkedHashMap<String, URL>();
        this.pluginMap = new HashMap<String, PluginInfo>();
        this.loaderMap = new HashMap<String, SubClassLoader>();
        this.classes = new HashSet<String>();
        this.classpath = classpath;
    }
    
    @Override
	public Class<?> findClass(final String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            clazz = super.findClass(name);
        }
        catch (Throwable t) {}
        if (clazz != null) {
            return clazz;
        }
        final byte[] bytes = this.clazzByteMap.get(name);
        if (bytes != null) {
            clazz = this.defineClass(name, bytes, 0, bytes.length);
            this.definePackage(name);
            return clazz;
        }
        try {
            clazz = this._findClass(name);
        }
        catch (ClassNotFoundException ex) {}
        catch (Throwable t2) {}
        if (clazz != null) {
            return clazz;
        }
        throw new ClassNotFoundException(name);
    }
    
    private Class<?> _findClass(final String name) throws ClassNotFoundException {
        final Iterator<Map.Entry<String, SubClassLoader>> iterator = this.loaderMap.entrySet().iterator();
        if (iterator.hasNext()) {
            final Map.Entry<String, SubClassLoader> entry = iterator.next();
            return entry.getValue().findClass(name);
        }
        return null;
    }
    
    private byte[] _findClassBytes(final String name) {
        final Iterator<Map.Entry<String, PluginInfo>> iterator = this.pluginMap.entrySet().iterator();
        if (iterator.hasNext()) {
            final Map.Entry<String, PluginInfo> entry = iterator.next();
            return entry.getValue().findClass(name);
        }
        return null;
    }
    
    public synchronized PluginInfo loadPlugin(final String name, final String url) throws Exception {
        if (this.pluginMap.containsKey(name)) {
            throw new RuntimeException(String.valueOf(name) + " plugin is already loaded");
        }
        final PluginInfo pluginInfo = new PluginInfo(name);
        this.parse(pluginInfo, new URL(url));
        pluginInfo.init();
        this.pluginMap.put(name, pluginInfo);
        this.loaderMap.put(name, new SubClassLoader(pluginInfo, this));
        return pluginInfo;
    }
    
    public void parseSdata(final URL url) throws Exception {
        final JarInputStream jar = new JarInputStream(this.getRemoteResource(url));
        JarEntry entry = null;
        while ((entry = jar.getNextJarEntry()) != null) {
            if (entry.getName().toLowerCase().endsWith("sdata.xml")) {
                this.sdataByteMap.put("sdata", this.getData(jar));
            }
            else {
                if (!entry.getName().toLowerCase().endsWith("version.txt")) {
                    continue;
                }
                this.sdataByteMap.put("sdataVersion", this.getData(jar));
            }
        }
    }
    
    public void parse(final URL url) throws Exception {
        final JarInputStream jar = new JarInputStream(this.getRemoteResource(url));
        JarEntry entry = null;
        while ((entry = jar.getNextJarEntry()) != null) {
            if (entry.getName().toLowerCase().endsWith(".class")) {
                final String className = this.getClassName(entry.getName());
                this.clazzByteMap.put(className, this.getData(jar));
                if (className.indexOf("$") != -1) {
                    continue;
                }
                this.urlMap.put(className, new URL("class", null, -1, className, new MyURLStreamHandler((MyURLStreamHandler)null)));
            }
            else {
                if (entry.isDirectory()) {
                    continue;
                }
                final String name = entry.getName();
                if (name.endsWith(".java")) {
                    continue;
                }
                if (name.endsWith("changelog.txt")) {
                    continue;
                }
                if (name.endsWith(".js")) {
                    continue;
                }
                File file = null;
                file = File.createTempFile(name.replace("/", "."), "." + StringUtils.substringAfterLast(name, "."));
                this.getFileData(file, jar);
                this.fileMap.put(entry.getName(), file);
            }
        }
    }
    
    private InputStream getRemoteResource(final URL url) throws Exception {
        int times = 0;
        Exception exception = null;
        while (times < 3) {
            try {
                final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Authorization", new String("Basic " + Base64.encode(new String("rzcore:rzcore2012!@#$").getBytes())));
                conn.connect();
                return conn.getInputStream();
            }
            catch (Exception t) {
                exception = t;
                ++times;
                Thread.sleep((long)(1000.0 + Math.random() * 1000.0));
            }
        }
        throw exception;
    }
    
    public void parse(final PluginInfo pluginInfo, final URL url) throws Exception {
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", new String("Basic " + Base64.encode(new String("rzcore:rzcore2012!@#$").getBytes())));
        conn.connect();
        final JarInputStream jar = new JarInputStream(conn.getInputStream());
        JarEntry entry = null;
        while ((entry = jar.getNextJarEntry()) != null) {
            if (entry.getName().toLowerCase().endsWith(".class")) {
                final String className = this.getClassName(entry.getName());
                pluginInfo.clazzByteMap.put(className, this.getData(jar));
                if (className.indexOf("$") != -1) {
                    continue;
                }
                pluginInfo.urlMap.put(className, new URL("class", null, -1, className, new MyURLStreamHandler((MyURLStreamHandler)null)));
            }
            else {
                if (entry.isDirectory()) {
                    continue;
                }
                final String name = entry.getName();
                if (name.endsWith(".java")) {
                    continue;
                }
                if (name.endsWith("changelog.txt")) {
                    continue;
                }
                if (name.endsWith(".js")) {
                    continue;
                }
                File file = null;
                file = File.createTempFile(name.replace("/", "."), "." + StringUtils.substringAfterLast(name, "."));
                this.getFileData(file, jar);
                pluginInfo.fileMap.put(entry.getName(), file);
            }
        }
    }
    
    @Override
    public URL findResource(final String name) {
        File file = this.fileMap.get(name);
        if (file != null) {
            try {
                return file.toURI().toURL();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        file = this._findResource(name);
        if (file != null) {
            try {
                return file.toURI().toURL();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return super.findResource(name);
    }
    
    private File _findResource(final String name) {
        final Iterator<Map.Entry<String, PluginInfo>> iterator = this.pluginMap.entrySet().iterator();
        if (iterator.hasNext()) {
            final Map.Entry<String, PluginInfo> entry = iterator.next();
            final File file = entry.getValue().findResource(name);
            return file;
        }
        return null;
    }
    
    @Override
    public Enumeration<URL> findResources(final String name) throws IOException {
        Enumeration<URL> urls = null;
        String className = name.replace("/", ".");
        if (className.endsWith(".class")) {
            className = this.getClassName(className);
        }
        if (className.startsWith(this.classpath)) {
            final Vector<URL> vector = new Vector<URL>();
            for (final Map.Entry<String, URL> entry : this.urlMap.entrySet()) {
                if (entry.getKey().startsWith(className)) {
                    vector.add(entry.getValue());
                }
            }
            if (vector.size() > 0) {
                urls = vector.elements();
            }
        }
        if (urls == null || !urls.hasMoreElements()) {
            urls = this._findResources(className);
        }
        if (urls == null || !urls.hasMoreElements()) {
            final File file = this.fileMap.get(name);
            if (file != null) {
                try {
                    final Vector<URL> vector2 = new Vector<URL>();
                    vector2.add(file.toURI().toURL());
                    urls = vector2.elements();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (urls == null || !urls.hasMoreElements()) {
            final File file = this._findResource(name);
            if (file != null) {
                try {
                    final Vector<URL> vector2 = new Vector<URL>();
                    vector2.add(file.toURI().toURL());
                    urls = vector2.elements();
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        if (urls == null || !urls.hasMoreElements()) {
            urls = super.findResources(name);
        }
        if (urls != null && !urls.hasMoreElements()) {
            return null;
        }
        return urls;
    }
    
    private Enumeration<URL> _findResources(final String className) {
        final Vector<URL> vector = new Vector<URL>();
        for (final Map.Entry<String, PluginInfo> entry : this.pluginMap.entrySet()) {
            final Vector<URL> temp = entry.getValue().findResources(className);
            if (temp != null) {
                vector.addAll(temp);
            }
        }
        if (vector.size() > 0) {
            return vector.elements();
        }
        return null;
    }
    
    @Override
    public InputStream getResourceAsStream(final String name) {
        if (name.endsWith(".class")) {
            final String clazzName = this.getClassName(name);
            byte[] bytes = this.clazzByteMap.get(clazzName);
            if (bytes != null) {
                return new ByteArrayInputStream(bytes);
            }
            bytes = this._findClassBytes(clazzName);
            if (bytes != null) {
                return new ByteArrayInputStream(bytes);
            }
        }
        else if (name.equals("sdata")) {
            final byte[] bytes2 = this.sdataByteMap.get("sdata");
            if (bytes2 != null) {
                return new ByteArrayInputStream(bytes2);
            }
        }
        else if (name.equals("sdataVersion")) {
            final byte[] bytes2 = this.sdataByteMap.get("sdataVersion");
            if (bytes2 != null) {
                return new ByteArrayInputStream(bytes2);
            }
        }
        return super.getResourceAsStream(name);
    }
    
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        Class<?> c = null;
        if (!this.classes.contains(name)) {
            c = this.findLoadedClass(name);
        }
        if (c != null) {
            return c;
        }
        if (!this.classes.contains(name)) {
            try {
                c = super.loadClass(name);
            }
            catch (ClassNotFoundException ex) {}
            catch (Throwable t) {}
        }
        if (c != null) {
            return c;
        }
        try {
            c = this.findClass(name);
        }
        catch (ClassNotFoundException ex2) {}
        catch (Throwable t2) {}
        if (c != null) {
            return c;
        }
        throw new ClassNotFoundException(name);
    }
    
    private String getClassName(final String name) {
        return name.substring(0, name.length() - ".class".length()).replace('/', '.');
    }
    
    private byte[] getData(final JarInputStream jar) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size = -1;
        final byte[] buff = new byte[1024];
        while (jar.available() > 0) {
            size = jar.read(buff);
            if (size > 0) {
                baos.write(buff, 0, size);
            }
        }
        return baos.toByteArray();
    }
    
    private InputStream getCopyInputStream(final InputStream is) throws IOException {
        is.reset();
        return this.getInputStreamFromURL(is);
    }
    
    private ByteArrayInputStream getInputStreamFromURL(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final byte[] buff = new byte[2048];
        int len = -1;
        while ((len = inputStream.read(buff)) != -1) {
            if (len > 0) {
                bos.write(buff, 0, len);
            }
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }
    
    private void definePackage(final String className) {
        final int pos = className.lastIndexOf(46);
        String packageName = null;
        if (pos != -1) {
            packageName = className.substring(0, pos);
        }
        Package pkg = null;
        if (packageName != null) {
            pkg = this.getPackage(packageName);
            if (pkg == null) {
                try {
                    this.definePackage(packageName, null, null, null, null, null, null, null);
                }
                catch (IllegalArgumentException ex) {}
                pkg = this.getPackage(packageName);
            }
        }
    }
    
    private void getFileData(final File file, final JarInputStream jar) {
        FileOutputStream fos = null;
        try {
            final byte[] buff = new byte[1024];
            fos = new FileOutputStream(file);
            while (true) {
                final int length = jar.read(buff, 0, buff.length);
                if (-1 == length) {
                    break;
                }
                fos.write(buff, 0, length);
            }
            fos.flush();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (fos != null) {
            try {
                fos.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private class MyURLStreamHandler extends URLStreamHandler
    {
        private InputStream is;
        
        @Override
        protected URLConnection openConnection(final URL url) throws IOException {
            return new URLConnection(url) {
                @Override
                public void connect() throws IOException {
                    if (!this.connected) {
                        final byte[] buff = (String.valueOf(this.url.getFile().substring(this.url.getFile().lastIndexOf(".") + 1)) + ".class").getBytes();
                        MyURLStreamHandler.access$0(MyURLStreamHandler.this, new ByteArrayInputStream(buff));
                    }
                }
                
                @Override
                public InputStream getInputStream() throws IOException {
                    if (!this.connected) {
                        this.connect();
                    }
                    return MyURLStreamHandler.this.is;
                }
            };
        }
        
        static /* synthetic */ void access$0(final MyURLStreamHandler myURLStreamHandler, final InputStream is) {
            myURLStreamHandler.is = is;
        }
    }
    
    public class SubClassLoader extends ClassLoader
    {
        private PluginInfo pi;
        private StandardClassLoader parent;
        
        public SubClassLoader(final PluginInfo pi, final StandardClassLoader parent) {
            this.pi = pi;
            this.parent = parent;
        }
        
        @Override
		public Class<?> findClass(final String name) throws ClassNotFoundException {
            final Class<?> c = this.findLoadedClass(name);
            if (c != null) {
                return c;
            }
            final byte[] bytes = this.pi.findClass(name);
            if (bytes == null) {
                throw new ClassNotFoundException(name);
            }
            final Class<?> clazz = this.defineClass(name, bytes, 0, bytes.length);
            this.definePackage(name);
            StandardClassLoader.this.classes.add(name);
            return clazz;
        }
        
        private void definePackage(final String className) {
            final int pos = className.lastIndexOf(46);
            String packageName = null;
            if (pos != -1) {
                packageName = className.substring(0, pos);
            }
            Package pkg = null;
            if (packageName != null) {
                pkg = this.getPackage(packageName);
                if (pkg == null) {
                    try {
                        this.definePackage(packageName, null, null, null, null, null, null, null);
                    }
                    catch (IllegalArgumentException ex) {}
                    pkg = this.getPackage(packageName);
                }
            }
        }
        
        @Override
		public URL findResource(final String name) {
            return this.parent.findResource(name);
        }
        
        @Override
		public Enumeration<URL> findResources(final String name) throws IOException {
            return this.parent.findResources(name);
        }
        
        @Override
        public InputStream getResourceAsStream(final String name) {
            return this.parent.getResourceAsStream(name);
        }
        
        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException {
            Class<?> c = this.findLoadedClass(name);
            if (c != null) {
                return c;
            }
            try {
                c = super.loadClass(name);
            }
            catch (ClassNotFoundException ex) {}
            catch (Throwable t) {}
            if (c != null) {
                return c;
            }
            try {
                c = this.parent.loadClass(name);
            }
            catch (ClassNotFoundException ex2) {}
            catch (Throwable t2) {}
            if (c != null) {
                return c;
            }
            try {
                c = this.findClass(name);
            }
            catch (ClassNotFoundException ex3) {}
            catch (Throwable t3) {}
            if (c != null) {
                return c;
            }
            throw new ClassNotFoundException(name);
        }
    }
}
