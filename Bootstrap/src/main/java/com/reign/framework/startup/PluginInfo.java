/*
 * $Header: PluginInfo.java
 * $Revision: 1.0.0.0 
 * $CreateDate: 2012-10-9
 * $ModifyDate: 2012-10-9 下午03:11:22
 * $Owner: 
 *
 * Copyright (c) 2011-2012 Shanghai Reign Co. Ltd.
 * All Right Reserved.
 */
package com.reign.framework.startup;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;



/**
 * 插件信息
 * 
 * @author   wangys
 * @version  1.0.0.0  2012-10-9 下午02:51:27
 */
public class PluginInfo {
	/** class到字节的映射 */
	public Map<String, byte[]> clazzByteMap = new LinkedHashMap<String, byte[]>();

	/** 文件的映射 */
	public Map<String, File> fileMap = new LinkedHashMap<String, File>();

	/** URL资源的映射 */
	public Map<String, URL> urlMap = new LinkedHashMap<String, URL>();

	/** scanPackage */
	public String scanPackage;
	
	/** 版本号 */
	public String version;
	
	/** 插件名称 */
	public String name;
	
	/** 插件检查Class */
	public String checkClass;
	
	/** urls */
	public Vector<URL> urls;
	
	/** commands */
	public Set<String> commands;
	
	/** components */
	public Set<String> components;
	
	/** sqls */
	public Set<String> sqls;
	
	/** sqlFile */
	public String sqlFile;
	
	/**
	 * 构造函数
	 * @param name
	 * @param checkClass 
	 */
	public PluginInfo(String name) {
		this.name = name;
	}
	
	/**
	 * 查找Class
	 * @param name
	 * @return
	 * $Date: 2012-10-9 下午02:58:03
	 */
	public byte[] findClass(String name) {
		return clazzByteMap.get(name);
	}
	
	/**
	 * 查找资源
	 * @param name
	 * @return
	 * $Date: 2012-10-9 下午03:02:16
	 */
	public File findResource(String name) {
		return fileMap.get(name);
	}
	
	/**
	 * 查找URL
	 * @param className
	 * @return
	 * $Date: 2012-10-9 下午03:05:58
	 */
	public Vector<URL> findResources(String className) {
		Vector<URL> vector = null;
		if (className.startsWith(scanPackage)) {
			for (Entry<String, URL> entry : urlMap.entrySet()) {
				if (entry.getKey().startsWith(className)) {
					if (null == vector) {
						vector = new Vector<URL>();
					}
					vector.add(entry.getValue());
				}
			}
		}
		return vector;
	}
	
	/**
	 * 插件初始化
	 * $Date: 2012-10-9 下午03:12:34
	 * @throws Exception 
	 */
	public void init() throws Exception {
		// 载入配置文件
		File file = findResource("plugin.xml");
		if (file == null) {
			throw new RuntimeException(name + " init failed, plugin.xml not found");
		}
		
		XML xml = new XML(new FileInputStream(file));

		this.version = xml.get("version").getValue();
		this.scanPackage = xml.get("scanPackage").getValue();
		this.urls = findResources(this.scanPackage);
		this.sqlFile = xml.get("sqlResources").getValue();
		this.checkClass = xml.get("checkClass").getValue();
	}
	
	/**
	 * 获取版本号
	 * @return
	 * $Date: 2012-10-9 下午03:38:33
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * 获取扫描的scanPackage
	 * @return
	 * $Date: 2012-10-9 下午04:02:13
	 */
	public Enumeration<URL> getScanPackage() {
		return this.urls.elements();
	}
	
	/**
	 * 获得Commands
	 * @return
	 * $Date: 2012-10-9 下午04:03:03
	 */
	public Set<String> getCommands() {
		return this.commands;
	}
	
	/**
	 * 添加Command
	 * @param command
	 * $Date: 2012-10-9 下午04:03:40
	 */
	public void addCommands(String command) {
		if (null == commands) {
			commands = new HashSet<String>();
		}
		commands.add(command);
	}

	/**
	 * 添加组件
	 * @param component
	 * $Date: 2012-10-12 下午04:06:52
	 */
	public void addSpringComponents(String component) {
		if (null == components) {
			components = new HashSet<String>();
		}
		components.add(component);
	}

	/**
	 * 添加SQL
	 * @param sqlId
	 * $Date: 2012-10-12 下午04:11:18
	 */
	public void addSqls(String sqlId) {
		if (null == sqls) {
			sqls = new HashSet<String>();
		}
		sqls.add(sqlId);
	}

	/**
	 * 
	 * $Date: 2012-10-12 下午04:14:04
	 */
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
