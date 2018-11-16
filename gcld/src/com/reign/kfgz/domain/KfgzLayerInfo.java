package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzLayerInfo implements IModel
{
    int layerId;
    String name;
    String upDownRule;
    float expCoef;
    int worldId;
    int worldNpcId;
    int worldStgId;
    
    public int getLayerId() {
        return this.layerId;
    }
    
    public void setLayerId(final int layerId) {
        this.layerId = layerId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getUpDownRule() {
        return this.upDownRule;
    }
    
    public void setUpDownRule(final String upDownRule) {
        this.upDownRule = upDownRule;
    }
    
    public float getExpCoef() {
        return this.expCoef;
    }
    
    public void setExpCoef(final float expCoef) {
        this.expCoef = expCoef;
    }
    
    public int getWorldId() {
        return this.worldId;
    }
    
    public void setWorldId(final int worldId) {
        this.worldId = worldId;
    }
    
    public int getWorldNpcId() {
        return this.worldNpcId;
    }
    
    public void setWorldNpcId(final int worldNpcId) {
        this.worldNpcId = worldNpcId;
    }
    
    public int getWorldStgId() {
        return this.worldStgId;
    }
    
    public void setWorldStgId(final int worldStgId) {
        this.worldStgId = worldStgId;
    }
}
