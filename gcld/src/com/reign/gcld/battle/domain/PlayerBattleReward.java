package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerBattleReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer mExp;
    private String generalIds;
    private Integer gExp;
    private Integer copper;
    private Date gainTime;
    private Integer terrain;
    private Integer type;
    private Integer attLost;
    private Integer defLost;
    private Integer defId;
    private Integer attForceId;
    private Integer defForceId;
    private Integer winSide;
    private String rbReward;
    private Integer maxKillGnum;
    private String bonusDrop;
    
    public PlayerBattleReward() {
        this.bonusDrop = null;
    }
    
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
    
    public Integer getMExp() {
        return this.mExp;
    }
    
    public void setMExp(final Integer mExp) {
        this.mExp = mExp;
    }
    
    public String getGeneralIds() {
        return this.generalIds;
    }
    
    public void setGeneralIds(final String generalIds) {
        this.generalIds = generalIds;
    }
    
    public Integer getGExp() {
        return this.gExp;
    }
    
    public void setGExp(final Integer gExp) {
        this.gExp = gExp;
    }
    
    public Integer getCopper() {
        return this.copper;
    }
    
    public void setCopper(final Integer copper) {
        this.copper = copper;
    }
    
    public Date getGainTime() {
        return this.gainTime;
    }
    
    public void setGainTime(final Date gainTime) {
        this.gainTime = gainTime;
    }
    
    public Integer getTerrain() {
        return this.terrain;
    }
    
    public void setTerrain(final Integer terrain) {
        this.terrain = terrain;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getAttLost() {
        return this.attLost;
    }
    
    public void setAttLost(final Integer attLost) {
        this.attLost = attLost;
    }
    
    public Integer getDefLost() {
        return this.defLost;
    }
    
    public void setDefLost(final Integer defLost) {
        this.defLost = defLost;
    }
    
    public Integer getDefId() {
        return this.defId;
    }
    
    public void setDefId(final Integer defId) {
        this.defId = defId;
    }
    
    public Integer getAttForceId() {
        return this.attForceId;
    }
    
    public void setAttForceId(final Integer attForceId) {
        this.attForceId = attForceId;
    }
    
    public Integer getDefForceId() {
        return this.defForceId;
    }
    
    public void setDefForceId(final Integer defForceId) {
        this.defForceId = defForceId;
    }
    
    public Integer getWinSide() {
        return this.winSide;
    }
    
    public void setWinSide(final Integer winSide) {
        this.winSide = winSide;
    }
    
    public String getRbReward() {
        return this.rbReward;
    }
    
    public void setRbReward(final String rbReward) {
        this.rbReward = rbReward;
    }
    
    public Integer getMaxKillGnum() {
        return this.maxKillGnum;
    }
    
    public void setMaxKillGnum(final Integer maxKillGnum) {
        this.maxKillGnum = maxKillGnum;
    }
    
    public String getBonusDrop() {
        return this.bonusDrop;
    }
    
    public void setBonusDrop(final String bonusDrop) {
        this.bonusDrop = bonusDrop;
    }
}
