package com.reign.plugin.yx.common.xml;

public class XMLDocument
{
    private StringBuffer buffer;
    private XMLNode root;
    
    public XMLDocument(final String version, final String encoding) {
        (this.buffer = new StringBuffer()).append("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?>");
    }
    
    public XMLDocument() {
        this.buffer = new StringBuffer();
    }
    
    public void startRoot(final String elementName) {
        (this.root = new XMLNode(elementName)).StartElement();
    }
    
    public void endRoot(final String elementName) {
        (this.root = new XMLNode(elementName)).EndElement();
    }
    
    public void createElement(final String elementName, final Object o) {
        final XMLNode node = new XMLNode(elementName);
        node.StartElement();
        node.appendContent(o);
        node.EndElement();
    }
    
    public void append(final String xml) {
        this.buffer.append(xml);
    }
    
    @Override
    public String toString() {
        return this.buffer.toString();
    }
    
    class XMLNode
    {
        private String name;
        
        public XMLNode(final String name) {
            this.name = name;
        }
        
        public StringBuffer StartElement() {
            XMLDocument.this.buffer.append("<").append(this.name.toLowerCase()).append(">");
            return XMLDocument.this.buffer;
        }
        
        public StringBuffer EndElement() {
            XMLDocument.this.buffer.append("</").append(this.name.toLowerCase()).append(">");
            return XMLDocument.this.buffer;
        }
        
        public StringBuffer appendContent(final Object o) {
            if (o != null) {
                XMLDocument.this.buffer.append(o.toString());
            }
            else {
                XMLDocument.this.buffer.append("");
            }
            return XMLDocument.this.buffer;
        }
    }
}
