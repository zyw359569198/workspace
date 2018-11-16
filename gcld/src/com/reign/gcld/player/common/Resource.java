package com.reign.gcld.player.common;

public class Resource
{
    private int copper;
    private int wood;
    private int food;
    private int iron;
    private int C;
    private int W;
    private int F;
    private int I;
    private long updateTime;
    private long kfgzVersion;
    
    public Resource() {
    }
    
    public Resource(final int copper, final int wood, final int food, final int iron) {
        this.copper = copper;
        this.wood = wood;
        this.food = food;
        this.iron = iron;
    }
    
    public long getKfgzVersion() {
        return this.kfgzVersion;
    }
    
    public void setKfgzVersion(final long kfgzVersion) {
        this.kfgzVersion = kfgzVersion;
    }
    
    public long getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final long updateTime) {
        this.updateTime = updateTime;
    }
    
    public int getCopper() {
        return this.copper;
    }
    
    public void setCopper(final int copper) {
        this.copper = copper;
    }
    
    public int getWood() {
        return this.wood;
    }
    
    public void setWood(final int wood) {
        this.wood = wood;
    }
    
    public int getFood() {
        return this.food;
    }
    
    public void setFood(final int food) {
        this.food = food;
    }
    
    public int getIron() {
        return this.iron;
    }
    
    public void setIron(final int iron) {
        this.iron = iron;
    }
    
    public int getC() {
        return this.C;
    }
    
    public void setC(final int c) {
        this.C = c;
    }
    
    public int getW() {
        return this.W;
    }
    
    public void setW(final int w) {
        this.W = w;
    }
    
    public int getF() {
        return this.F;
    }
    
    public void setF(final int f) {
        this.F = f;
    }
    
    public int getI() {
        return this.I;
    }
    
    public void setI(final int i) {
        this.I = i;
    }
}
