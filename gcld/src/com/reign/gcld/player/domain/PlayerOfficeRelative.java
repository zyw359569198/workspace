package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerOfficeRelative implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer officerId;
    private Date occupyOfficialTime;
    private Integer highestOfficer;
    private Integer officerNpc;
    private Integer salaryGotToday;
    private Integer lastOfficerId;
    private Date reputationTime;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getOfficerId() {
        return this.officerId;
    }
    
    public void setOfficerId(final Integer officerId) {
        this.officerId = officerId;
    }
    
    public Date getOccupyOfficialTime() {
        return this.occupyOfficialTime;
    }
    
    public void setOccupyOfficialTime(final Date occupyOfficialTime) {
        this.occupyOfficialTime = occupyOfficialTime;
    }
    
    public Integer getHighestOfficer() {
        return this.highestOfficer;
    }
    
    public void setHighestOfficer(final Integer highestOfficer) {
        this.highestOfficer = highestOfficer;
    }
    
    public Integer getOfficerNpc() {
        return this.officerNpc;
    }
    
    public void setOfficerNpc(final Integer officerNpc) {
        this.officerNpc = officerNpc;
    }
    
    public Integer getSalaryGotToday() {
        return this.salaryGotToday;
    }
    
    public void setSalaryGotToday(final Integer salaryGotToday) {
        this.salaryGotToday = salaryGotToday;
    }
    
    public Integer getLastOfficerId() {
        return this.lastOfficerId;
    }
    
    public void setLastOfficerId(final Integer lastOfficerId) {
        this.lastOfficerId = lastOfficerId;
    }
    
    public Date getReputationTime() {
        return this.reputationTime;
    }
    
    public void setReputationTime(final Date reputationTime) {
        this.reputationTime = reputationTime;
    }
}
