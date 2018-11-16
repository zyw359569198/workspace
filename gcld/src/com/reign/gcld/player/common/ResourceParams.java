package com.reign.gcld.player.common;

public class ResourceParams
{
    public int playerId;
    public int copper;
    public int wood;
    public int food;
    public int iron;
    public int maxC;
    public int maxW;
    public int maxF;
    public int maxI;
    public boolean canMinus;
    public boolean isCareMax;
    public Object attribute;
    public boolean joinActivity;
    public boolean updateTime;
    public boolean isKF;
    public long fromVersion;
    public long toVersion;
    
    public ResourceParams() {
        this.playerId = 0;
        this.copper = 0;
        this.wood = 0;
        this.food = 0;
        this.iron = 0;
        this.maxC = 0;
        this.maxW = 0;
        this.maxF = 0;
        this.maxI = 0;
        this.canMinus = false;
        this.isCareMax = false;
        this.attribute = "";
        this.joinActivity = false;
        this.updateTime = false;
        this.isKF = false;
        this.fromVersion = 0L;
        this.toVersion = 0L;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
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
    
    public int getMaxC() {
        return this.maxC;
    }
    
    public void setMaxC(final int maxC) {
        this.maxC = maxC;
    }
    
    public int getMaxW() {
        return this.maxW;
    }
    
    public void setMaxW(final int maxW) {
        this.maxW = maxW;
    }
    
    public int getMaxF() {
        return this.maxF;
    }
    
    public void setMaxF(final int maxF) {
        this.maxF = maxF;
    }
    
    public int getMaxI() {
        return this.maxI;
    }
    
    public void setMaxI(final int maxI) {
        this.maxI = maxI;
    }
    
    public boolean isCanMinus() {
        return this.canMinus;
    }
    
    public void setCanMinus(final boolean canMinus) {
        this.canMinus = canMinus;
    }
    
    public boolean isCareMax() {
        return this.isCareMax;
    }
    
    public void setCareMax(final boolean isCareMax) {
        this.isCareMax = isCareMax;
    }
    
    public Object getAttribute() {
        return this.attribute;
    }
    
    public void setAttribute(final Object attribute) {
        this.attribute = attribute;
    }
    
    public boolean isJoinActivity() {
        return this.joinActivity;
    }
    
    public void setJoinActivity(final boolean joinActivity) {
        this.joinActivity = joinActivity;
    }
    
    public boolean isUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final boolean updateTime) {
        this.updateTime = updateTime;
    }
    
    public boolean isKF() {
        return this.isKF;
    }
    
    public void setKF(final boolean isKF) {
        this.isKF = isKF;
    }
    
    public long getFromVersion() {
        return this.fromVersion;
    }
    
    public void setFromVersion(final long fromVersion) {
        this.fromVersion = fromVersion;
    }
    
    public long getToVersion() {
        return this.toVersion;
    }
    
    public void setToVersion(final long toVersion) {
        this.toVersion = toVersion;
    }
}
