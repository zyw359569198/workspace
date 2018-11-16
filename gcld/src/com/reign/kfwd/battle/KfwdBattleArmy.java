package com.reign.kfwd.battle;

public class KfwdBattleArmy
{
    KfwdCampArmy myCamp;
    int position;
    boolean isGeneralLastArmy;
    int strategy;
    int defaultStrategy;
    boolean choose;
    boolean isFirstAction;
    int special;
    int[] troopHp;
    boolean isTD;
    double TD_defense_e;
    
    public KfwdBattleArmy() {
        this.myCamp = null;
        this.isGeneralLastArmy = false;
        this.choose = false;
        this.isFirstAction = true;
        this.troopHp = new int[3];
        this.isTD = false;
        this.TD_defense_e = 0.0;
    }
    
    public int getSpecial() {
        return this.special;
    }
    
    public void setSpecial(final int special) {
        this.special = special;
    }
    
    public KfwdCampArmy getCampArmy() {
        return this.myCamp;
    }
    
    public void setCampArmy(final KfwdCampArmy myCamp) {
        this.myCamp = myCamp;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public int getStrategy() {
        return this.strategy;
    }
    
    public void setStrategy(final int strategy) {
        this.strategy = strategy;
    }
    
    public boolean isChoose() {
        return this.choose;
    }
    
    public void setChoose(final boolean choose) {
        this.choose = choose;
    }
    
    public int[] getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final int[] troopHp) {
        this.troopHp = troopHp;
    }
    
    public boolean isGeneralLastArmy() {
        return this.isGeneralLastArmy;
    }
    
    public void setGeneralLastArmy(final boolean isGeneralLastArmy) {
        this.isGeneralLastArmy = isGeneralLastArmy;
    }
    
    public int getDefaultStrategy() {
        return this.defaultStrategy;
    }
    
    public void setDefaultStrategy(final int defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
    
    public int getUsedStrategy() {
        if (this.strategy == 0) {
            return this.defaultStrategy;
        }
        return this.strategy;
    }
    
    public boolean isTD() {
        return this.isTD;
    }
    
    public void setTD(final boolean isTD) {
        this.isTD = isTD;
    }
    
    public double getTD_defense_e() {
        return this.TD_defense_e;
    }
    
    public void setTD_defense_e(final double tD_defense_e) {
        this.TD_defense_e = tD_defense_e;
    }
}
