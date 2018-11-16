package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class LoginReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer totalDay;
    private Integer haveReward;
    private Date todayFirstLoginTime;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getTotalDay() {
        return this.totalDay;
    }
    
    public void setTotalDay(final Integer totalDay) {
        this.totalDay = totalDay;
    }
    
    public Integer getHaveReward() {
        return this.haveReward;
    }
    
    public void setHaveReward(final Integer haveReward) {
        this.haveReward = haveReward;
    }
    
    public Date getTodayFirstLoginTime() {
        return this.todayFirstLoginTime;
    }
    
    public void setTodayFirstLoginTime(final Date todayFirstLoginTime) {
        this.todayFirstLoginTime = todayFirstLoginTime;
    }
}
