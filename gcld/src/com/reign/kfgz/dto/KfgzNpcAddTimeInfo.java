package com.reign.kfgz.dto;

import java.util.*;
import java.util.concurrent.*;

public class KfgzNpcAddTimeInfo
{
    int gzId;
    Map<Integer, Long[]> force1cityCDMap;
    Map<Integer, Long[]> force2cityCDMap;
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public KfgzNpcAddTimeInfo(final int gzId) {
        this.force1cityCDMap = new ConcurrentHashMap<Integer, Long[]>();
        this.force2cityCDMap = new ConcurrentHashMap<Integer, Long[]>();
        this.gzId = gzId;
    }
    
    public Map<Integer, Long[]> getForce1cityCDMap() {
        return this.force1cityCDMap;
    }
    
    public void setForce1cityCDMap(final Map<Integer, Long[]> force1cityCDMap) {
        this.force1cityCDMap = force1cityCDMap;
    }
    
    public Map<Integer, Long[]> getForce2cityCDMap() {
        return this.force2cityCDMap;
    }
    
    public void setForce2cityCDMap(final Map<Integer, Long[]> force2cityCDMap) {
        this.force2cityCDMap = force2cityCDMap;
    }
}
