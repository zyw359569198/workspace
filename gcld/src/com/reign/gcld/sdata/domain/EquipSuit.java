package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EquipSuit implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer quality;
    private Integer minChiefLv;
    private String equipList;
    private Integer maxIntimacyLv;
    
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
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public Integer getMinChiefLv() {
        return this.minChiefLv;
    }
    
    public void setMinChiefLv(final Integer minChiefLv) {
        this.minChiefLv = minChiefLv;
    }
    
    public String getEquipList() {
        return this.equipList;
    }
    
    public void setEquipList(final String equipList) {
        this.equipList = equipList;
    }
    
    public Integer getMaxIntimacyLv() {
        return this.maxIntimacyLv;
    }
    
    public void setMaxIntimacyLv(final Integer maxIntimacyLv) {
        this.maxIntimacyLv = maxIntimacyLv;
    }
}
