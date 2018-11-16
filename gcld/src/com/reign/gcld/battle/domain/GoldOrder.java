package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class GoldOrder implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer forceId;
    private Integer cityId;
    private Integer num;
    private String battleId;
    private Date startTime;
    private List<Integer> playerIdList;
    
    public GoldOrder() {
        this.playerIdList = new ArrayList<Integer>();
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
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public String getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final String battleId) {
        this.battleId = battleId;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public void setPlayerIdList(final List<Integer> playerIdList) {
        this.playerIdList = playerIdList;
    }
    
    public List<Integer> getPlayerIdList() {
        return this.playerIdList;
    }
}
