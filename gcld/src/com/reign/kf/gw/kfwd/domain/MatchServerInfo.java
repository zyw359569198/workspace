package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class MatchServerInfo implements IModel
{
    private int pk;
    private int matchId;
    private String matchAdress;
    private int type;
    private String matchName;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public String getMatchAdress() {
        return this.matchAdress;
    }
    
    public void setMatchAdress(final String matchAdress) {
        this.matchAdress = matchAdress;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public String getMatchName() {
        return this.matchName;
    }
    
    public void setMatchName(final String matchName) {
        this.matchName = matchName;
    }
}
