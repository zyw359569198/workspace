package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class OfficerAffair implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intro;
    private Integer time;
    private Integer resourceOutputType;
    private Integer resourceOutput;
    private Integer officerExpOutput;
    private Integer openLv;
    private Integer upgradeInterval;
    private Integer maxLevel;
    private Integer upgradeOutputIncrease;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
    
    public Integer getResourceOutputType() {
        return this.resourceOutputType;
    }
    
    public void setResourceOutputType(final Integer resourceOutputType) {
        this.resourceOutputType = resourceOutputType;
    }
    
    public Integer getResourceOutput() {
        return this.resourceOutput;
    }
    
    public void setResourceOutput(final Integer resourceOutput) {
        this.resourceOutput = resourceOutput;
    }
    
    public Integer getOfficerExpOutput() {
        return this.officerExpOutput;
    }
    
    public void setOfficerExpOutput(final Integer officerExpOutput) {
        this.officerExpOutput = officerExpOutput;
    }
    
    public Integer getOpenLv() {
        return this.openLv;
    }
    
    public void setOpenLv(final Integer openLv) {
        this.openLv = openLv;
    }
    
    public Integer getUpgradeInterval() {
        return this.upgradeInterval;
    }
    
    public void setUpgradeInterval(final Integer upgradeInterval) {
        this.upgradeInterval = upgradeInterval;
    }
    
    public Integer getMaxLevel() {
        return this.maxLevel;
    }
    
    public void setMaxLevel(final Integer maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    public Integer getUpgradeOutputIncrease() {
        return this.upgradeOutputIncrease;
    }
    
    public void setUpgradeOutputIncrease(final Integer upgradeOutputIncrease) {
        this.upgradeOutputIncrease = upgradeOutputIncrease;
    }
}
