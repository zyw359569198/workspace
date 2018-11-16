/*
 * $Header: MyClassLoader.java
 * $Revision: 1.0.0.0
 * $CreateDate: 2011-4-15
 * $ModifyDate: 2011-4-15 下午03:34:29
 * $Owner:
 * Copyright (c) 2011-2012 Shanghai Reign Co. Ltd.
 * All Right Reserved.
 */
package com.reign.framework.startup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.lang.StringUtils;

import com.sun.org.apache.xml.internal.security.utils.Base64;


/**
 * 自定义ClassLoader
 * @author wangys
 * @version 1.0.0.0 2011-4-15 下午03:34:29
 */
public class StandardClassLoader extends URLClassLoader {
	/** class到字节的映射 */
	private Map<String, byte[]> clazzByteMap = new LinkedHashMap<String, byte[]>();
	
	/** sdata的映射 */
	private Map<String, byte[]> sdataByteMap = new LinkedHashMap<String, byte[]>();
	
	/** 文件的映射 */
	private Map<String, File> fileMap = new LinkedHashMap<String, File>();
	
	/** URL资源的映射 */
	private Map<String, URL> urlMap = new LinkedHashMap<String, URL>();
	
	/** classpath */
	private String classpath;
	
//	/** jar路径 */
//	private URL[] urls;
//	
//	/** 父ClassLoader */
//	private ClassLoader parent;
	
//	/** 下载URL */
//	private Vector<URL> downloads;
	
	/** 插件相关 */
	/** 插件Map */
	private Map<String, PluginInfo> pluginMap = new HashMap<String, PluginInfo>();
	
	/** 插件加载器Map */
	private Map<String, SubClassLoader> loaderMap = new HashMap<String, SubClassLoader>();
	
	/** 插件中的classes */
	private Set<String> classes = new HashSet<String>();
	
	/**
	 * 构造函数
	 * @param loader tomcat默认的webappClassLoader
	 * @param parent tomcat默认的webappClassLoader的父加载器
	 * @param classpath 默认的扫描包前缀
	 * @throws Exception
	 */
	public StandardClassLoader(URL[] urls, ClassLoader parent, String classpath) throws Exception {
		super(urls, parent);
		this.classpath = classpath;
//		this.urls = urls;
//		this.parent = parent;
//		this.downloads = new Vector<URL>();
	}
	
	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {		
		// 先查找父类
		Class<?> clazz = null;
		
		try {
			clazz = super.findClass(name);
		} catch (Throwable t) {
			// t.printStackTrace();
		}
		
		if (null != clazz) {
			return clazz;
		}
		
		// 查找本地缓存
		byte[] bytes = clazzByteMap.get(name);
		if (null != bytes) {
			// 定义Class
			clazz = defineClass(name, bytes, 0, bytes.length);
			
			// 定义Package
			definePackage(name);
			
			return clazz;
		}
		
		// 插件中查找类
		try {
			clazz = _findClass(name);
		} catch (ClassNotFoundException e) {
		} catch (Throwable t) {
			// t.printStackTrace();
		}
		
		if (null != clazz) {
			return clazz;
		}
		
        throw new ClassNotFoundException(name);
	}
	
	/**
	 * 查找类
	 * @param name
	 * @return
	 * $Date: 2012-10-9 下午03:00:35
	 * @throws ClassNotFoundException 
	 */
	private Class<?> _findClass(String name) throws ClassNotFoundException {
		// 查找本地缓存
		for (Entry<String, SubClassLoader> entry : loaderMap.entrySet()) {
			// 查找类
			return entry.getValue().findClass(name);
		}
		
		return null;
	}
	
	/**
	 * 查找类
	 * @param name
	 * @return
	 * $Date: 2012-10-9 下午03:00:35
	 * @throws ClassNotFoundException 
	 */
	private byte[] _findClassBytes(String name) {
		// 查找本地缓存
		for (Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
			// 查找类
			return entry.getValue().findClass(name);
		}
		
		return null;
	}
	
