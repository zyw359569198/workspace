package com.reign.kfzb.dto.response;

import java.util.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfzbFeastInfo
{
    public static final int STATE_NONE = 0;
    public static final int STATE_BEGIN = 1;
    public static final int STATE_FEAST_END = 2;
    public static final int STATE_END = 3;
    int state;
    int seasonId;
    Date endTime;
    Map<Integer, KfzbFeastOrganizer> map;
    
    public KfzbFeastInfo() {
        this.map = new HashMap<Integer, KfzbFeastOrganizer>();
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public Map<Integer, KfzbFeastOrganizer> getMap() {
        return this.map;
    }
    
    public void setMap(final Map<Integer, KfzbFeastOrganizer> map) {
        this.map = map;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    @JsonIgnore
    public int getRealState() {
        if (this.state == 3) {
            return 0;
        }
        return this.state;
    }
}
