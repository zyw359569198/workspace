/*
 * $Header: Bootstrap.java
 * $Revision: 1.0.0.0 
 * $CreateDate: 2012-4-9
 * $ModifyDate: 2012-4-9 上午11:59:32
 * $Owner: 
 *
 * Copyright (c) 2011-2012 Shanghai Reign Co. Ltd.
 * All Right Reserved.
 */
package com.reign.framework.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * 启动器
 * @author   wangys
 * @version  1.0.0.0  2012-4-9 上午11:59:32
 */
public class Bootstrap {
	/** 启动器 */
	private static Bootstrap daemon = null;
	
	/** START_HOME_TOKEN */
	protected static final String START_HOME_TOKEN = "${start.home}";
	
	/** SDATA_PATH_TOKEN */
	protected static final String SDATA_PATH_TOKEN = "${sdata.path}";

	protected static final Integer IS_DIR = new Integer(0);
	protected static final Integer IS_JAR = new Integer(1);
	protected static final Integer IS_GLOB = new Integer(2);
	protected static final Integer IS_URL = new Integer(3);
	
	/** ClassLoader */
	private static ClassLoader standardClassLoader = null;
	/** Properties */
	private static Properties properties = null;
	/** 下载文件用的密码 */
	private static String password = null;
	
	/** log */
	private static Log log = null;
	
	/** 启动类 */
	private static Object startObj = null;
	
