package com.reign.kfgz.dto;

public class KfgzWorldStgResult
{
    int cityId;
    int allDamage;
    int wStgId;
    
    public KfgzWorldStgResult(final int cityId, final int allDamage, final int wStgId) {
        this.cityId = cityId;
        this.allDamage = allDamage;
        this.wStgId = wStgId;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getAllDamage() {
        return this.allDamage;
    }
    
    public void setAllDamage(final int allDamage) {
        this.allDamage = allDamage;
    }
    
    public int getwStgId() {
        return this.wStgId;
    }
    
    public void setwStgId(final int wStgId) {
        this.wStgId = wStgId;
    }
}
