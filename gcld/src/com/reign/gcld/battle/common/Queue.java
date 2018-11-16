package com.reign.gcld.battle.common;

import java.io.*;

public class Queue implements Serializable
{
    private static final long serialVersionUID = -7637022551191658322L;
    private int front;
    private int back;
    private int size;
    private int maxSize;
    private Object[] data;
    private Object lock;
    
    public Queue(final int maxSize) {
        this.front = 0;
        this.size = 0;
        this.lock = new Object();
        this.maxSize = maxSize;
        this.data = new Object[maxSize];
    }
    
    public int getSize() {
        return this.size;
    }
    
    public Object dequeue() {
        synchronized (this.lock) {
            Object obj = null;
            if (this.size > 0) {
                --this.size;
                obj = this.data[this.front];
                this.data[this.front] = null;
                this.front = ((this.front + 1 > this.data.length - 1) ? 0 : (this.front + 1));
            }
            // monitorexit(this.lock)
            return obj;
        }
    }
    
    public boolean canEnqueue() {
        synchronized (this.lock) {
            if (this.size >= this.maxSize) {
                // monitorexit(this.lock)
                return false;
            }
            // monitorexit(this.lock)
            return true;
        }
    }
    
    public Object getQueue(int i) {
        synchronized (this.lock) {
            i = ((this.front + i > this.data.length - 1) ? (this.front + i - this.data.length) : (this.front + i));
            // monitorexit(this.lock)
            return this.data[i];
        }
    }
    
    public void enqueue(final Object obj) {
        synchronized (this.lock) {
            this.back = ((this.back > this.data.length - 1) ? 0 : this.back);
            this.data[this.back] = obj;
            ++this.back;
            ++this.size;
        }
        // monitorexit(this.lock)
    }
}
