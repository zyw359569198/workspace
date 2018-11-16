package com.reign.gcld.world.domain;

public class CityMessage
{
    private String type;
    private int cityId;
    private int forceId;
    
    public CityMessage(final int c, final int f, final String type) {
        this.cityId = c;
        this.forceId = f;
        this.type = type;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void messageChanged() {
    }
    
    public String getMessage(final int forceId) {
        return "";
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
}