	public static void main(String[] args) {
		if (daemon == null) {
            daemon = new Bootstrap();
            try {
                daemon.init();
            } catch (Throwable t) {
                t.printStackTrace();
                return;
            }
        }
		
		try {
			daemon.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化
	 * $Date: 2012-4-9 下午12:28:12
	 */
	private void init() {
		// 设置启动路径
		setStartHome();
		
		// 初始化log4j.properties
		File file = new File(getFile("log4j.properties"));
		PropertyConfigurator.configureAndWatch(file.getAbsolutePath(), 3000);
		
		// log
		log = LogFactory.getLog("bootstrap");
		
		log.info("init start");
		try {
			// System.setProperty("java.io.tmpdir", getDirectory("temp", true));
			
			// 载入配置文件
			loadProperties();
			
			// 清理临时目录
			clearTempDir();
			
			// 初始化ClassLoader
			initClassLoaders();
			Thread.currentThread().setContextClassLoader(standardClassLoader);
			
			// 监听信号量
			// MySignalHandler.install("TERM");
			// MySignalHandler.install("INT");
			
			// 添加关闭Hook
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("init context error", e);
		}
		log.info("init end");
	}
	
	/**
	 * 载入配置文件
	 * $Date: 2012-4-9 下午01:20:21
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void loadProperties() throws FileNotFoundException, IOException {
		log.info("load properties " + getFile("app.properties"));
		properties = new Properties();
		properties.load(new FileInputStream(new File(getFile("app.properties"))));
//		getPassword(properties);
	}

	/**
	 * 获取密码
	 * @param 配置文件
	 * $Date: 2013-8-26 下午02:37:03
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void getPassword(Properties prop) throws Exception {
		String path = prop.getProperty("file.path"); 
		String key = prop.getProperty("file.key");
		String encrypt = prop.getProperty("file.encrypt");
		
		if (path != null && !"".equals(path.trim())) {
			Properties properties = new Properties();
			properties.load(new FileInputStream(new File(getFile(path))));
			password = properties.getProperty(key);
			
			if ("true".equalsIgnoreCase(encrypt)) {
				
			}
		}
	}

	/**
	 * 启动
	 * @throws Exception
	 * $Date: 2012-4-9 下午01:19:52
	 */
	private void start() throws Exception {
		// 载入ServletBootstrap
		Class<?> clazz = standardClassLoader.loadClass(properties.getProperty("startup.class"));
		startObj = clazz.newInstance();
		Method method = clazz.getMethod("startup", (Class[]) null);
		method.invoke(startObj, (Object[]) null);
	}

	/**
	 * 初始化ClassLoader
	 * $Date: 2012-4-9 下午12:36:33
	 * @throws Exception 
	 */
	private void initClassLoaders() throws Exception {
		standardClassLoader = createClassLoader("common", Bootstrap.class.getClassLoader());
		log.info("create common classLoader " + standardClassLoader);
	}
	
	/**
	 * 创建ClassLoader
	 * @param name
	 * @param parent
	 * @return
	 * @throws Exception
	 * $Date: 2012-4-9 下午12:58:07
	 */
	@SuppressWarnings("unused")
	private ClassLoader createClassLoader(String name, ClassLoader parent) throws Exception {
		String value = properties.getProperty(name + ".loader");
		if ((value == null) || (value.equals(""))) {
			return parent;
		}

		List<String> repositoryLocations = new ArrayList<String>(); // 资源路径
		List<Integer> repositoryTypes = new ArrayList<Integer>(); // 资源类型
		
		// 解析包路径
		int i;
		StringTokenizer tokenizer = new StringTokenizer(value, ",");
		while (tokenizer.hasMoreElements()) {
			String repository = tokenizer.nextToken();
			
			// Local repository
			boolean replace = false;
			String before = repository;
			while ((i = repository.indexOf(START_HOME_TOKEN)) >= 0) {
				replace = true;
				if (i > 0) {
					repository = repository.substring(0, i)
							+ getStartHome()
							+ repository.substring(i + START_HOME_TOKEN.length());
				} else {
					repository = getStartHome() + repository.substring(START_HOME_TOKEN.length());
				}
			}
			if (replace && log.isDebugEnabled()) {
				log.debug("Expanded " + before + " to " + repository);
			}

			// Check for a JAR URL repository
			try {
				URL url = new URL(repository);
				repositoryLocations.add(repository);
				repositoryTypes.add(IS_URL);
				continue;
			} catch (MalformedURLException e) {
				// Ignore
			}

			if (repository.endsWith("*.jar")) {
				repository = repository.substring(0, repository.length() - "*.jar".length());
				repositoryLocations.add(repository);
				repositoryTypes.add(IS_GLOB);
			} else if (repository.endsWith(".jar")) {
				repositoryLocations.add(repository);
				repositoryTypes.add(IS_JAR);
			} else {
				repositoryLocations.add(repository);
				repositoryTypes.add(IS_DIR);
			}
		}

		String[] locations = (String[]) repositoryLocations.toArray(new String[0]);
		Integer[] types = (Integer[]) repositoryTypes.toArray(new Integer[0]);

		StandardClassLoader classLoader = (StandardClassLoader) createClassLoader(locations, types, parent);
		
		String jarPath = properties.getProperty("jar.path");
		for (String str : jarPath.split(",")) {
			classLoader.parse(new URL(str));
		}
		String sdataPath = properties.getProperty("sdata.path");
		if (null != sdataPath) {
			classLoader.parseSdata(new URL(sdataPath));
			System.setProperty(SDATA_PATH_TOKEN, sdataPath);
		}

		return classLoader;

	}
	
	/**
     * Create and return a new class loader, based on the configuration
     * defaults and the specified directory paths:
     *
     * @param locations Array of strings containing class directories, jar files,
     *  jar directories or URLS that should be added to the repositories of
     *  the class loader. The type is given by the member of param types.
     * @param types Array of types for the members of param locations.
     *  Possible values are IS_DIR (class directory), IS_JAR (single jar file),
     *  IS_GLOB (directory of jar files) and IS_URL (URL).
     * @param parent Parent class loader for the new class loader, or
     *  <code>null</code> for the system class loader.
     *
     * @exception Exception if an error occurs constructing the class loader
     */
	public static ClassLoader createClassLoader(String locations[], Integer types[], ClassLoader parent) throws Exception {
		log.debug("Creating new class loader");
		// Construct the "class path" for this class loader
		List<URL> list = new ArrayList<URL>();
		if (locations != null && types != null && locations.length == types.length) {
			for (int i = 0; i < locations.length; i++) {
				String location = locations[i];
				if (types[i] == IS_URL) {
					URL url = new URL(location);
					log.debug("  Including URL " + url);
					list.add(url);
				} else if (types[i] == IS_DIR) {
					File directory = new File(location);
					directory = new File(directory.getCanonicalPath());
					if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) continue;
					URL url = directory.toURI().toURL();
					log.debug("  Including directory " + url);
					list.add(url);
				} else if (types[i] == IS_JAR) {
					File file = new File(location);
					file = new File(file.getCanonicalPath());
					if (!file.exists() || !file.canRead()) {
						continue;
					}
					URL url = file.toURI().toURL();
					log.debug("  Including jar file " + url);
					list.add(url);
				} else if (types[i] == IS_GLOB) {
					File directory = new File(location);
					if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
						continue;
					}
					log.debug("  Including directory glob " + directory.getAbsolutePath());
					String filenames[] = directory.list();
					for (int j = 0; j < filenames.length; j++) {
						String filename = filenames[j].toLowerCase();
						if (!filename.endsWith(".jar")) continue;
						File file = new File(directory, filenames[j]);
						file = new File(file.getCanonicalPath());
						if (!file.exists() || !file.canRead()) continue;
						log.debug("    Including glob jar file " + file.getAbsolutePath());
						URL url = file.toURI().toURL();
						list.add(url);
					}
				}
			}
		}

		// Construct the class loader itself
		URL[] array = (URL[]) list.toArray(new URL[list.size()]);
		if (log.isDebugEnabled()) {
			for (int i = 0; i < array.length; i++) {
				log.debug("  location " + i + " is " + array[i]);
			}
		}
		
		LibClassLoader libClassLoader = new LibClassLoader(array, parent);
		StandardClassLoader classLoader = null;
		classLoader = new StandardClassLoader(new URL[0], libClassLoader, properties.getProperty("scan.path"));
		return (classLoader);

	}

