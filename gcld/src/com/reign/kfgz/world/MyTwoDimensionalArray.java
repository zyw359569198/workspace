package com.reign.kfgz.world;

import java.util.*;

public class MyTwoDimensionalArray
{
    private int[][] data;
    Map<Integer, Integer> idMap;
    private int index;
    
    private synchronized int getSelfId(final int id) {
        if (!this.idMap.containsKey(id)) {
            this.idMap.put(id, this.index);
            ++this.index;
        }
        return this.idMap.get(id);
    }
    
    public MyTwoDimensionalArray(final int size) {
        this.idMap = new HashMap<Integer, Integer>();
        this.index = 0;
        this.data = new int[size][size];
    }
    
    public int get(final int x, final int y) {
        return this.data[this.getSelfId(x)][this.getSelfId(y)];
    }
    
    public void set(final int x, final int y, final int value) {
        this.data[this.getSelfId(x)][this.getSelfId(y)] = value;
    }
}
