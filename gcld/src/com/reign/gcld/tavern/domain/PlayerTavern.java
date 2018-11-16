package com.reign.gcld.tavern.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerTavern implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer tavernState;
    private Integer civilRefreshTime;
    private Integer militaryRefreshTime;
    private Date nextCivilDate;
    private Date nextMilitaryDate;
    private String lockGeneralId;
    private String militaryInfo;
    private String civilInfo;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getTavernState() {
        return this.tavernState;
    }
    
    public void setTavernState(final Integer tavernState) {
        this.tavernState = tavernState;
    }
    
    public Integer getCivilRefreshTime() {
        return this.civilRefreshTime;
    }
    
    public void setCivilRefreshTime(final Integer civilRefreshTime) {
        this.civilRefreshTime = civilRefreshTime;
    }
    
    public Integer getMilitaryRefreshTime() {
        return this.militaryRefreshTime;
    }
    
    public void setMilitaryRefreshTime(final Integer militaryRefreshTime) {
        this.militaryRefreshTime = militaryRefreshTime;
    }
    
    public Date getNextCivilDate() {
        return this.nextCivilDate;
    }
    
    public void setNextCivilDate(final Date nextCivilDate) {
        this.nextCivilDate = nextCivilDate;
    }
    
    public Date getNextMilitaryDate() {
        return this.nextMilitaryDate;
    }
    
    public void setNextMilitaryDate(final Date nextMilitaryDate) {
        this.nextMilitaryDate = nextMilitaryDate;
    }
    
    public String getLockGeneralId() {
        return this.lockGeneralId;
    }
    
    public void setLockGeneralId(final String lockGeneralId) {
        this.lockGeneralId = lockGeneralId;
    }
    
    public String getMilitaryInfo() {
        return this.militaryInfo;
    }
    
    public void setMilitaryInfo(final String militaryInfo) {
        this.militaryInfo = militaryInfo;
    }
    
    public String getCivilInfo() {
        return this.civilInfo;
    }
    
    public void setCivilInfo(final String civilInfo) {
        this.civilInfo = civilInfo;
    }
}
