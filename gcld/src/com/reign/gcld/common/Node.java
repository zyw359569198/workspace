package com.reign.gcld.common;

public class Node<E>
{
    public E e;
    public Node<E> prev;
    public Node<E> next;
    
    public Node() {
        this.e = null;
        this.prev = null;
        this.next = null;
    }
    
    public Node(final E e, final Node<E> prev, final Node<E> next) {
        this.e = e;
        this.prev = prev;
        this.next = next;
    }
    
    public Node(final E e) {
        this.e = e;
        this.prev = null;
        this.next = null;
    }
}
