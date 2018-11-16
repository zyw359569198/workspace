package com.reign.framework.common;

import org.apache.ibatis.io.*;
import org.apache.ibatis.logging.*;
import java.util.jar.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class MyBatisVFS extends VFS
{
    private static final Log log;
    private static final byte[] JAR_MAGIC;
    
    static {
        log = LogFactory.getLog(ResolverUtil.class);
        JAR_MAGIC = new byte[] { 80, 75, 3, 4 };
    }
    
    @Override
	public boolean isValid() {
        return true;
    }
    
    @Override
	public List<String> list(final URL url, final String path) throws IOException {
        InputStream is = null;
        try {
            List<String> resources = new ArrayList<String>();
            final URL jarUrl = this.findJarForResource(url);
            if (jarUrl != null) {
                is = jarUrl.openStream();
                MyBatisVFS.log.debug("Listing " + url);
                resources = this.listResources(new JarInputStream(is), path);
            }
            else {
                List<String> children = new ArrayList<String>();
                try {
                    if (this.isJar(url)) {
                        is = url.openStream();
                        final JarInputStream jarInput = new JarInputStream(is);
                        MyBatisVFS.log.debug("Listing " + url);
                        JarEntry entry;
                        while ((entry = jarInput.getNextJarEntry()) != null) {
                            MyBatisVFS.log.debug("Jar entry: " + entry.getName());
                            children.add(entry.getName());
                        }
                    }
                    else {
                        is = url.openStream();
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        final List<String> lines = new ArrayList<String>();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            MyBatisVFS.log.debug("Reader entry: " + line);
                            lines.add(line);
                            if (getResources(String.valueOf(path) + "/" + line).isEmpty()) {
                                lines.clear();
                                break;
                            }
                        }
                        if (!lines.isEmpty()) {
                            MyBatisVFS.log.debug("Listing " + url);
                            children.addAll(lines);
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    if (!"file".equals(url.getProtocol())) {
                        throw e;
                    }
                    final File file = new File(url.getFile());
                    MyBatisVFS.log.debug("Listing directory " + file.getAbsolutePath());
                    if (file.isDirectory()) {
                        MyBatisVFS.log.debug("Listing " + url);
                        children = Arrays.asList(file.list());
                    }
                }
                String prefix = url.toExternalForm();
                if (!prefix.endsWith("/")) {
                    prefix = String.valueOf(prefix) + "/";
                }
                for (final String child : children) {
                    final String resourcePath = String.valueOf(path) + "/" + child;
                    resources.add(resourcePath);
                    if (!"class".equalsIgnoreCase(url.getProtocol())) {
                        try {
                            final URL childUrl = new URL(String.valueOf(prefix) + child);
                            resources.addAll(this.list(childUrl, resourcePath));
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            return resources;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    protected List<String> listResources(final JarInputStream jar, String path) throws IOException {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = String.valueOf(path) + "/";
        }
        final List<String> resources = new ArrayList<String>();
        JarEntry entry;
        while ((entry = jar.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {
                String name = entry.getName();
                if (!name.startsWith("/")) {
                    name = "/" + name;
                }
                if (!name.startsWith(path)) {
                    continue;
                }
                MyBatisVFS.log.debug("Found resource: " + name);
                resources.add(name.substring(1));
            }
        }
        return resources;
    }
    
    protected URL findJarForResource(URL url) throws MalformedURLException {
        MyBatisVFS.log.debug("Find JAR URL: " + url);
        try {
            while (true) {
                url = new URL(url.getFile());
                MyBatisVFS.log.debug("Inner URL: " + url);
            }
        }
        catch (MalformedURLException ex) {
            final StringBuilder jarUrl = new StringBuilder(url.toExternalForm());
            final int index = jarUrl.lastIndexOf(".jar");
            if (index >= 0) {
                jarUrl.setLength(index + 4);
                MyBatisVFS.log.debug("Extracted JAR URL: " + jarUrl);
                try {
                    URL testUrl = new URL(jarUrl.toString());
                    if (this.isJar(testUrl)) {
                        return testUrl;
                    }
                    MyBatisVFS.log.debug("Not a JAR: " + jarUrl);
                    jarUrl.replace(0, jarUrl.length(), testUrl.getFile());
                    File file = new File(jarUrl.toString());
                    if (!file.exists()) {
                        try {
                            file = new File(URLEncoder.encode(jarUrl.toString(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException e) {
                            throw new RuntimeException("Unsupported encoding?  UTF-8?  That's unpossible.");
                        }
                    }
                    if (file.exists()) {
                        MyBatisVFS.log.debug("Trying real file: " + file.getAbsolutePath());
                        testUrl = file.toURI().toURL();
                        if (this.isJar(testUrl)) {
                            return testUrl;
                        }
                    }
                }
                catch (MalformedURLException e2) {
                    MyBatisVFS.log.warn("Invalid JAR URL: " + jarUrl);
                }
                MyBatisVFS.log.debug("Not a JAR: " + jarUrl);
                return null;
            }
            MyBatisVFS.log.debug("Not a JAR: " + jarUrl);
            return null;
        }
    }
    
    protected String getPackagePath(final String packageName) {
        return (packageName == null) ? null : packageName.replace('.', '/');
    }
    
    protected boolean isJar(final URL url) {
        return this.isJar(url, new byte[MyBatisVFS.JAR_MAGIC.length]);
    }
    
    protected boolean isJar(final URL url, final byte[] buffer) {
        InputStream is = null;
        try {
            is = url.openStream();
            is.read(buffer, 0, MyBatisVFS.JAR_MAGIC.length);
            if (Arrays.equals(buffer, MyBatisVFS.JAR_MAGIC)) {
                MyBatisVFS.log.debug("Found JAR: " + url);
                return true;
            }
        }
        catch (Exception ex) {}
        finally {
            try {
                is.close();
            }
            catch (Exception ex2) {}
        }
        try {
            is.close();
        }
        catch (Exception ex3) {}
        return false;
    }
}
