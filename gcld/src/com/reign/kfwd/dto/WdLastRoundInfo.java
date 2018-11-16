package com.reign.kfwd.dto;

public class WdLastRoundInfo
{
    private boolean isAttack;
    private int battleRes;
    private int Inspire1;
    private int Inspire2;
    private boolean round3IsAttacker;
    
    public boolean isRound3IsAttacker() {
        return this.round3IsAttacker;
    }
    
    public void setRound3IsAttacker(final boolean round3IsAttacker) {
        this.round3IsAttacker = round3IsAttacker;
    }
    
    public boolean isAttack() {
        return this.isAttack;
    }
    
    public void setAttack(final boolean isAttack) {
        this.isAttack = isAttack;
    }
    
    public int getBattleRes() {
        return this.battleRes;
    }
    
    public void setBattleRes(final int battleRes) {
        this.battleRes = battleRes;
    }
    
    public int getInspire1() {
        return this.Inspire1;
    }
    
    public void setInspire1(final int inspire1) {
        this.Inspire1 = inspire1;
    }
    
    public int getInspire2() {
        return this.Inspire2;
    }
    
    public void setInspire2(final int inspire2) {
        this.Inspire2 = inspire2;
    }
}
