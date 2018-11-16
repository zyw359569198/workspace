package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldCity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer type;
    private Integer terrain;
    private Integer terrainEffectType;
    private Integer chief;
    private String npcs;
    private Integer output;
    private String pic;
    private String intro;
    private Integer weiArea;
    private Integer shuArea;
    private Integer wuArea;
    private Integer showMask;
    private Integer weiDistance;
    private Integer shuDistance;
    private Integer wuDistance;
    private Integer[] armiesId;
    
    public int getArea(final int forceId) {
        if (forceId == 1) {
            return this.weiArea;
        }
        if (forceId == 2) {
            return this.shuArea;
        }
        if (forceId == 3) {
            return this.wuArea;
        }
        return 30;
    }
    
    public int getDistance(final int forceId) {
        if (forceId == 1) {
            return this.weiDistance;
        }
        if (forceId == 2) {
            return this.shuDistance;
        }
        if (forceId == 3) {
            return this.wuDistance;
        }
        return 30;
    }
    
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
    
    public Integer getOutput() {
        return this.output;
    }
    
    public void setOutput(final Integer output) {
        this.output = output;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getWeiArea() {
        return this.weiArea;
    }
    
    public void setWeiArea(final Integer weiArea) {
        this.weiArea = weiArea;
    }
    
    public Integer getShuArea() {
        return this.shuArea;
    }
    
    public void setShuArea(final Integer shuArea) {
        this.shuArea = shuArea;
    }
    
    public Integer getWuArea() {
        return this.wuArea;
    }
    
    public void setWuArea(final Integer wuArea) {
        this.wuArea = wuArea;
    }
    
    public Integer getShowMask() {
        return this.showMask;
    }
    
    public void setShowMask(final Integer showMask) {
        this.showMask = showMask;
    }
    
    public Integer getWeiDistance() {
        return this.weiDistance;
    }
    
    public void setWeiDistance(final Integer weiDistance) {
        this.weiDistance = weiDistance;
    }
    
    public Integer getShuDistance() {
        return this.shuDistance;
    }
    
    public void setShuDistance(final Integer shuDistance) {
        this.shuDistance = shuDistance;
    }
    
    public Integer getWuDistance() {
        return this.wuDistance;
    }
    
    public void setWuDistance(final Integer wuDistance) {
        this.wuDistance = wuDistance;
    }
    
    public Integer[] getArmiesId() {
        return this.armiesId;
    }
    
    public void setArmiesId(final Integer[] armiesId) {
        this.armiesId = armiesId;
    }
}
