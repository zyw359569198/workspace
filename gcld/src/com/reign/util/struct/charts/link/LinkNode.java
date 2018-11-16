package com.reign.util.struct.charts.link;

import com.reign.util.struct.charts.*;

public class LinkNode<T extends ISetSeqable<T>>
{
    private T data;
    private LinkNode<T> prev;
    private LinkNode<T> next;
    
    public LinkNode(final T data, final LinkNode<T> prev, final LinkNode<T> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
    
    public LinkNode<T> getPrev() {
        return this.prev;
    }
    
    public void setPrev(final LinkNode<T> prev) {
        this.prev = prev;
    }
    
    public LinkNode<T> getNext() {
        return this.next;
    }
    
    public void setNext(final LinkNode<T> next) {
        this.next = next;
    }
    
    public T getData() {
        return this.data;
    }
}
