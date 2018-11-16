package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class GeneralRecruit implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer generalId;
    private Integer minRefurTime;
    private Integer goldMin;
    private Integer goldMax;
    private Integer copperMin;
    private Integer copperMax;
    private Double goldProb;
    private Integer dropIndex;
    private Integer type;
    private Integer powerId;
    private Integer npcId;
    private String intro;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getMinRefurTime() {
        return this.minRefurTime;
    }
    
    public void setMinRefurTime(final Integer minRefurTime) {
        this.minRefurTime = minRefurTime;
    }
    
    public Integer getGoldMin() {
        return this.goldMin;
    }
    
    public void setGoldMin(final Integer goldMin) {
        this.goldMin = goldMin;
    }
    
    public Integer getGoldMax() {
        return this.goldMax;
    }
    
    public void setGoldMax(final Integer goldMax) {
        this.goldMax = goldMax;
    }
    
    public Integer getCopperMin() {
        return this.copperMin;
    }
    
    public void setCopperMin(final Integer copperMin) {
        this.copperMin = copperMin;
    }
    
    public Integer getCopperMax() {
        return this.copperMax;
    }
    
    public void setCopperMax(final Integer copperMax) {
        this.copperMax = copperMax;
    }
    
    public Double getGoldProb() {
        return this.goldProb;
    }
    
    public void setGoldProb(final Double goldProb) {
        this.goldProb = goldProb;
    }
    
    public Integer getDropIndex() {
        return this.dropIndex;
    }
    
    public void setDropIndex(final Integer dropIndex) {
        this.dropIndex = dropIndex;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final Integer powerId) {
        this.powerId = powerId;
    }
    
    public Integer getNpcId() {
        return this.npcId;
    }
    
    public void setNpcId(final Integer npcId) {
        this.npcId = npcId;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
}
