package com.reign.gcld.player.dto;

public class ResourceDto
{
    private int type;
    private double value;
    private long maxValue;
    private int multiple;
    
    public ResourceDto(final int type, final double value, final long maxValue) {
        this.multiple = 1;
        this.type = type;
        this.value = value;
        this.maxValue = maxValue;
    }
    
    public ResourceDto(final int type, final double value) {
        this.multiple = 1;
        this.type = type;
        this.value = value;
    }
    
    public ResourceDto(final int type, final double value, final int multiple) {
        this.multiple = 1;
        this.type = type;
        this.value = value;
        this.multiple = multiple;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public double getValue() {
        return this.value;
    }
    
    public void setValue(final double value) {
        this.value = value;
    }
    
    public long getMaxValue() {
        return this.maxValue;
    }
    
    public void setMaxValue(final long maxValue) {
        this.maxValue = maxValue;
    }
    
    public void setMultiple(final int multiple) {
        this.multiple = multiple;
    }
    
    public int getMultiple() {
        return this.multiple;
    }
}
