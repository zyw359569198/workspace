package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class PlayerArmyExtra implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer powerId;
    private Integer armyId;
    private Integer attackable;
    private Integer winNum;
    private Integer attNum;
    private Integer firstWin;
    private Integer firstOpen;
    private String npcLost;
    private Integer hp;
    private Integer hpMax;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final Integer powerId) {
        this.powerId = powerId;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
    }
    
    public Integer getAttackable() {
        return this.attackable;
    }
    
    public void setAttackable(final Integer attackable) {
        this.attackable = attackable;
    }
    
    public Integer getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final Integer winNum) {
        this.winNum = winNum;
    }
    
    public Integer getAttNum() {
        return this.attNum;
    }
    
    public void setAttNum(final Integer attNum) {
        this.attNum = attNum;
    }
    
    public Integer getFirstWin() {
        return this.firstWin;
    }
    
    public void setFirstWin(final Integer firstWin) {
        this.firstWin = firstWin;
    }
    
    public Integer getFirstOpen() {
        return this.firstOpen;
    }
    
    public void setFirstOpen(final Integer firstOpen) {
        this.firstOpen = firstOpen;
    }
    
    public String getNpcLost() {
        return this.npcLost;
    }
    
    public void setNpcLost(final String npcLost) {
        this.npcLost = npcLost;
    }
    
    public Integer getHp() {
        return this.hp;
    }
    
    public void setHp(final Integer hp) {
        this.hp = hp;
    }
    
    public Integer getHpMax() {
        return this.hpMax;
    }
    
    public void setHpMax(final Integer hpMax) {
        this.hpMax = hpMax;
    }
}
