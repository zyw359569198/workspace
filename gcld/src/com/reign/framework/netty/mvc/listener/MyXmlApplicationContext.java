package com.reign.framework.netty.mvc.listener;

import org.springframework.context.support.*;
import com.reign.framework.netty.servlet.*;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.beans.*;
import org.springframework.core.io.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import java.net.*;
import com.reign.framework.plugin.*;
import com.reign.framework.startup.*;
import java.util.*;
import org.apache.commons.lang.*;
import org.springframework.stereotype.*;

public class MyXmlApplicationContext extends AbstractRefreshableConfigApplicationContext
{
    private static final String COMPONENT_SCAN_PACKAGE = "componentScanPackage";
    private ServletContext servletContext;
    
    public MyXmlApplicationContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    @Override
	protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        final XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
        this.loadBeanDefinitions(beanDefinitionReader);
        this.initBeanDefinitionReader(beanDefinitionReader);
    }
    
    private void loadBeanDefinitions(final XmlBeanDefinitionReader reader) {
        final String[] configLocations = this.getConfigLocations();
        if (configLocations != null) {
            String[] array;
            for (int length = (array = configLocations).length, i = 0; i < length; ++i) {
                final String configLocation = array[i];
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }
    
    protected void initBeanDefinitionReader(final XmlBeanDefinitionReader beanDefinitionReader) {
        beanDefinitionReader.loadBeanDefinitions(new ByteArrayResource(this.generateComponentXML()));
    }
    
    @Override
	public String[] getConfigLocations() {
        return new String[] { "classpath*:applicationContext.xml" };
    }
    
    public byte[] generateComponentXML() {
        try {
            final String pkg = (String)this.servletContext.getInitParam("componentScanPackage");
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getClassLoader().getResourceAsStream("component.xml"));
            final Map<String, String> resourcesMap = this.scanClasses(pkg);
            for (final Map.Entry<String, String> entry : resourcesMap.entrySet()) {
                final Element e = doc.createElement("bean");
                e.setAttribute("id", entry.getKey());
                e.setAttribute("class", entry.getValue());
                doc.getDocumentElement().appendChild(e);
            }
            final DOMSource ds = new DOMSource(doc);
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final StringWriter writer = new StringWriter();
            final Result result = new StreamResult(writer);
            transformer.transform(ds, result);
            return writer.toString().getBytes();
        }
        catch (FileNotFoundException e2) {
            e2.printStackTrace();
        }
        catch (SAXException e3) {
            e3.printStackTrace();
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        catch (ParserConfigurationException e5) {
            e5.printStackTrace();
        }
        catch (TransformerException e6) {
            e6.printStackTrace();
        }
        return null;
    }
    
    private Map<String, String> scanClasses(final String pkg) {
        final Map<String, String> resourcesMap = new LinkedHashMap<String, String>();
        try {
            final Enumeration<URL> urls = this.getClassLoader().getResources(pkg);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    this.put(resourcesMap, url.getFile());
                }
            }
            final Map<String, PluginInfo> pluginMap = PluginLoader.getInstance().getPlugins();
            for (final Map.Entry<String, PluginInfo> entry : pluginMap.entrySet()) {
                final PluginInfo pluginInfo = entry.getValue();
                if (pluginInfo.urls != null) {
                    final Enumeration<URL> temp = pluginInfo.urls.elements();
                    while (temp.hasMoreElements()) {
                        final URL url2 = temp.nextElement();
                        this.put(resourcesMap, url2.getFile());
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        return resourcesMap;
    }
    
    private void put(final Map<String, String> resourcesMap, final String className) throws ClassNotFoundException {
        if (StringUtils.isNotBlank(className) && className.indexOf("$") == -1) {
            String simpleName = StringUtils.substringAfterLast(className, ".");
            final Class<?> clazz = this.getClassLoader().loadClass(className);
            simpleName = WordUtils.uncapitalize(simpleName);
            final Component component = clazz.getAnnotation(Component.class);
            if (component != null) {
                simpleName = (StringUtils.isBlank(component.value()) ? simpleName : component.value());
                resourcesMap.put(simpleName, className);
                return;
            }
            final javax.annotation.Resource resource = clazz.getAnnotation(javax.annotation.Resource.class);
            if (resource != null) {
                simpleName = (StringUtils.isBlank(resource.name()) ? simpleName : resource.name());
                resourcesMap.put(simpleName, className);
                return;
            }
            final Service service = clazz.getAnnotation(Service.class);
            if (service != null) {
                simpleName = (StringUtils.isBlank(service.value()) ? simpleName : service.value());
                resourcesMap.put(simpleName, className);
                return;
            }
            final Repository repository = clazz.getAnnotation(Repository.class);
            if (repository != null) {
                simpleName = (StringUtils.isBlank(repository.value()) ? simpleName : repository.value());
                resourcesMap.put(simpleName, className);
            }
        }
    }
}
