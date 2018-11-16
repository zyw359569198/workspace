package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerGeneralMilitaryPhantom implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Date buyTime;
    private Integer playerId;
    private Integer forceId;
    private Integer playerLv;
    private Integer generalId;
    private Integer generalLv;
    private Integer locationId;
    private Integer troopId;
    private Integer strength;
    private Integer leader;
    private Integer att;
    private Integer def;
    private Integer hp;
    private Integer hpMax;
    private Integer columnNum;
    private Integer attB;
    private Integer defB;
    private Integer tacticAtt;
    private Integer tacticDef;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Date getBuyTime() {
        return this.buyTime;
    }
    
    public void setBuyTime(final Date buyTime) {
        this.buyTime = buyTime;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final Integer playerLv) {
        this.playerLv = playerLv;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final Integer generalLv) {
        this.generalLv = generalLv;
    }
    
    public Integer getLocationId() {
        return this.locationId;
    }
    
    public void setLocationId(final Integer locationId) {
        this.locationId = locationId;
    }
    
    public Integer getTroopId() {
        return this.troopId;
    }
    
    public void setTroopId(final Integer troopId) {
        this.troopId = troopId;
    }
    
    public Integer getStrength() {
        return this.strength;
    }
    
    public void setStrength(final Integer strength) {
        this.strength = strength;
    }
    
    public Integer getLeader() {
        return this.leader;
    }
    
    public void setLeader(final Integer leader) {
        this.leader = leader;
    }
    
    public Integer getAtt() {
        return this.att;
    }
    
    public void setAtt(final Integer att) {
        this.att = att;
    }
    
    public Integer getDef() {
        return this.def;
    }
    
    public void setDef(final Integer def) {
        this.def = def;
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
    
    public Integer getColumnNum() {
        return this.columnNum;
    }
    
    public void setColumnNum(final Integer columnNum) {
        this.columnNum = columnNum;
    }
    
    public Integer getAttB() {
        return this.attB;
    }
    
    public void setAttB(final Integer attB) {
        this.attB = attB;
    }
    
    public Integer getDefB() {
        return this.defB;
    }
    
    public void setDefB(final Integer defB) {
        this.defB = defB;
    }
    
    public Integer getTacticAtt() {
        return this.tacticAtt;
    }
    
    public void setTacticAtt(final Integer tacticAtt) {
        this.tacticAtt = tacticAtt;
    }
    
    public Integer getTacticDef() {
        return this.tacticDef;
    }
    
    public void setTacticDef(final Integer tacticDef) {
        this.tacticDef = tacticDef;
    }
}
