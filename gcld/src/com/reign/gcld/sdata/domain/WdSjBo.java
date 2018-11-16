package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjBo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pic;
    private Integer terrain;
    private Integer terrainEffectType;
    private Integer chief;
    private String npcs;
    private Integer[] armyIds;
    private Integer level;
    private String plot;
    private String reward;
    private Integer view;
    private Integer rewardLv;
    private String notice;
    
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
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final Integer terrain) {
        this.terrain = terrain;
    }
    
    public Integer getTerrainEffectType() {
        return this.terrainEffectType;
    }
    
    public void setTerrainEffectType(final Integer terrainEffectType) {
        this.terrainEffectType = terrainEffectType;
    }
    
    public Integer getChief() {
        return this.chief;
    }
    
    public void setChief(final Integer chief) {
        this.chief = chief;
    }
    
    public String getNpcs() {
        return this.npcs;
    }
    
    public void setNpcs(final String npcs) {
        this.npcs = npcs;
    }
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public String getPlot() {
        return this.plot;
    }
    
    public void setPlot(final String plot) {
        this.plot = plot;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getView() {
        return this.view;
    }
    
    public void setView(final Integer view) {
        this.view = view;
    }
    
    public Integer getRewardLv() {
        return this.rewardLv;
    }
    
    public void setRewardLv(final Integer rewardLv) {
        this.rewardLv = rewardLv;
    }
    
    public String getNotice() {
        return this.notice;
    }
    
    public void setNotice(final String notice) {
        this.notice = notice;
    }
    
    public Integer[] getArmiesId() {
        return this.armyIds;
    }
    
    public void setArmiesId(final Integer[] armyIds) {
        this.armyIds = armyIds;
    }
}
