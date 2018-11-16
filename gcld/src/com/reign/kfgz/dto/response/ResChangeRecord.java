package com.reign.kfgz.dto.response;

import java.util.*;

public class ResChangeRecord
{
    public static final String gold = "gold";
    public static final String copper = "copper";
    public static final String wood = "wood";
    public static final String food = "food";
    public static final String iron = "iron";
    public static final String exp = "exp";
    public static final String gExp = "gExp";
    public static final String recruitToken = "recruitToken";
    public static final String phantomCount = "phantomCount";
    private long id;
    private String unit;
    private long value;
    private boolean increase;
    private int gId;
    private Date time;
    
    public void setId(final long id) {
        this.id = id;
    }
    
    public long getId() {
        return this.id;
    }
    
    public void setUnit(final String unit) {
        this.unit = unit;
    }
    
    public String getUnit() {
        return this.unit;
    }
    
    public void setValue(final long value) {
        this.value = value;
    }
    
    public long getValue() {
        return this.value;
    }
    
    public void setTime(final Date time) {
        this.time = time;
    }
    
    public Date getTime() {
        return this.time;
    }
    
    public void setIncrease(final boolean increase) {
        this.increase = increase;
    }
    
    public boolean getIncrease() {
        return this.increase;
    }
    
    public void setgId(final int gId) {
        this.gId = gId;
    }
    
    public int getgId() {
        return this.gId;
    }
}
