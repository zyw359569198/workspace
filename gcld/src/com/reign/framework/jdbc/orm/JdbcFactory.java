package com.reign.framework.jdbc.orm;

import org.springframework.beans.factory.*;
import com.reign.framework.jdbc.orm.cache.*;
import javax.sql.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.plugin.*;
import com.reign.framework.startup.*;
import java.net.*;
import org.apache.commons.lang.*;
import com.reign.framework.common.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.jdbc.orm.session.*;
import com.reign.framework.jdbc.orm.transaction.*;
import java.util.*;
import com.reign.util.*;

public class JdbcFactory implements InitializingBean
{
    private String scanPackage;
    private Map<Class<?>, JdbcEntity> entityMap;
    private Map<String, CacheConfig> cacheConfigMap;
    private CacheFactory cacheFactory;
    private BaseJdbcExtractor baseJdbcExtractor;
    private DataSource dataSource;
    private List<TransactionListener> listeners;
    private String cacheConfigFile;
    
    public JdbcFactory() {
        this.entityMap = new HashMap<Class<?>, JdbcEntity>();
        this.cacheConfigMap = new HashMap<String, CacheConfig>();
    }
    
    public void setScanPackage(final String scanPackage) {
        this.scanPackage = scanPackage;
    }
    
    public void setCacheFactory(final CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }
    
    public BaseJdbcExtractor getBaseJdbcExtractor() {
        return this.baseJdbcExtractor;
    }
    
    public void setBaseJdbcExtractor(final BaseJdbcExtractor baseJdbcExtractor) {
        this.baseJdbcExtractor = baseJdbcExtractor;
        if (this.baseJdbcExtractor instanceof TransactionListener) {
            this.addListener((TransactionListener)this.baseJdbcExtractor);
        }
    }
    
    public CacheFactory getCacheFactory() {
        return this.cacheFactory;
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setCacheConfigFile(final String cacheConfigFile) {
        this.cacheConfigFile = cacheConfigFile;
    }
    
    public void init() throws Exception {
        this.parseCacheConfig();
        final Set<Class<?>> set = Scans.getClasses(this.getScanPackage());
        final Map<String, PluginInfo> pluginMap = PluginLoader.getInstance().getPlugins();
        for (final Map.Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
            final PluginInfo pluginInfo = entry.getValue();
            final Enumeration<URL> urls = pluginInfo.getScanPackage();
            if (urls != null) {
                if (!urls.hasMoreElements()) {
                    continue;
                }
                final URL url = urls.nextElement();
                final String className = url.getFile();
                if (!StringUtils.isNotBlank(className) || className.indexOf("$") != -1) {
                    continue;
                }
                final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                set.add(clazz);
            }
        }
        for (final Class<?> clazz2 : set) {
            final com.reign.framework.jdbc.orm.annotation.JdbcEntity domain = Lang.getAnnotation(clazz2, com.reign.framework.jdbc.orm.annotation.JdbcEntity.class);
            if (domain != null) {
                final JdbcEntity entity = JdbcEntity.resolve(clazz2, new DefaultNameStrategy(), this, this.cacheFactory);
                this.entityMap.put(clazz2, entity);
            }
        }
    }
    
    public JdbcEntity getJdbcEntity(final Class<?> clazz) {
        return this.entityMap.get(clazz);
    }
    
    public String getScanPackage() {
        return this.scanPackage;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.init();
    }
    
    public JdbcSession openSession() {
        return new DefaultJdbcSession(this.dataSource, this);
    }
    
    public JdbcSession getCurrentSession() {
        return JdbcSessionUtil.getSession(this, false);
    }
    
    public void notifyTransactionBegin(final Transaction transaction) {
        if (this.listeners == null) {
            return;
        }
        for (final TransactionListener listener : this.listeners) {
            listener.begin(transaction);
        }
    }
    
    public void notifyTransactionBeforeCommit(final Transaction transaction, final boolean succ) {
        if (this.listeners == null) {
            return;
        }
        for (final TransactionListener listener : this.listeners) {
            listener.beforeCommit(transaction, succ);
        }
    }
    
    public void notifyTransactionCommit(final Transaction transaction, final boolean succ) {
        if (this.listeners == null) {
            return;
        }
        for (final TransactionListener listener : this.listeners) {
            listener.commit(transaction, succ);
        }
    }
    
    public void loadPlugin(final PluginInfo pluginInfo) throws Exception {
        final Enumeration<URL> urls = pluginInfo.getScanPackage();
        if (urls == null || !urls.hasMoreElements()) {
            return;
        }
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            final String className = url.getFile();
            if (StringUtils.isNotBlank(className) && className.indexOf("$") == -1) {
                final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                final com.reign.framework.jdbc.orm.annotation.JdbcEntity domain = Lang.getAnnotation(clazz, com.reign.framework.jdbc.orm.annotation.JdbcEntity.class);
                if (domain == null) {
                    continue;
                }
                final JdbcEntity entity = JdbcEntity.resolve(clazz, new DefaultNameStrategy(), this, this.cacheFactory);
                this.entityMap.put(clazz, entity);
            }
        }
    }
    
    public CacheConfig getCacheConfig(final String name) {
        CacheConfig config = this.cacheConfigMap.get(name);
        if (config == null) {
            config = this.cacheConfigMap.get("defaultCache");
        }
        return config;
    }
    
    private void addListener(final TransactionListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<TransactionListener>();
        }
        this.listeners.add(listener);
    }
    
    private void parseCacheConfig() {
        if (this.cacheConfigFile != null) {
            final XML xml = new XML(this.cacheConfigFile);
            XML.XMLNode node = xml.get("defaultCache");
            if (node != null) {
                final int timeToLiveSeconds = Integer.valueOf(node.getAttribute("timeToLiveSeconds"));
                final CacheConfig config = new CacheConfig("defaultCache", timeToLiveSeconds);
                this.cacheConfigMap.put("defaultCache", config);
            }
            node = xml.get("queryCache");
            if (node != null) {
                final int timeToLiveSeconds = Integer.valueOf(node.getAttribute("timeToLiveSeconds"));
                final CacheConfig config = new CacheConfig("queryCache", timeToLiveSeconds);
                this.cacheConfigMap.put("queryCache", config);
            }
            final List<XML.XMLNode> nodeList = (List<XML.XMLNode>)xml.getList("cache");
            for (final XML.XMLNode temp : nodeList) {
                final int timeToLiveSeconds2 = Integer.valueOf(temp.getAttribute("timeToLiveSeconds"));
                final String name = temp.getAttribute("name");
                final CacheConfig config2 = new CacheConfig(name, timeToLiveSeconds2);
                this.cacheConfigMap.put(name, config2);
            }
        }
        if (this.cacheConfigMap.size() == 0 || this.cacheConfigMap.get("defaultCache") == null) {
            final CacheConfig config3 = new CacheConfig("defaultCache", 600);
            this.cacheConfigMap.put("defaultCache", config3);
        }
    }
}
