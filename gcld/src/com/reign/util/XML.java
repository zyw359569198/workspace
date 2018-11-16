package com.reign.util;

import org.w3c.dom.*;
import java.util.*;
import org.xml.sax.*;
import java.io.*;
import javax.xml.parsers.*;

public class XML
{
    private Document doc;
    private Element root;
    
    public XML(final String path) {
        try {
            this.init(new FileInputStream(path));
        }
        catch (Exception e) {
            throw new RuntimeException("init xml error", e);
        }
    }
    
    public XML(final InputStream is) {
        try {
            this.init(is);
        }
        catch (Exception e) {
            throw new RuntimeException("init xml error", e);
        }
    }
    
    public XMLNode get(final String tagName) {
        final NodeList nodeList = this.root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == 1) {
                final String nodeName = node.getNodeName();
                if (nodeName.equalsIgnoreCase(tagName)) {
                    return new XMLNode((Element)node);
                }
            }
        }
        return null;
    }
    
    public List<XMLNode> getList(final String tagName) {
        final NodeList nodeList = this.root.getChildNodes();
        final List<XMLNode> resultList = new ArrayList<XMLNode>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == 1) {
                final String nodeName = node.getNodeName();
                if (nodeName.equalsIgnoreCase(tagName)) {
                    resultList.add(new XMLNode((Element)node));
                }
            }
        }
        return resultList;
    }
    
    private void init(final InputStream is) throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = dbf.newDocumentBuilder();
        this.doc = builder.parse(is);
        this.root = this.doc.getDocumentElement();
    }
    
    public class XMLNode
    {
        private Element element;
        
        public XMLNode(final Element element) {
            this.element = element;
        }
        
        public String getValue() {
            return this.element.getTextContent();
        }
        
        public String getAttribute(final String attr) {
            return this.element.getAttribute(attr);
        }
        
        public XMLNode get(final String tagName) {
            final NodeList nodeList = this.element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == 1) {
                    final String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase(tagName)) {
                        return new XMLNode((Element)node);
                    }
                }
            }
            return null;
        }
        
        public List<XMLNode> getList(final String tagName) {
            final NodeList nodeList = this.element.getChildNodes();
            final List<XMLNode> resultList = new ArrayList<XMLNode>();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == 1) {
                    final String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase(tagName)) {
                        resultList.add(new XMLNode((Element)node));
                    }
                }
            }
            return resultList;
        }
    }
}