	/**
	 * @param checkClass 
	 * 载入插件
	 * @param name
	 * @param url
	 * $Date: 2012-10-9 下午02:52:43
	 * @throws Exception 
	 * @throws  
	 */
	public synchronized PluginInfo loadPlugin(String name, String url) throws Exception {
		if (pluginMap.containsKey(name)) {
			throw new RuntimeException(name + " plugin is already loaded");
		}
		
		PluginInfo pluginInfo = new PluginInfo(name);
		parse(pluginInfo, new URL(url));
		pluginInfo.init();
		
		pluginMap.put(name, pluginInfo);
		loaderMap.put(name, new SubClassLoader(pluginInfo, this));
		
		return pluginInfo;
	}
	
	/**
	 * 解析SdataURL
	 * @param url
	 * $Date: 2012-12-13 下午05:53:12
	 */
	public void parseSdata(URL url) throws Exception {
		JarInputStream jar = new JarInputStream(getRemoteResource(url));
		JarEntry entry = null;
		while ((entry = jar.getNextJarEntry()) != null) {
			if (entry.getName().toLowerCase().endsWith("sdata.xml")) {
				sdataByteMap.put("sdata", getData(jar));
			} else if (entry.getName().toLowerCase().endsWith("version.txt")) {
				sdataByteMap.put("sdataVersion", getData(jar));
			}
		}
	}

	/**
	 * 解析指定URL
	 * @param url
	 * @throws Exception
	 * $Date: 2011-4-23 下午07:53:24
	 */
	public void parse(URL url) throws Exception {	
		JarInputStream jar = new JarInputStream(getRemoteResource(url));
		JarEntry entry = null;
		while ((entry = jar.getNextJarEntry()) != null) {
			if (entry.getName().toLowerCase().endsWith(".class")) {
				String className = getClassName(entry.getName());
				clazzByteMap.put(className, getData(jar));
				
				if (className.indexOf("$") == -1) {
					// 自定义新的URL，用于资源检索
					urlMap.put(className, new URL("class", null, -1, className, new MyURLStreamHandler()));
				}
			} else if (!entry.isDirectory()) {
				// 加载文件到本地系统
				String name = entry.getName();
				if (name.endsWith(".java")) {
					continue;
				} else if (name.endsWith("changelog.txt")) {
					continue;
				} else if (name.endsWith(".js")) {
					continue;
				}
				
				File file = null;
				file = File.createTempFile(name.replace("/", "."), "." + StringUtils.substringAfterLast(name, "."));
				getFileData(file, jar);
				fileMap.put(entry.getName(), file);
			}
		}
	}
	
	/**
	 * 获取远程资源
	 * @param url URL地址
	 * @return
	 * $Date: 2012-12-13 下午05:54:51
	 * @throws IOException 
	 */
	private InputStream getRemoteResource(URL url) throws Exception {
		int times = 0;
		Exception exception = null;
		while (times < 3) {
			try {
				// 从URL中获取流
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Authorization", new String("Basic " + Base64.encode(new String("rzcore:rzcore2012!@#$").getBytes())));
				conn.connect();
				
				return conn.getInputStream();
			} catch (Exception t) {
				exception = t;
				times++;
				// 休息1~2s钟之后重试
				Thread.sleep((long) (1000 + Math.random() * 1000));
			}
		}
		throw exception;
	}
	
	/**
	 * 解析指定URL
	 * @param pluginInfo 
	 * @param url
	 * @throws Exception
	 * $Date: 2011-4-23 下午07:53:24
	 */
	public void parse(PluginInfo pluginInfo, URL url) throws Exception {
		// 从URL中获取流
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization",
								new String("Basic " + Base64.encode(new String("rzcore:rzcore2012!@#$").getBytes())));
		conn.connect();

