package com.reign.kfwd.battle;

import java.util.*;

public class FightResult
{
    static final int STATE_RUNNING = 0;
    static final int STATE_ROUNDFINISHED = 1;
    static final int STATE_BATTLEFINISHED = 2;
    KfwdBattle battle;
    int battleId;
    int attRemainNum;
    int defRemainNum;
    int attKilledForce;
    int defKilledForce;
    boolean isAttWin;
    int state;
    List<Integer> attUpList;
    List<Integer> attDownList;
    List<Integer> defUpList;
    List<Integer> defDownList;
    
    public FightResult() {
        this.battle = null;
        this.attRemainNum = 0;
        this.defRemainNum = 0;
        this.attKilledForce = 0;
        this.defKilledForce = 0;
        this.state = 0;
        this.attUpList = new ArrayList<Integer>();
        this.attDownList = new ArrayList<Integer>();
        this.defUpList = new ArrayList<Integer>();
        this.defDownList = new ArrayList<Integer>();
    }
    
    public boolean getIsAttWin() {
        return this.isAttWin;
    }
    
    public int getAttRemainNum() {
        return this.attRemainNum;
    }
    
    public void setAttRemainNum(final int attRemainNum) {
        this.attRemainNum = attRemainNum;
    }
    
    public int getDefRemainNum() {
        return this.defRemainNum;
    }
    
    public void setDefRemainNum(final int defRemainNum) {
        this.defRemainNum = defRemainNum;
    }
    
    public void setAttWin(final boolean isAttWin) {
        this.isAttWin = isAttWin;
    }
    
    public KfwdBattle getBattle() {
        return this.battle;
    }
    
    public void setBattle(final KfwdBattle battle) {
        this.battle = battle;
    }
    
    public int getAttKilledForce() {
        return this.attKilledForce;
    }
    
    public void setAttKilledForce(final int attKilledForce) {
        this.attKilledForce = attKilledForce;
    }
    
    public int getDefKilledForce() {
        return this.defKilledForce;
    }
    
    public void setDefKilledForce(final int defKilledForce) {
        this.defKilledForce = defKilledForce;
    }
}
