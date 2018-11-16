package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class KfgzWorldRoad implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer start;
    private Integer end;
    private Integer length;
    private Integer world_id;
    private Integer type;
    private Integer connect_minutes;
    private Integer disconnect_minutes;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setStart(final Integer start) {
        this.start = start;
    }
    
    public Integer getStart() {
        return this.start;
    }
    
    public void setEnd(final Integer end) {
        this.end = end;
    }
    
    public Integer getEnd() {
        return this.end;
    }
    
    public void setLength(final Integer length) {
        this.length = length;
    }
    
    public Integer getLength() {
        return this.length;
    }
    
    public Integer getWorld_id() {
        return this.world_id;
    }
    
    public void setWorld_id(final Integer world_id) {
        this.world_id = world_id;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public int realGetLimitMinutes(final int connect) {
        if (connect == 1) {
            return this.connect_minutes;
        }
        return this.disconnect_minutes;
    }
    
    public void setConnect_minutes(final Integer connect_minutes) {
        this.connect_minutes = connect_minutes;
    }
    
    public Integer getConnect_minutes() {
        return this.connect_minutes;
    }
    
    public void setDisconnect_minutes(final Integer disconnect_minutes) {
        this.disconnect_minutes = disconnect_minutes;
    }
    
    public Integer getDisconnect_minutes() {
        return this.disconnect_minutes;
    }
}
