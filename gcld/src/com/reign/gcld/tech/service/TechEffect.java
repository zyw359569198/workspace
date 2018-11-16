package com.reign.gcld.tech.service;

public class TechEffect
{
    private int tuntian;
    
    public TechEffect(final int playerId) {
        this.tuntian = 0;
        this.tuntian = 11;
    }
    
    public int getTuntian() {
        return this.tuntian;
    }
    
    public void refreshTuntian(final int id) {
        this.tuntian = 11;
    }
}
