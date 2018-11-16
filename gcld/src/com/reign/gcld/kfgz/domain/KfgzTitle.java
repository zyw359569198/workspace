package com.reign.gcld.kfgz.domain;

import com.reign.framework.mybatis.*;

public class KfgzTitle implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer kfgzSeasonId;
    private String playerName;
    private String title;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getKfgzSeasonId() {
        return this.kfgzSeasonId;
    }
    
    public void setKfgzSeasonId(final Integer kfgzSeasonId) {
        this.kfgzSeasonId = kfgzSeasonId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
}
