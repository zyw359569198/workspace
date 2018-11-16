package com.reign.framework.plugin;

import org.apache.commons.logging.*;
import com.reign.framework.startup.*;
import com.reign.util.*;
import java.util.*;
import java.lang.reflect.*;

public class PluginLoader
{
    private static final Log log;
    private static final PluginLoader instance;
    private Map<String, PluginInfo> pluginMap;
    
    static {
        log = LogFactory.getLog(PluginLoader.class);
        instance = new PluginLoader();
    }
    
    public static PluginLoader getInstance() {
        return PluginLoader.instance;
    }
    
    private PluginLoader() {
        this.pluginMap = new HashMap<String, PluginInfo>();
    }
    
    public Map<String, PluginInfo> getPlugins() {
        return this.pluginMap;
    }
    
    public void init() throws Exception {
        final StandardClassLoader loader = (StandardClassLoader)Thread.currentThread().getContextClassLoader();
        final XML xml = new XML(this.getClass().getClassLoader().getResourceAsStream("plugins.xml"));
        final List<XML.XMLNode> nodeList = (List<XML.XMLNode>)xml.getList("plugin");
        for (final XML.XMLNode node : nodeList) {
            final String name = node.get("name").getValue();
            final String url = node.get("url").getValue();
            final PluginInfo pluginInfo = loader.loadPlugin(name, url);
            this.pluginMap.put(name, pluginInfo);
            PluginLoader.log.info("\u8f7d\u5165\u63d2\u4ef6[name: " + name + ", url: " + url + "]");
        }
    }
    
    public void check() {
        for (final Map.Entry<String, PluginInfo> entry : this.pluginMap.entrySet()) {
            final PluginInfo pluginInfo = entry.getValue();
            try {
                final Class<?> clazz = this.getClass().getClassLoader().loadClass(entry.getValue().checkClass);
                final Method method = clazz.getMethod("check", new Class[0]);
                method.invoke(clazz, new Object[0]);
            }
            catch (Exception e) {
                throw new RuntimeException("check plugin " + pluginInfo.name + " fail", e);
            }
        }
    }
}
