package com.reign.gcld.battle.common;

import java.io.*;
import com.reign.gcld.battle.scene.*;

public class BattleArmy implements Serializable
{
    private static final long serialVersionUID = 777443601060714035L;
    CampArmy myCamp;
    int position;
    int strategy;
    boolean choose;
    int special;
    boolean isTD;
    double TD_defense_e;
    int[] troopHp;
    
    public BattleArmy() {
        this.myCamp = null;
        this.isTD = false;
        this.TD_defense_e = 0.0;
        this.troopHp = new int[3];
    }
    
    public int getSpecial() {
        return this.special;
    }
    
    public void setSpecial(final int special) {
        this.special = special;
    }
    
    public boolean isChoose() {
        return this.choose;
    }
    
    public void setChoose(final boolean choose) {
        this.choose = choose;
    }
    
    public CampArmy getCampArmy() {
        return this.myCamp;
    }
    
    public void setCampArmy(final CampArmy myCamp) {
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
    
    public boolean getIsTd() {
        return this.isTD;
    }
    
    public void setIsTd(final boolean isTD) {
        this.isTD = isTD;
    }
    
    public double getTD_defense_e() {
        return this.TD_defense_e;
    }
    
    public void setTD_defense_e(final double TD_defense_e) {
        this.TD_defense_e = TD_defense_e;
    }
    
    public int[] getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final int[] troopHp) {
        this.troopHp = troopHp;
    }
}
