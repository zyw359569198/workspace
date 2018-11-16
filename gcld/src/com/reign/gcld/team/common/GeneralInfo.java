package com.reign.gcld.team.common;

public class GeneralInfo
{
    private int generalId;
    private int generalLv;
    private int generalQuality;
    private String generalName;
    private long maxFroces;
    private long froces;
    
    public long getFroces() {
        return this.froces;
    }
    
    public void setFroces(final long froces) {
        this.froces = froces;
    }
    
    public long getMaxFroces() {
        return this.maxFroces;
    }
    
    public void setMaxFroces(final long maxFroces) {
        this.maxFroces = maxFroces;
    }
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public int getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final int generalLv) {
        this.generalLv = generalLv;
    }
    
    public int getGeneralQuality() {
        return this.generalQuality;
    }
    
    public void setGeneralQuality(final int generalQuality) {
        this.generalQuality = generalQuality;
    }
    
    public String getGeneralName() {
        return this.generalName;
    }
    
    public void setGeneralName(final String generalName) {
        this.generalName = generalName;
    }
}
