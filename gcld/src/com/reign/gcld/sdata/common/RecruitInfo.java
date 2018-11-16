package com.reign.gcld.sdata.common;

public class RecruitInfo
{
    private int generalId;
    private String generalName;
    private int quality;
    private int dropIndex;
    private int powerId;
    private String powerName;
    
    public int getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final int generalId) {
        this.generalId = generalId;
    }
    
    public String getGeneralName() {
        return this.generalName;
    }
    
    public void setGeneralName(final String generalName) {
        this.generalName = generalName;
    }
    
    public int getQuality() {
        return this.quality;
    }
    
    public void setQuality(final int quality) {
        this.quality = quality;
    }
    
    public int getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final int powerId) {
        this.powerId = powerId;
    }
    
    public String getPowerName() {
        return this.powerName;
    }
    
    public void setPowerName(final String powerName) {
        this.powerName = powerName;
    }
    
    public int getDropIndex() {
        return this.dropIndex;
    }
    
    public void setDropIndex(final int dropIndex) {
        this.dropIndex = dropIndex;
    }
}
