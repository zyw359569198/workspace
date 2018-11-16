package com.reign.framework.json;

import com.alibaba.fastjson.serializer.*;
import com.reign.util.*;

public class JsonDocument
{
    private SerializeWriter out;
    private JSONSerializer serializer;
    private boolean first;
    
    public JsonDocument() {
        this.first = true;
        this.out = new SerializeWriter();
        this.serializer = new JSONSerializer(this.out);
    }
    
    public void reset() {
        this.out = new SerializeWriter();
        this.serializer = new JSONSerializer(this.out);
        this.first = true;
    }
    
    public void startObject(final String elementName) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_QUOT).append(elementName.toCharArray()).append(SymbolConstants.B_QUOT).append(SymbolConstants.B_COLON).append(SymbolConstants.B_L_BRACE);
        this.first = true;
    }
    
    public void startObject() {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_L_BRACE);
        this.first = true;
    }
    
    public void endObject() {
        this.append(SymbolConstants.B_R_BRACE);
        this.first = false;
    }
    
    public void startArray(final String elementName) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_QUOT).append(elementName.toCharArray()).append(SymbolConstants.B_QUOT).append(SymbolConstants.B_COLON).append(SymbolConstants.B_L_BRACKET);
        this.first = true;
    }
    
    public void startArray() {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_L_BRACKET);
        this.first = true;
    }
    
    public void endArray() {
        this.append(SymbolConstants.B_R_BRACKET);
        this.first = false;
    }
    
    public void createElement(final String elementName, final Object o) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_QUOT).append(elementName.toCharArray()).append(SymbolConstants.B_QUOT).append(SymbolConstants.B_COLON);
        this.createValue(o);
        this.first = false;
    }
    
    public void createElement(final Object o) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.createValue(o);
        this.first = false;
    }
    
    private void createValue(final Object o) {
        try {
            this.serializer.write(o);
        }
        catch (Throwable t) {
            throw new RuntimeException("", t);
        }
    }
    
    public void appendJson(final byte[] json) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(json);
        this.first = false;
    }
    
    public void appendJson(final String elementName, final byte[] json) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_QUOT).append(elementName.toCharArray()).append(SymbolConstants.B_QUOT).append(SymbolConstants.B_COLON);
        this.append(json);
        this.first = false;
    }
    
    public void appendObjectJson(final String elementName, final byte[] json) {
        if (!this.first) {
            this.append(SymbolConstants.B_COMMA);
        }
        this.append(SymbolConstants.B_QUOT).append(elementName.toCharArray()).append(SymbolConstants.B_QUOT).append(SymbolConstants.B_COLON).append(SymbolConstants.B_L_BRACE);
        this.append(json);
        this.append(SymbolConstants.B_R_BRACE);
        this.first = false;
    }
    
    @Override
    public String toString() {
        return new String(this.out.toBytes("UTF-8"));
    }
    
    public byte[] toByte() {
        return this.out.toBytes("UTF-8");
    }
    
    private JsonDocument append(final char[] bytes) {
        try {
            this.out.write(bytes);
            return this;
        }
        catch (Throwable t) {
            throw new RuntimeException("", t);
        }
    }
    
    private JsonDocument append(final byte[] bytes) {
        try {
            this.out.write(new String(bytes, 0, bytes.length));
            return this;
        }
        catch (Throwable t) {
            throw new RuntimeException("", t);
        }
    }
}
