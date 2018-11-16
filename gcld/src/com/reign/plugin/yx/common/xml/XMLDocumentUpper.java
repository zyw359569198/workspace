package com.reign.plugin.yx.common.xml;

public class XMLDocumentUpper
{
    private StringBuffer buffer;
    private XMLNode root;
    
    public XMLDocumentUpper(final String version, final String encoding) {
        (this.buffer = new StringBuffer()).append("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?>");
    }
    
    public XMLDocumentUpper() {
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
            XMLDocumentUpper.this.buffer.append("<").append(this.name).append(">");
            return XMLDocumentUpper.this.buffer;
        }
        
        public StringBuffer EndElement() {
            XMLDocumentUpper.this.buffer.append("</").append(this.name).append(">");
            return XMLDocumentUpper.this.buffer;
        }
        
        public StringBuffer appendContent(final Object o) {
            if (o != null) {
                XMLDocumentUpper.this.buffer.append(o.toString());
            }
            else {
                XMLDocumentUpper.this.buffer.append("");
            }
            return XMLDocumentUpper.this.buffer;
        }
    }
}
