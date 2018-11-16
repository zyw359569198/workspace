package com.reign.kfgz.dto;

import java.util.*;

public class KfgzPhantomToExpObj
{
    public int cId;
    public double copperSum;
    public double chiefExpSum;
    public Map<Integer, Double> gExpMap;
    
    public KfgzPhantomToExpObj(final int cId) {
        this.copperSum = 0.0;
        this.chiefExpSum = 0.0;
        this.gExpMap = new HashMap<Integer, Double>();
        this.cId = cId;
    }
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public double getCopperSum() {
        return this.copperSum;
    }
    
    public void setCopperSum(final double copperSum) {
        this.copperSum = copperSum;
    }
    
    public double getChiefExpSum() {
        return this.chiefExpSum;
    }
    
    public void setChiefExpSum(final double chiefExpSum) {
        this.chiefExpSum = chiefExpSum;
    }
    
    public Map<Integer, Double> getgExpMap() {
        return this.gExpMap;
    }
    
    public void setgExpMap(final Map<Integer, Double> gExpMap) {
        this.gExpMap = gExpMap;
    }
}