		JarInputStream jar = new JarInputStream(conn.getInputStream());
		JarEntry entry = null;
		while ((entry = jar.getNextJarEntry()) != null) {
			if (entry.getName().toLowerCase().endsWith(".class")) {
				String className = getClassName(entry.getName());
				pluginInfo.clazzByteMap.put(className, getData(jar));

				if (className.indexOf("$") == -1) {
					// 自定义新的URL，用于资源检索
					pluginInfo.urlMap.put(className, new URL("class", null, -1, className, new MyURLStreamHandler()));
				}
			} else if (!entry.isDirectory()) {
				// 加载文件到本地系统
				String name = entry.getName();
				if (name.endsWith(".java")) {
					continue;
				} else if (name.endsWith("changelog.txt")) {
					continue;
				} else if (name.endsWith(".js")) {
					continue;
				}

				File file = null;
				file = File.createTempFile(name.replace("/", "."), "." + StringUtils.substringAfterLast(name, "."));
				getFileData(file, jar);
				pluginInfo.fileMap.put(entry.getName(), file);
			}
		}
	}
	
	
	/**
	 * @see java.lang.ClassLoader#findResource(java.lang.String)
	 */
	@Override
	public URL findResource(String name) {
		// 先查找本地资源
		File file = fileMap.get(name);
		if (null != file) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		// 从插件资源中查找
		file = _findResource(name);
		if (null != file) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		return super.findResource(name);
	}
	
	/**
	 * 查找类
	 * @param name
	 * @return
	 * $Date: 2012-10-9 下午03:00:35
	 */
	private File _findResource(String name) {
		// 查找本地缓存
		for (Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
			// 查找类
			File file = entry.getValue().findResource(name);
			return file;
		}
		
		return null;
	}
	
	/**
	 * @see java.lang.ClassLoader#findResources(java.lang.String)
	 */
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		// 从Tomcat默认加载器加载资源
		Enumeration<URL> urls = null;
		
		// 从自定义URL中查找
		String className = name.replace("/", ".");
		if (className.endsWith(".class")) {
			className = getClassName(className);
		}
		if (className.startsWith(classpath)) {
			Vector<URL> vector = new Vector<URL>();
			for (Entry<String, URL> entry : urlMap.entrySet()) {
				if (entry.getKey().startsWith(className)) {
					vector.add(entry.getValue());
				}
			}
			if (vector.size() > 0) {
				urls = vector.elements();
			}
		}
		
		// 从插件中查找
		if (urls == null || !urls.hasMoreElements()) {
			urls = _findResources(className);
		}
		
		// 从本地缓存文件加载
		if (urls == null || !urls.hasMoreElements()) {
			File file = fileMap.get(name);
			if (null != file) {
				try {
					Vector<URL> vector = new Vector<URL>();
					vector.add(file.toURI().toURL());
					urls = vector.elements();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		// 从插件中查找
		if (urls == null || !urls.hasMoreElements()) {
			File file = _findResource(name);
			if (null != file) {
				try {
					Vector<URL> vector = new Vector<URL>();
					vector.add(file.toURI().toURL());
					urls = vector.elements();
				} catch (MalformedURLException e) {
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
	
	/**
	 * 查找类
	 * @param name
	 * @return
	 * $Date: 2012-10-9 下午03:00:35
	 */
	private Enumeration<URL> _findResources(String className) {
		Vector<URL> vector = new Vector<URL>();
		// 查找本地缓存
		for (Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
			// 查找类
			Vector<URL> temp = entry.getValue().findResources(className);
			if (null != temp) {
				vector.addAll(temp);
			}
		}
		
		if (vector.size() > 0) {
			return vector.elements();
		}
		return null;
	}
	
	/**
	 * 自定义URL解析Handler
	 * 
	 * @author   wangys
	 * @version  1.0.0.0  2012-3-30 下午05:24:52
	 */
	private class MyURLStreamHandler extends URLStreamHandler {
		private InputStream is;
		
		/**
		 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
		 */
		@Override
		protected URLConnection openConnection(final URL url) throws IOException {
			return new URLConnection(url) {
				/**
				 * @see java.net.URLConnection#connect()
				 */
				@Override
				public void connect() throws IOException {
					if (!connected) {
						byte[] buff = (url.getFile().substring(url.getFile().lastIndexOf(".") + 1) + ".class").getBytes();
						is = new ByteArrayInputStream(buff);
					}
				}
				
				/**
				 * @see java.net.URLConnection#getInputStream()
				 */
				@Override
				public InputStream getInputStream() throws IOException {
					if (!connected) {
						connect();
					}
					return is;
				}				
			};
		}
		
	}

	/**
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String name) {	
		// 对于查找Class文件，先查看本地缓存
		if (name.endsWith(".class")) {
			String clazzName = getClassName(name);
			byte[] bytes = clazzByteMap.get(clazzName);
			if (null != bytes) {
				return new ByteArrayInputStream(bytes);
			}
			
			// 从插件中查找
			bytes = _findClassBytes(clazzName);
			if (null != bytes) {
				return new ByteArrayInputStream(bytes);
			}
		} else if (name.equals("sdata")) {
			byte[] bytes = sdataByteMap.get("sdata");
			if (null != bytes) {
				return new ByteArrayInputStream(bytes);
			}
		} else if (name.equals("sdataVersion")) {
			byte[] bytes = sdataByteMap.get("sdataVersion");
			if (null != bytes) {
				return new ByteArrayInputStream(bytes);
			}
		}
		
		return super.getResourceAsStream(name);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// 查找已经加载过的Class
		Class<?> c = null;
		if (!classes.contains(name)) {
			c = findLoadedClass(name);
		}
		if (c == null) {
			if (!classes.contains(name)) {
				try {
					// 通过父亲加载器加载
					c = super.loadClass(name);
				} catch (ClassNotFoundException e) {
				} catch (Throwable e) {
				}
			}

			// 如果找到返回
			if (null != c) {
				return c;
			}
			
			// 先查找本地类
			try {
				c = findClass(name);
			} catch (ClassNotFoundException e) {
			} catch (Throwable e) {
			}
			
			// 如果找到返回
			if (null != c) {
				return c;
			}

			// 都找不到，返回ClassNotFoundException
			throw new ClassNotFoundException(name);
			
		} else {
			return c;
		}
	}
	
	
//	@Override
//	public StandardClassLoader clone() {
//		try {
//			StandardClassLoader loader = new StandardClassLoader(this.urls, this.parent, this.classpath);
//			// 拷贝Map
//			for (Entry<String, byte[]> entry : this.clazzByteMap.entrySet()) {
//				loader.clazzByteMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Entry<String, File> entry : this.fileMap.entrySet()) {
//				loader.fileMap.put(entry.getKey(), entry.getValue());
//			}
//			for (Entry<String, URL> entry : this.urlMap.entrySet()) {
//				loader.urlMap.put(entry.getKey(), entry.getValue());
//			}
//			loader.downloads = this.downloads;
//			
//			return loader;
//		} catch (CloneNotSupportedException e) {
//			// Ignore
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	/**
//	 * 重置
//	 * $Date: 2012-9-5 下午03:54:29
//	 * @throws Exception 
//	 */
//	public void reset() throws Exception {
//		Vector<URL> copy = new Vector<URL>(downloads);
//		this.clazzByteMap.clear();
//		this.fileMap.clear();
//		this.urlMap.clear();
//		this.downloads.clear();
//		for (URL url : copy) {
//			parse(url);
//		}
//	}

	/**
	 * 获取ClassName
	 * @param name
	 * @return
	 *         $Date: 2011-4-23 下午07:52:36
	 */
	private String getClassName(String name) {
		return name.substring(0, name.length() - ".class".length()).replace('/', '.');
	}
	
	/**
	 * 从Jar中获取字节流
	 * @param jar
	 * @return
	 *         $Date: 2011-4-15 上午11:09:19
	 * @throws IOException
	 */
	private byte[] getData(JarInputStream jar) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int size = -1;
		byte[] buff = new byte[1024];
		while (jar.available() > 0) {
			size = jar.read(buff);
			if (size > 0) {
				baos.write(buff, 0, size);
			}
		}
		return baos.toByteArray();
	}

	/**
	 * 获得InputStream的一个拷贝
	 * @param is
	 * @return
	 *         $Date: 2011-4-23 下午07:35:56
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private InputStream getCopyInputStream(InputStream is) throws IOException {
		is.reset();
		return getInputStreamFromURL(is);
	}

	/**
	 * 获取流
	 * 
	 * @param inputStream
	 * @return
	 *         $Date: 2011-4-23 下午07:14:09
	 * @throws IOException
	 */
	private ByteArrayInputStream getInputStreamFromURL(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buff = new byte[2048];
		int len = -1;
		while ((len = inputStream.read(buff)) != -1) {
			if (len > 0) {
				bos.write(buff, 0, len);
			}
		}
		return new ByteArrayInputStream(bos.toByteArray());
	}
	
	/**
	 * 定义Package，这个很重要，JAXB中就必须要定义这个
	 * @param className 类名
	 * $Date: 2012-3-30 下午05:09:06
	 */
	private void definePackage(String className) {
		int pos = className.lastIndexOf('.');
		String packageName = null;
		if (pos != -1) {
			packageName = className.substring(0, pos);
		}

		Package pkg = null;
		if (packageName != null) {
			pkg = getPackage(packageName);
			// Define the package (if null)
			if (pkg == null) {
				try {
					definePackage(packageName, null, null, null, null, null, null, null);
				} catch (IllegalArgumentException e) {
					// Ignore: normal error due to dual definition of package
				}
				pkg = getPackage(packageName);
			}
		}
	}
	
	/**
	 * 从远程获取文件
	 * @param file
	 * @param jar
	 * $Date: 2012-3-23 下午01:13:36
	 */
	private void getFileData(File file, JarInputStream jar) {
		InputStream is = jar;
		FileOutputStream fos = null;
		try {
			byte[] buff = new byte[1024];
			fos = new FileOutputStream(file);
			while (true) {
				int length = is.read(buff, 0, buff.length);
				if (-1 != length) {
					fos.write(buff, 0, length);
				} else {
					break;
				}
			}
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 子插件加载器
	 * 
	 * @author   wangys
	 * @version  1.0.0.0  2012-10-12 上午11:38:10
	 */
	public class SubClassLoader extends ClassLoader {
		// 插件信息
		private PluginInfo pi;
		
		// 父加载器
		private StandardClassLoader parent;
		
		/**
		 * 构造函数
		 * @param pi
		 */
		public SubClassLoader(PluginInfo pi, StandardClassLoader parent) {
			this.pi = pi;
			this.parent = parent;
		}
		
		/**
		 * @see java.lang.ClassLoader#findClass(java.lang.String)
		 */
		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			Class<?> c = findLoadedClass(name);
			if (c != null) {
				return c;
			}
			
			// 查找类
			byte[] bytes = pi.findClass(name);
			if (null == bytes) {
				throw new ClassNotFoundException(name);
			}

			// 定义Class
			Class<?> clazz = defineClass(name, bytes, 0, bytes.length);

			// 定义Package
			definePackage(name);
			
			// 特殊缓存
			classes.add(name);

			return clazz;
		}
		
		/**
		 * 定义Package，这个很重要，JAXB中就必须要定义这个
		 * 
		 * @param className 类名
		 * $Date: 2012-3-30 下午05:09:06
		 */
		private void definePackage(String className) {
			int pos = className.lastIndexOf('.');
			String packageName = null;
			if (pos != -1) {
				packageName = className.substring(0, pos);
			}

			Package pkg = null;
			if (packageName != null) {
				pkg = getPackage(packageName);
				// Define the package (if null)
				if (pkg == null) {
					try {
						definePackage(packageName, null, null, null, null, null, null, null);
					} catch (IllegalArgumentException e) {
						// Ignore: normal error due to dual definition of package
					}
					pkg = getPackage(packageName);
				}
			}
		}
		
		/**
		 * @see java.lang.ClassLoader#findResource(java.lang.String)
		 */
		@Override
		public URL findResource(String name) {
			return parent.findResource(name);
		}
		
		/**
		 * @see java.lang.ClassLoader#findResources(java.lang.String)
		 */
		@Override
		public Enumeration<URL> findResources(String name) throws IOException {
			return parent.findResources(name);
		}
		
		/**
		 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
		 */
		@Override
		public InputStream getResourceAsStream(String name) {
			return parent.getResourceAsStream(name);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			// 查找已经加载过的Class
			Class<?> c = findLoadedClass(name);
			if (c == null) {
				try {
					// 通过父亲加载器加载
					c = super.loadClass(name);
				} catch (ClassNotFoundException e) {
				} catch (Throwable e) {
				}

				// 如果找到返回
				if (null != c) {
					return c;
				}
				
				// 查找父亲的类
				try {
					// 通过父亲加载器加载
					c = parent.loadClass(name);
				} catch (ClassNotFoundException e) {
				} catch (Throwable e) {
				}
				// 如果找到返回
				if (null != c) {
					return c;
				}
				
				// 先查找本地类
				try {
					c = findClass(name);
				} catch (ClassNotFoundException e) {
				} catch (Throwable e) {
				}

				// 如果找到返回
				if (null != c) {
					return c;
				}

				// 都找不到，返回ClassNotFoundException
				throw new ClassNotFoundException(name);

			} else {
				return c;
			}
		}
	}
	
}
