package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.battle.common.*;

public class ArmiesReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer terrain;
    private Integer terrainEffectType;
    private Integer powerId;
    private Integer pos;
    private Integer chief;
    private String npcs;
    private Integer[] armiesId;
    private Integer level;
    private String plot;
    private Integer time;
    private String markTrace;
    private Integer goldInit;
    private Integer goldIncrease;
    private String reward;
    private Map<Integer, BattleDrop> dropMap;
    
    public ArmiesReward() {
        this.dropMap = null;
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
    
    public Integer[] getArmiesId() {
        return this.armiesId;
    }
    
    public void setArmiesId(final Integer[] armiesId) {
        this.armiesId = armiesId;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
    
    public String getMarkTrace() {
        return this.markTrace;
    }
    
    public void setMarkTrace(final String markTrace) {
        this.markTrace = markTrace;
    }
    
    public Integer getGoldInit() {
        return this.goldInit;
    }
    
    public void setGoldInit(final Integer goldInit) {
        this.goldInit = goldInit;
    }
    
    public Integer getGoldIncrease() {
        return this.goldIncrease;
    }
    
    public void setGoldIncrease(final Integer goldIncrease) {
        this.goldIncrease = goldIncrease;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Map<Integer, BattleDrop> getDropMap() {
        return this.dropMap;
    }
    
    public void setDropMap(final Map<Integer, BattleDrop> dropMap) {
        this.dropMap = dropMap;
    }
}
