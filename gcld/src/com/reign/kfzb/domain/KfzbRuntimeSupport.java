package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;

public class KfzbRuntimeSupport implements IModel
{
    int pk;
    int seasonId;
    int matchId;
    int cId1;
    int cId2;
    int supportNum1;
    int supportNum2;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getSupportNum1() {
        return this.supportNum1;
    }
    
    public void setSupportNum1(final int supportNum1) {
        this.supportNum1 = supportNum1;
    }
    
    public int getSupportNum2() {
        return this.supportNum2;
    }
    
    public void setSupportNum2(final int supportNum2) {
        this.supportNum2 = supportNum2;
    }
    
    public int getcId1() {
        return this.cId1;
    }
    
    public void setcId1(final int cId1) {
        this.cId1 = cId1;
    }
    
    public int getcId2() {
        return this.cId2;
    }
    
    public void setcId2(final int cId2) {
        this.cId2 = cId2;
    }
}
