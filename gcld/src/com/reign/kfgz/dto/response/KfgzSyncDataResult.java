package com.reign.kfgz.dto.response;

import java.util.*;
import com.reign.kf.comm.util.*;

public class KfgzSyncDataResult
{
    private Integer playerId;
    private Integer cid;
    private int gold;
    private long copper;
    private long wood;
    private long food;
    private long iron;
    private int recruitToken;
    private int phantomCount;
    private int exp;
    private List<Tuple<Integer, Integer>> gExp;
    private long versionFrom;
    private long versionTo;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getCid() {
        return this.cid;
    }
    
    public void setCid(final Integer cid) {
        this.cid = cid;
    }
    
    public int getGold() {
        return this.gold;
    }
    
    public void setGold(final int gold) {
        this.gold = gold;
    }
    
    public void setVersionFrom(final long versionFrom) {
        this.versionFrom = versionFrom;
    }
    
    public long getVersionFrom() {
        return this.versionFrom;
    }
    
    public void setVersionTo(final long versionTo) {
        this.versionTo = versionTo;
    }
    
    public long getVersionTo() {
        return this.versionTo;
    }
    
    public void setCopper(final long copper) {
        this.copper = copper;
    }
    
    public long getCopper() {
        return this.copper;
    }
    
    public void setWood(final long wood) {
        this.wood = wood;
    }
    
    public long getWood() {
        return this.wood;
    }
    
    public void setFood(final long food) {
        this.food = food;
    }
    
    public long getFood() {
        return this.food;
    }
    
    public void setIron(final long iron) {
        this.iron = iron;
    }
    
    public long getIron() {
        return this.iron;
    }
    
    public void setExp(final int exp) {
        this.exp = exp;
    }
    
    public int getExp() {
        return this.exp;
    }
    
    public void setRecruitToken(final int recruitToken) {
        this.recruitToken = recruitToken;
    }
    
    public int getRecruitToken() {
        return this.recruitToken;
    }
    
    public void setgExp(final List<Tuple<Integer, Integer>> gExp) {
        this.gExp = gExp;
    }
    
    public List<Tuple<Integer, Integer>> getgExp() {
        return this.gExp;
    }
    
    public void setPhantomCount(final int phantomCount) {
        this.phantomCount = phantomCount;
    }
    
    public int getPhantomCount() {
        return this.phantomCount;
    }
}
