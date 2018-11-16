package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class CityDefenceNpc implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer cityId;
    private Integer playerLv;
    private Integer generalId;
    private Integer generalLv;
    private Integer troopId;
    private Integer strength;
    private Integer leader;
    private Integer att;
    private Integer def;
    private Integer hp;
    private Integer columnNum;
    private Integer attB;
    private Integer defB;
    private Integer tacticAtt;
    private Integer tacticDef;
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
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
