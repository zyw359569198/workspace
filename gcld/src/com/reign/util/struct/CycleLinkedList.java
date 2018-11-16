package com.reign.util.struct;

import java.util.*;

public class CycleLinkedList<E> extends LinkedList<E>
{
    private static final long serialVersionUID = 1L;
    private int maxCount;
    
    public CycleLinkedList(final int maxCount) {
        this.maxCount = maxCount;
    }
    
    @Override
    public boolean add(final E e) {
        this.addLast(e);
        if (this.size() > this.maxCount) {
            this.removeFirst();
        }
        return true;
    }
}
