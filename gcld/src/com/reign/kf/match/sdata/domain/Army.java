package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class Army implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer generalId;
    private String name;
    private Integer generalLv;
    private Integer armyHp;
    private Integer troopHp;
    private String effect;
    private int att;
    private int def;
    private int bd;
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final Integer generalLv) {
        this.generalLv = generalLv;
    }
    
    public Integer getArmyHp() {
        return this.armyHp;
    }
    
    public void setArmyHp(final Integer armyHp) {
        this.armyHp = armyHp;
    }
    
    public Integer getTroopHp() {
        return this.troopHp;
    }
    
    public void setTroopHp(final Integer troopHp) {
        this.troopHp = troopHp;
    }
    
    public String getEffect() {
        return this.effect;
    }
    
    public void setEffect(final String effect) {
        this.effect = effect;
    }
    
    public int getAtt() {
        return this.att;
    }
    
    public void setAtt(final int att) {
        this.att = att;
    }
    
    public int getDef() {
        return this.def;
    }
    
    public void setDef(final int def) {
        this.def = def;
    }
    
    public int getBd() {
        return this.bd;
    }
    
    public void setBd(final int bd) {
        this.bd = bd;
    }
}
