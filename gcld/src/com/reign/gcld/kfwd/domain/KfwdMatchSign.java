package com.reign.gcld.kfwd.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import com.reign.gcld.kfwd.common.*;

public class KfwdMatchSign implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vid;
    private String matchTag;
    private Integer competitorId;
    private Integer playerId;
    private Date createTime;
    private String gIds;
    
    public KfwdMatchSign() {
    }
    
    public KfwdMatchSign(final MatchAttendee matchAttendee) {
        this.vid = 0;
        this.matchTag = matchAttendee.getMatchTag();
        this.competitorId = matchAttendee.getCompetitorId();
        this.playerId = matchAttendee.getPlayerId();
        this.createTime = new Date();
        this.gIds = matchAttendee.getgIds();
    }
    
    public void assign(final MatchAttendee matchAttendee) {
        this.createTime = new Date();
    }
    
    public Integer getVid() {
        return this.vid;
    }
    
    public void setVid(final Integer vid) {
        this.vid = vid;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public Integer getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final Integer competitorId) {
        this.competitorId = competitorId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    public String getGIds() {
        return this.gIds;
    }
    
    public void setGIds(final String gIds) {
        this.gIds = gIds;
    }
}
