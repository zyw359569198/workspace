package com.reign.gcld.kfwd.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.kfwd.manager.*;

public class KfwdSignup implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer pk;
    private int seasonId;
    private int playerId;
    private int groupType;
    private Date time;
    private int synced;
    private int state;
    private int version;
    private int competitorId;
    private int scheduleId;
    
    public Integer getPk() {
        return this.pk;
    }
    
    public void setPk(final Integer pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getGroupType() {
        return this.groupType;
    }
    
    public void setGroupType(final int groupType) {
        this.groupType = groupType;
    }
    
    public Date getTime() {
        return this.time;
    }
    
    public void setTime(final Date time) {
        this.time = time;
    }
    
    public int getSynced() {
        return this.synced;
    }
    
    public void setSynced(final int synced) {
        this.synced = synced;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public KfwdSignInfo copyToKfwdSignInfo() {
        final KfwdSignInfo signInfo = new KfwdSignInfo();
        signInfo.setCompletedId(this.competitorId);
        signInfo.setPlayerId(this.playerId);
        signInfo.setScheduleId(this.scheduleId);
        signInfo.setSeasonId(this.seasonId);
        return signInfo;
    }
}
