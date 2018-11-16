package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.sdata.common.*;
import java.util.*;

public class Troop implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer serial;
    private Integer type;
    private Integer level;
    private String name;
    private Integer openLv;
    private Integer att;
    private Integer def;
    private Integer speed;
    private String terrainSpec;
    private Map<Integer, TroopTerrain> terrains;
    private String drop;
    private String terrainStrategy;
    private BattleDropAnd battleDropAnd;
    private Map<Integer, int[]> strategyMap;
    private Map<Integer, int[]> strategyDefMap;
    private String terrainStrategeDefense;
    private String terrainStrategySpec;
    private List<TerrainStrategySpecDto> tsstList;
    private Integer quality;
    
    public Troop() {
        this.terrains = new HashMap<Integer, TroopTerrain>();
    }
    
    public List<TerrainStrategySpecDto> getTsstList() {
        return this.tsstList;
    }
    
    public void setTsstList(final List<TerrainStrategySpecDto> tsstList) {
        this.tsstList = tsstList;
    }
    
    public Map<Integer, int[]> getStrategyDefMap() {
        return this.strategyDefMap;
    }
    
    public void setStrategyDefMap(final Map<Integer, int[]> strategyDefMap) {
        this.strategyDefMap = strategyDefMap;
    }
    
    public Map<Integer, TroopTerrain> getTerrains() {
        return this.terrains;
    }
    
    public void setTerrains(final Map<Integer, TroopTerrain> terrains) {
        this.terrains = terrains;
    }
    
    public Map<Integer, int[]> getStrategyMap() {
        return this.strategyMap;
    }
    
    public void setStrategyMap(final Map<Integer, int[]> strategyMap) {
        this.strategyMap = strategyMap;
    }
    
    public BattleDropAnd getTroopDrop() {
        return this.battleDropAnd;
    }
    
    public void setTroopDrop(final BattleDropAnd battleDropAnd) {
        this.battleDropAnd = battleDropAnd;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSerial() {
        return this.serial;
    }
    
    public void setSerial(final Integer serial) {
        this.serial = serial;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getOpenLv() {
        return this.openLv;
    }
    
    public void setOpenLv(final Integer openLv) {
        this.openLv = openLv;
    }
    
    public Integer getAtt() {
        return this.att;
    }
    
    public void setAtt(final Integer att) {
        this.att = att;
    }
    
    public Integer getDef() {
        return this.def;
    }
    
    public void setDef(final Integer def) {
        this.def = def;
    }
    
    public Integer getSpeed() {
        return this.speed;
    }
    
    public void setSpeed(final Integer speed) {
        this.speed = speed;
    }
    
    public String getTerrainSpec() {
        return this.terrainSpec;
    }
    
    public void setTerrainSpec(final String terrainSpec) {
        this.terrainSpec = terrainSpec;
    }
    
    public String getDrop() {
        return this.drop;
    }
    
    public void setDrop(final String drop) {
        this.drop = drop;
    }
    
    public String getTerrainStrategy() {
        return this.terrainStrategy;
    }
    
    public void setTerrainStrategy(final String terrainStrategy) {
        this.terrainStrategy = terrainStrategy;
    }
    
    public String getTerrainStrategeDefense() {
        return this.terrainStrategeDefense;
    }
    
    public void setTerrainStrategeDefense(final String terrainStrategeDefense) {
        this.terrainStrategeDefense = terrainStrategeDefense;
    }
    
    public String getTerrainStrategySpec() {
        return this.terrainStrategySpec;
    }
    
    public void setTerrainStrategySpec(final String terrainStrategySpec) {
        this.terrainStrategySpec = terrainStrategySpec;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
}
