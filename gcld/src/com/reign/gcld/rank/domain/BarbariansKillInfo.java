package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class BarbariansKillInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerid;
    private Integer forceid;
    private Integer killnum;
    private Integer isrewarder;
    
    public Integer getPlayerid() {
        return this.playerid;
    }
    
    public void setPlayerid(final Integer playerid) {
        this.playerid = playerid;
    }
    
    public Integer getForceid() {
        return this.forceid;
    }
    
    public void setForceid(final Integer forceid) {
        this.forceid = forceid;
    }
    
    public Integer getKillnum() {
        return this.killnum;
    }
    
    public void setKillnum(final Integer killnum) {
        this.killnum = killnum;
    }
    
    public Integer getIsrewarder() {
        return this.isrewarder;
    }
    
    public void setIsrewarder(final Integer isrewarder) {
        this.isrewarder = isrewarder;
    }
}
