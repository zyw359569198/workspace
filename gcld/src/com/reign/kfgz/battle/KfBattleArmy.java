package com.reign.kfgz.battle;

import com.reign.kfgz.comm.*;

public class KfBattleArmy
{
    KfCampArmy kfCampArmy;
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
    
    public KfBattleArmy() {
        this.kfCampArmy = null;
        this.isGeneralLastArmy = false;
        this.choose = false;
        this.isFirstAction = true;
        this.troopHp = new int[3];
        this.isTD = false;
        this.TD_defense_e = 0.0;
    }
    
    public KfCampArmy getCampArmy() {
        return this.kfCampArmy;
    }
    
    public void setCampArmy(final KfCampArmy kfCampArmy) {
        this.kfCampArmy = kfCampArmy;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public boolean isGeneralLastArmy() {
        return this.isGeneralLastArmy;
    }
    
    public void setGeneralLastArmy(final boolean isGeneralLastArmy) {
        this.isGeneralLastArmy = isGeneralLastArmy;
    }
    
    public int getStrategy() {
        return this.strategy;
    }
    
    public void setStrategy(final int strategy) {
        this.strategy = strategy;
    }
    
    public int getDefaultStrategy() {
        return this.defaultStrategy;
    }
    
    public void setDefaultStrategy(final int defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
    
    public boolean isChoose() {
        return this.choose;
    }
    
    public void setChoose(final boolean choose) {
        this.choose = choose;
    }
    
    public boolean isFirstAction() {
        return this.isFirstAction;
    }
    
    public void setFirstAction(final boolean isFirstAction) {
        this.isFirstAction = isFirstAction;
    }
    
    public int getSpecial() {
        return this.special;
    }
    
    public void setSpecial(final int special) {
        this.special = special;
    }
    
    public int[] getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final int[] troopHp) {
        this.troopHp = troopHp;
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
