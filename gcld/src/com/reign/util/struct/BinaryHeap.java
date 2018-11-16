package com.reign.util.struct;

import org.apache.commons.logging.*;
import java.util.*;

public class BinaryHeap<T>
{
    private static final Log logger;
    private Comparator<T> _comparator;
    private List<T> _array;
    private int _capacity;
    
    static {
        logger = LogFactory.getLog(BinaryHeap.class);
    }
    
    public BinaryHeap(final int length, final Comparator<T> comparator) {
        this._comparator = comparator;
        this._array = new ArrayList<T>(length);
        this._capacity = length;
    }
    
    public int size() {
        return this._array.size();
    }
    
    public T getItem(final int i) {
        if (i > this._array.size()) {
            BinaryHeap.logger.error("out of index when getItem.");
            return null;
        }
        return this._array.get(i - 1);
    }
    
    public void setItem(final int i, final T obj) {
        if (i > this._array.size()) {
            BinaryHeap.logger.error("out of index when getItem.");
        }
        else {
            this._array.set(i - 1, obj);
        }
    }
    
    public void enqueue(final T obj) {
        if (this._array.size() >= this._capacity) {
            BinaryHeap.logger.error("binary heap is full");
            return;
        }
        this._array.add(obj);
        int i;
        for (i = this._array.size(); i > 1 && this._comparator.compare(this._array.get(i / 2 - 1), obj) > 0; i /= 2) {
            this._array.set(i - 1, this._array.get(i / 2 - 1));
        }
        this._array.set(i - 1, obj);
    }
    
    public T findMin() {
        if (this._array.size() == 0) {
            BinaryHeap.logger.error("binary heap is empty");
            return null;
        }
        return this._array.get(0);
    }
    
    public T dequeueMin() {
        final T rtn = this.findMin();
        this._array.set(0, this._array.get(this._array.size() - 1));
        this._array.remove(this._array.size() - 1);
        int i = 1;
        T curr = null;
        T left = null;
        T right = null;
        while (i * 2 <= this._array.size()) {
            curr = this._array.get(i - 1);
            left = this._array.get(i * 2 - 1);
            if (i * 2 < this._array.size()) {
                right = this._array.get(i * 2);
            }
            if (this._comparator.compare(left, curr) > 0) {
                if (i * 2 >= this._array.size()) {
                    break;
                }
                if (this._comparator.compare(right, curr) > 0) {
                    break;
                }
            }
            if (i * 2 < this._array.size() && this._comparator.compare(left, right) > 0) {
                this._array.set(i * 2, curr);
                this._array.set(i - 1, right);
                i = i * 2 + 1;
            }
            else {
                this._array.set(i * 2 - 1, curr);
                this._array.set(i - 1, left);
                i *= 2;
            }
        }
        return rtn;
    }
    
    public void adjustItem(int index) {
        if (index < 1 || index > this._array.size()) {
            BinaryHeap.logger.error("out of index when adjustItem");
        }
        T curr;
        for (curr = this._array.get(index - 1); index > 1 && this._comparator.compare(this._array.get(index / 2 - 1), curr) > 0; index /= 2) {
            this._array.set(index - 1, this._array.get(index / 2 - 1));
        }
        this._array.set(index - 1, curr);
    }
}
