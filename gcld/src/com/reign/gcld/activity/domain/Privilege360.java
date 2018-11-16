package com.reign.gcld.activity.domain;

import com.reign.framework.mybatis.*;

public class Privilege360 implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String status;
    private String title;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
}
