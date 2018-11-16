package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

public class Armies implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private String name;
    private Integer terrain;
    private Integer terrainEffectType;
    private Integer powerId;
    private Integer pos;
    private Integer chief;
    private String npcs;
    private String reward;
    private String intro;
    private Integer level;
    private Integer foodConsume;
    private Integer expReward;
    private Double extraRewardE;
    private String goldReward;
    private Integer[] armiesId;
    private Map<Integer, BattleDrop> dropMap;
    
    public Armies() {
        this.dropMap = null;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
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
    
    public Integer getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final Integer powerId) {
        this.powerId = powerId;
    }
    
    public Integer getPos() {
        return this.pos;
    }
    
    public void setPos(final Integer pos) {
        this.pos = pos;
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
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public Integer getFoodConsume() {
        return this.foodConsume;
    }
    
    public void setFoodConsume(final Integer foodConsume) {
        this.foodConsume = foodConsume;
    }
    
    public Integer getExpReward() {
        return this.expReward;
    }
    
    public void setExpReward(final Integer expReward) {
        this.expReward = expReward;
    }
    
    public Double getExtraRewardE() {
        return this.extraRewardE;
    }
    
    public void setExtraRewardE(final Double extraRewardE) {
        this.extraRewardE = extraRewardE;
    }
    
    public String getGoldReward() {
        return this.goldReward;
    }
    
    public void setGoldReward(final String goldReward) {
        this.goldReward = goldReward;
    }
    
    public Integer[] getArmiesId() {
        return this.armiesId;
    }
    
    public void setArmiesId(final Integer[] armiesId) {
        this.armiesId = armiesId;
    }
    
    public Map<Integer, BattleDrop> getDropMap() {
        return this.dropMap;
    }
    
    public void setDropMap(final Map<Integer, BattleDrop> dropMap) {
        this.dropMap = dropMap;
    }
}
