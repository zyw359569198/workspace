package com.reign.gcld.politics.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerPoliticsEvent implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer politicsEventNum;
    private Date lastEventTime;
    private Integer peopleLoyal;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPoliticsEventNum() {
        return this.politicsEventNum;
    }
    
    public void setPoliticsEventNum(final Integer politicsEventNum) {
        this.politicsEventNum = politicsEventNum;
    }
    
    public Date getLastEventTime() {
        return this.lastEventTime;
    }
    
    public void setLastEventTime(final Date lastEventTime) {
        this.lastEventTime = lastEventTime;
    }
    
    public Integer getPeopleLoyal() {
        return this.peopleLoyal;
    }
    
    public void setPeopleLoyal(final Integer peopleLoyal) {
        this.peopleLoyal = peopleLoyal;
    }
}
