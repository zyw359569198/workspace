package com.reign.kfgz.resource.dto;

public class ConsumeResourceDto
{
    private int value;
    private String unit;
    private String reason;
    
    public void setValue(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void setUnit(final String unit) {
        this.unit = unit;
    }
    
    public String getUnit() {
        return this.unit;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public String getReason() {
        return this.reason;
    }
}
