package com.reign.kf.match.common;

import java.io.*;

public class BattleArmy implements Serializable
{
    private static final long serialVersionUID = 777443601060714035L;
    int id;
    int troopHp;
    int position;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final int troopHp) {
        this.troopHp = troopHp;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
}
