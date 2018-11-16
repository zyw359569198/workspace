package com.reign.util.characterFilter.PatternAC;

import java.util.*;

public class Node
{
    private char content;
    private Node fail;
    private HashMap<Character, Node> next;
    private int strLen;
    
    public Node(final char content) {
        this.content = content;
        this.fail = null;
        this.next = new HashMap<Character, Node>();
        this.strLen = 0;
    }
    
    public Node(final char content, final int len) {
        this.content = content;
        this.fail = null;
        this.next = new HashMap<Character, Node>();
        this.strLen = len;
    }
    
    public Node getFail() {
        return this.fail;
    }
    
    public void setFail(final Node fail) {
        this.fail = fail;
    }
    
    public boolean isEndChar() {
        return this.strLen > 0;
    }
    
    public int getStrLen() {
        return this.strLen;
    }
    
    public void setEndChar(final int strLen) {
        this.strLen = strLen;
    }
    
    public char getContent() {
        return this.content;
    }
    
    public void addNext(final Node node) {
        this.next.put(node.content, node);
    }
    
    public Node getNext(final char content) {
        return this.next.get(content);
    }
    
    public boolean isContains(final char content) {
        return this.next.containsKey(content);
    }
    
    public Collection<Node> getNext() {
        return this.next.values();
    }
}
