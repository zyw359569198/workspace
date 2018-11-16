package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class SoloCity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer soloId;
    private String name;
    private Integer capital;
    private Integer belong;
    private Integer terrain;
    private Integer terrainEffectType;
    private String npcs1;
    private String npcs2;
    private String npcs3;
    private String npcs4;
    private String npcs5;
    private Map<Integer, List<Integer>> npcListMap;
    
    public SoloCity() {
        this.npcListMap = null;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSoloId() {
        return this.soloId;
    }
    
    public void setSoloId(final Integer soloId) {
        this.soloId = soloId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getCapital() {
        return this.capital;
    }
    
    public void setCapital(final Integer capital) {
        this.capital = capital;
    }
    
    public Integer getBelong() {
        return this.belong;
    }
    
    public void setBelong(final Integer belong) {
        this.belong = belong;
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
    
    public String getNpcs1() {
        return this.npcs1;
    }
    
    public void setNpcs1(final String npcs1) {
        this.npcs1 = npcs1;
    }
    
    public String getNpcs2() {
        return this.npcs2;
    }
    
    public void setNpcs2(final String npcs2) {
        this.npcs2 = npcs2;
    }
    
    public String getNpcs3() {
        return this.npcs3;
    }
    
    public void setNpcs3(final String npcs3) {
        this.npcs3 = npcs3;
    }
    
    public String getNpcs4() {
        return this.npcs4;
    }
    
    public void setNpcs4(final String npcs4) {
        this.npcs4 = npcs4;
    }
    
    public String getNpcs5() {
        return this.npcs5;
    }
    
    public void setNpcs5(final String npcs5) {
        this.npcs5 = npcs5;
    }
    
    public Map<Integer, List<Integer>> getNpcListMap() {
        return this.npcListMap;
    }
    
    public void setNpcListMap(final Map<Integer, List<Integer>> npcListMap) {
        this.npcListMap = npcListMap;
    }
}
