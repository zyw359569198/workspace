package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerArmyReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer powerId;
    private Integer armyId;
    private Integer first;
    private Date expireTime;
    private String npcLost;
    private Integer hp;
    private Integer hpMax;
    private Integer state;
    private Integer buyCount;
    private Integer firstWin;
    private Integer winCount;
    
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
    
    public Integer getFirst() {
        return this.first;
    }
    
    public void setFirst(final Integer first) {
        this.first = first;
    }
    
    public Date getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final Date expireTime) {
        this.expireTime = expireTime;
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
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getBuyCount() {
        return this.buyCount;
    }
    
    public void setBuyCount(final Integer buyCount) {
        this.buyCount = buyCount;
    }
    
    public Integer getFirstWin() {
        return this.firstWin;
    }
    
    public void setFirstWin(final Integer firstWin) {
        this.firstWin = firstWin;
    }
    
    public Integer getWinCount() {
        return this.winCount;
    }
    
    public void setWinCount(final Integer winCount) {
        this.winCount = winCount;
    }
}
