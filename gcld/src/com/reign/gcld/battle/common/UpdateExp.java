package com.reign.gcld.battle.common;

public class UpdateExp
{
    private int curLv;
    private long orgExp;
    private long curExp;
    private int nextExp;
    
    public UpdateExp() {
    }
    
    public UpdateExp(final int curLv, final long orgExp, final long curExp, final int nextExp) {
        this.orgExp = orgExp;
        this.curLv = curLv;
        this.curExp = curExp;
        this.nextExp = nextExp;
    }
    
    public long getOrgExp() {
        return this.orgExp;
    }
    
    public void setOrgExp(final long orgExp) {
        this.orgExp = orgExp;
    }
    
    public void setCurExp(final long curExp) {
        this.curExp = curExp;
    }
    
    public int getCurLv() {
        return this.curLv;
    }
    
    public void setCurLv(final int curLv) {
        this.curLv = curLv;
    }
    
    public long getCurExp() {
        return this.curExp;
    }
    
    public void setCurExp(final int curExp) {
        this.curExp = curExp;
    }
    
    public int getNextExp() {
        return this.nextExp;
    }
    
    public void setNextExp(final int nextExp) {
        this.nextExp = nextExp;
    }
}
