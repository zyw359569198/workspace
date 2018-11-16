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

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自定义ClassLoader
 * @author wangys
 * @version 1.0.0.0 2011-4-15 下午03:34:29
 */
public class LibClassLoader extends URLClassLoader {	
	/**
	 * 构造函数
	 * @param loader tomcat默认的webappClassLoader
	 * @param parent tomcat默认的webappClassLoader的父加载器
	 * @param classpath 默认的扫描包前缀
	 * @throws Exception
	 */
	public LibClassLoader(URL[] urls, ClassLoader parent) throws Exception {
		super(urls, parent);
	}
}