	/**
	 * 设置启动路径
	 * $Date: 2012-4-9 下午12:29:11
	 */
	private void setStartHome() {
		if (System.getProperty("start.home") != null) {
			return;
		}
		
  		File bootstrapJar = new File(System.getProperty("user.dir"), "bootstrap.jar");
		if (bootstrapJar.exists()) {
			try {
				System.setProperty("start.home", (new File(System.getProperty("user.dir"))).getCanonicalPath());
			} catch (Exception e) {
				// Ignore
				System.setProperty("start.home", System.getProperty("user.dir"));
			}
		} else {
			System.setProperty("start.home", System.getProperty("user.dir"));
		}
	}
	
	/**
	 * 获得启动路径
	 * @return
	 * $Date: 2012-4-9 下午01:03:18
	 */
	private static String getStartHome() {
		return System.getProperty("start.home", System.getProperty("user.dir"));
	}
	
	/**
	 * 获得文件
	 * @param create 是否创建
	 * @return
	 * $Date: 2012-4-9 下午01:03:18
	 */
	private static String getFile(String fileName) {
		String path = getStartHome() + File.separator + "apps" + File.separator + fileName;
		return path;
	}
	
	/**
	 * 获得目录
	 * @param create 是否创建
	 * @return
	 * $Date: 2012-4-9 下午01:03:18
	 */
	private static String getDirectory(String name, boolean create) {
		String path = getStartHome() + File.separator + name;
		if (create) {
			File file = new File(path);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.mkdir();
			}
		}
		return path;
	}
	
	/**
	 * App退出
	 * 
	 * $Date: 2012-4-10 下午03:24:17
	 */
	private static void appExit() {
		clearTempDir();
	}
	
	/**
	 * 清理临时目录
	 * 
	 * $Date: 2012-4-11 下午03:43:58
	 */
	private static void clearTempDir() {
		// 删除临时目录文件
		File file = new File(getDirectory("temp", false));
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
	}
	
	/**
	 * 关闭Hook
	 * 
	 * @author   wangys
	 * @version  1.0.0.0  2012-4-10 下午01:41:25
	 */
	protected class ShutdownHook extends Thread {
		public void run() {
			appExit();
			log.info("shut down compelete");
		}
	}
	
	/**
	 * 信号量处理器
	 * 
	 * @author   wangys
	 * @version  1.0.0.0  2012-4-10 下午03:23:52
	 */
	protected static class MySignalHandler implements SignalHandler {
		// SignalHandler
		private SignalHandler oldHandler;
		
		/**
		 * @see sun.misc.SignalHandler#handle(sun.misc.Signal)
		 */
		@Override
		public void handle(Signal signal) {
			try {
				log.info("handle signal " + signal.toString() + ":" + signal.getNumber());
				signalAction(signal);
				
				if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
					oldHandler.handle(signal);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * @param signal
		 * $Date: 2012-4-10 下午02:52:17
		 */
		private void signalAction(Signal signal) {
			appExit();
			log.info("shut down compelete");
		}
		
		/**
		 * 注册信号量
		 * @param signalName
		 * @return
		 * $Date: 2012-4-10 下午02:54:57
		 */
		public static SignalHandler install(String signalName) {
			Signal signal = new Signal(signalName);
			
			MySignalHandler handler = new MySignalHandler();
			handler.oldHandler = Signal.handle(signal, handler);
			return handler;
		}

	}
	
}
