/*
 * $Header: XML.java
 * $Revision: 1.0.0.0
 * $CreateDate: 2012-8-3 上午10:54:47
 * $Owner: wangys
 */
package com.reign.framework.startup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML
 * @author wangys
 * @version 1.0.0.0 2012-8-3 上午10:54:47
 */
public class XML {
    /** doc */
    private Document doc;
    
    /** 根节点 */
    private Element root;
    
    /**
     * 构造函数
     * @param path
     */
    public XML(String path) {
        try {
            init(new FileInputStream(path));
        } catch (Exception e) {
            throw new RuntimeException("init xml error", e);
        }
    }
    
    /**
     * 构造函数
     * @param is
     */
    public XML(InputStream is) {
        try {
            init(is);
        } catch (Exception e) {
            throw new RuntimeException("init xml error", e);
        }
    }
    
    /**
     * 获得指定节点
     * @param tagName
     * @return
     * @version 1.0.0.0 2012-8-3 上午11:17:13
     */
    public XMLNode get(String tagName) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();
                if (nodeName.equalsIgnoreCase(tagName)) {
                    return new XMLNode((Element) node);
                }
            }
        }
        return null;
    }
    
    /**
     * 获取节点列表
     * @param tagName
     * @return
     * @version 1.0.0.0 2012-8-3 上午11:17:41
     */
    public List<XMLNode> getList(String tagName) {
        NodeList nodeList = root.getChildNodes();
        List<XMLNode> resultList = new ArrayList<XMLNode>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();
                if (nodeName.equalsIgnoreCase(tagName)) {
                    resultList.add(new XMLNode((Element) node));
                }
            }
        }
        return resultList;
    }

    /**
     * 初始化
     * @param path
     * @version 1.0.0.0 2012-8-3 上午11:02:52
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    private void init(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder  builder = dbf.newDocumentBuilder();
        this.doc = builder.parse(is);
        this.root = doc.getDocumentElement();
    }
    
    /** XMLNode */
    public class XMLNode {
        /** node */
        private Element element;
        
        /**
         * @param node
         */
        public XMLNode(Element element) {
            this.element = element;
        }
        
        /**
         * 获得节点的值
         * e.g: <player>你好</player> 这样获取到的值是：你好
         * @return
         * @version 1.0.0.0 2012-8-3 上午11:31:16
         */
        public String getValue() {
            return element.getTextContent();
        }
        
        /**
         * 获取Attribute值
         * @param attr
         * @return
         * @version 1.0.0.0 2012-8-3 上午11:31:45
         */
        public String getAttribute(String attr) {
            return element.getAttribute(attr);
        }
        
        /**
         * 获取子节点
         * @param tagName
         * @return
         * @version 1.0.0.0 2012-8-3 上午11:31:54
         */
        public XMLNode get(String tagName) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase(tagName)) {
                        return new XMLNode((Element) node);
                    }
                }
            }
            return null;
        }
        
        /**
         * 获取子节点列表
         * @param tagName
         * @return
         * @version 1.0.0.0 2012-8-3 上午11:32:06
         */
        public List<XMLNode> getList(String tagName) {
            NodeList nodeList = element.getChildNodes();
            List<XMLNode> resultList = new ArrayList<XMLNode>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase(tagName)) {
                        resultList.add(new XMLNode((Element) node));
                    }
                }
            }
            return resultList;
        }
    }
}
