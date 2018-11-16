package com.reign.util.concurrentLinkedHashMap;

interface Linked<T extends Linked<T>>
{
    T getPrevious();
    
    void setPrevious(final T p0);
    
    T getNext();
    
    void setNext(final T p0);
}
