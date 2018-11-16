package com.reign.gcld.kfgz.service;

public class KfgzSyncResDto
{
    private int gold;
    private long copper;
    private long wood;
    private long food;
    private long iron;
    private int recruitToken;
    private int mubing;
    private int phantomCount;
    
    public void setGold(final int gold) {
        this.gold = gold;
    }
    
    public int getGold() {
        return this.gold;
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
    
    public void setMubing(final int mubing) {
        this.mubing = mubing;
    }
    
    public int getMubing() {
        return this.mubing;
    }
    
    public void setRecruitToken(final int recruitToken) {
        this.recruitToken = recruitToken;
    }
    
    public int getRecruitToken() {
        return this.recruitToken;
    }
    
    public void setPhantomCount(final int phantomCount) {
        this.phantomCount = phantomCount;
    }
    
    public int getPhantomCount() {
        return this.phantomCount;
    }
}
