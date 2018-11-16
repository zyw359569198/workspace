package com.reign.gcld.common.web.listener;

import com.reign.framework.netty.servlet.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.component.*;
import java.lang.reflect.*;

public class JarLoadListener implements ServletContextListener
{
    private static final String COMPONENT_FIELD_NAME = "ENVIRONMENT";
    private static final String COMPONENT_COMPONENT_NAME = "componentName";
    private static final String COMPONENT_COMPONENT_VERSION = "version";
    
    @Override
	public void contextInitialized(final ServletContext sc) {
        this.jarLoadListener(sc);
    }
    
    private void jarLoadListener(final ServletContext sc) {
        final String componentParam = (String)sc.getInitParam("versionInfo");
        if (componentParam == null) {
            return;
        }
        final String[] components = componentParam.split(",");
        String[] array;
        for (int length = (array = components).length, i = 0; i < length; ++i) {
            final String component = array[i];
            if (!StringUtils.isBlank(component)) {
                try {
                    final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(component);
                    final Field field = clazz.getField("ENVIRONMENT");
                    final Map<String, String> map = (Map<String, String>)field.get(clazz);
                    ComponentManager.getInstance().addComponent(new ComponentMessage(map.get("componentName"), map.get("version")));
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                catch (SecurityException e2) {
                    e2.printStackTrace();
                }
                catch (NoSuchFieldException e3) {
                    e3.printStackTrace();
                }
                catch (IllegalArgumentException e4) {
                    e4.printStackTrace();
                }
                catch (IllegalAccessException e5) {
                    e5.printStackTrace();
                }
            }
        }
    }
    
    @Override
	public void contextDestoryed(final ServletContext sc) {
    }
}
