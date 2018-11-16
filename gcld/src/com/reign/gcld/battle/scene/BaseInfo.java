package com.reign.gcld.battle.scene;

import java.io.*;

public class BaseInfo implements Serializable
{
    private static final long serialVersionUID = 6735848127621703047L;
    int id;
    int forceId;
    int defChiefId;
    int num;
    int allNum;
    int npcNum;
    int curArmyId;
    boolean foreWin;
    int id2;
    int initNum;
    
    public BaseInfo() {
        this.num = 0;
        this.allNum = 0;
        this.npcNum = 0;
        this.curArmyId = 0;
    }
    
    public int getId2() {
        return this.id2;
    }
    
    public void setId2(final int id2) {
        this.id2 = id2;
    }
    
    public boolean isForeWin() {
        return this.foreWin;
    }
    
    public void setForeWin(final boolean foreWin) {
        this.foreWin = foreWin;
    }
    
    public int getCurArmyId() {
        return this.curArmyId;
    }
    
    public void setCurArmyId(final int curArmyId) {
        this.curArmyId = curArmyId;
    }
    
    public int getDefChiefId() {
        return this.defChiefId;
    }
    
    public void setDefChiefId(final int defChiefId) {
        this.defChiefId = defChiefId;
    }
    
    public int getNpcNum() {
        return this.npcNum;
    }
    
    public void setNpcNum(final int npcNum) {
        this.npcNum = npcNum;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getNum() {
        return this.num;
    }
    
    public void setNum(final int num) {
        this.num = num;
    }
    
    public int getAllNum() {
        return this.allNum;
    }
    
    public void setAllNum(final int allNum) {
        this.allNum = allNum;
    }
}
