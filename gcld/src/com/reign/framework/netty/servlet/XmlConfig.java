package com.reign.framework.netty.servlet;

import java.util.*;
import org.apache.commons.lang.*;
import com.reign.framework.exception.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XmlConfig
{
    private String servletName;
    private Class<? extends Servlet> servletClass;
    private Map<String, Object> servletParamsMap;
    private Map<String, Object> nettyParamsMap;
    private Map<String, Object> nettyTcpParamsMap;
    private List<Class<?>> listenerList;
    
    public XmlConfig(final String path) {
        this.servletParamsMap = new HashMap<String, Object>();
        this.nettyParamsMap = new HashMap<String, Object>();
        this.nettyTcpParamsMap = new HashMap<String, Object>();
        this.listenerList = new ArrayList<Class<?>>();
        this.parse(path);
    }
    
    public ServletConfig getServletConfig() {
        return new ServletConfig() {
            private long sessionTimeOutMillis = -1L;
            private int sessionTickTime = -1;
            
            @Override
            public String getServletName() {
                return XmlConfig.this.servletName;
            }
            
            @Override
            public Class<? extends Servlet> getServletClass() {
                return XmlConfig.this.servletClass;
            }
            
            @Override
            public Map<String, Object> getInitParams() {
                return XmlConfig.this.servletParamsMap;
            }
            
            @Override
            public Object getInitParam(final String paramName) {
                return XmlConfig.this.servletParamsMap.get(paramName);
            }
            
            @Override
            public List<Class<?>> getListeners() {
                return XmlConfig.this.listenerList;
            }
            
            @Override
            public long getSessionTimeOutMillis() {
                if (this.sessionTimeOutMillis != -1L) {
                    return this.sessionTimeOutMillis;
                }
                final Integer minutes = (Integer)this.getInitParam("sessionTimeOut");
                return this.sessionTimeOutMillis = ((minutes == null) ? 120000 : (minutes * 60000));
            }
            
            @Override
            public int getSessionTickTime() {
                if (this.sessionTickTime != -1) {
                    return this.sessionTickTime;
                }
                final Integer seconds = (Integer)this.getInitParam("sessionTickTime");
                return this.sessionTickTime = ((seconds == null) ? 25 : seconds);
            }
        };
    }
    
    public NettyConfig getNettyConfig() {
        return new NettyConfig() {
            @Override
            public Map<String, Object> getTcpParams() {
                return XmlConfig.this.nettyTcpParamsMap;
            }
            
            @Override
            public Map<String, Object> getInitParams() {
                return XmlConfig.this.nettyParamsMap;
            }
            
            @Override
            public Object getInitParam(final String paramName) {
                return XmlConfig.this.nettyParamsMap.get(paramName);
            }
        };
    }
    
    private void parse(final String path) {
        if (StringUtils.isBlank(path)) {
            throw new ServletConfigException("can't parse servlet config, path must not be null");
        }
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(false);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(final SAXParseException arg0) throws SAXException {
                    throw arg0;
                }
                
                @Override
                public void fatalError(final SAXParseException arg0) throws SAXException {
                    throw arg0;
                }
                
                @Override
                public void error(final SAXParseException arg0) throws SAXException {
                    throw arg0;
                }
            });
            final Document doc = db.parse(XmlConfig.class.getClassLoader().getResourceAsStream(path));
            final Element nettyElement = (Element)doc.getElementsByTagName("netty").item(0);
            if (nettyElement != null) {
                this.parsePropertyNode(nettyElement, "init-param", "props", "property", this.nettyParamsMap);
                this.parsePropertyNode(nettyElement, "tcp-param", "props", "property", this.nettyTcpParamsMap);
            }
            final Element servletElement = (Element)doc.getElementsByTagName("servlet").item(0);
            if (servletElement == null) {
                throw new ServletConfigException("can't parse servlet config, can't found [servlet] element");
            }
            this.parsePropertyNode(servletElement, "init-param", "props", "property", this.servletParamsMap);
            final Element servletNameElement = (Element)servletElement.getElementsByTagName("servlet-name").item(0);
            if (servletNameElement == null) {
                throw new ServletConfigException("can't parse servlet config, can't found [servlet-name] element");
            }
            this.servletName = servletNameElement.getTextContent();
            final Element servletClassElement = (Element)servletElement.getElementsByTagName("servlet-class").item(0);
            if (servletClassElement == null) {
                throw new ServletConfigException("can't parse servlet config, can't found [servletClassElement] element");
            }
            this.servletClass = (Class<? extends Servlet>)Class.forName(servletClassElement.getTextContent());
            final NodeList nodeList = doc.getElementsByTagName("listener-class");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                if (node instanceof Element) {
                    final Element element = (Element)node;
                    this.listenerList.add(Class.forName(element.getTextContent()));
                }
            }
        }
        catch (Exception e) {
            throw new ServletConfigException("can't parse servlet config, have a exception", e);
        }
    }
    
    private void parsePropertyNode(final Element rootElement, final String rootName, final String secondName, final String nodeName, final Map<String, Object> map) {
        final Element initParamElement = (Element)rootElement.getElementsByTagName(rootName).item(0);
        if (initParamElement != null) {
            final NodeList children = initParamElement.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                final Node childNode = children.item(i);
                if (childNode instanceof Element) {
                    final Element element = (Element)childNode;
                    final String elementName = element.getNodeName();
                    if (secondName.equalsIgnoreCase(elementName)) {
                        final NodeList propsList = element.getChildNodes();
                        for (int j = 0; j < propsList.getLength(); ++j) {
                            final Node propsNode = propsList.item(j);
                            if (propsNode instanceof Element) {
                                final Element propsElement = (Element)propsNode;
                                final String propsElementName = propsElement.getNodeName();
                                if (nodeName.equalsIgnoreCase(propsElementName)) {
                                    final String name = propsElement.getAttribute("name");
                                    final String type = propsElement.getAttribute("type");
                                    final Object value = this.parseValue(type, propsElement.getTextContent());
                                    map.put(name, value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Object parseValue(final String type, final String value) {
        if ("int".equalsIgnoreCase(type)) {
            return Integer.valueOf(value);
        }
        if ("string".equalsIgnoreCase(type)) {
            return value;
        }
        if ("boolean".equalsIgnoreCase(type)) {
            return Boolean.valueOf(value);
        }
        return value;
    }
    
    public static void main(final String[] args) {
    }
}
