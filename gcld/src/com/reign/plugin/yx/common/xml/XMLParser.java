package com.reign.plugin.yx.common.xml;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.net.*;
import java.io.*;

public class XMLParser
{
    private InputStream is;
    private Document doc;
    
    public XMLParser(final InputStream is) {
        this.is = is;
        this.init();
    }
    
    public XMLParser(final String value) {
        this(new ByteArrayInputStream(value.getBytes()));
    }
    
    public XMLParser(final String value, final String encoding) throws UnsupportedEncodingException {
        this(new ByteArrayInputStream(value.getBytes(encoding)));
    }
    
    public String getValueByTagName(final String tagName) {
        if (this.doc == null) {
            return null;
        }
        final NodeList nodeList = this.doc.getElementsByTagName(tagName);
        final int length = nodeList.getLength();
        if (nodeList == null || length == 0) {
            return null;
        }
        for (int i = 0; i < length; ++i) {
            final Node node = nodeList.item(i);
            if (node instanceof Element) {
                final Element element = (Element)node;
                return element.getTextContent();
            }
        }
        return null;
    }
    
    private void init() {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(false);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(final SAXParseException exception) throws SAXException {
                    throw exception;
                }
                
                @Override
                public void fatalError(final SAXParseException exception) throws SAXException {
                    throw exception;
                }
                
                @Override
                public void warning(final SAXParseException exception) {
                }
            });
            this.doc = db.parse(this.is);
        }
        catch (Exception e) {
            throw new RuntimeException("Error loading configuration file ", e);
        }
    }
    
    public static void main(final String[] args) throws IOException {
        final String str = "<s>\u6211</s>";
        byte[] bytes;
        for (int length = (bytes = str.getBytes()).length, i = 0; i < length; ++i) {
            final byte b = bytes[i];
            System.out.print(String.valueOf(b) + " ");
        }
        System.out.println();
        final String newStr = URLEncoder.encode(str, "UTF-8");
        System.out.println(newStr);
        final XMLParser parser = new XMLParser(str);
        System.out.println(parser.getValueByTagName("s"));
    }
}
