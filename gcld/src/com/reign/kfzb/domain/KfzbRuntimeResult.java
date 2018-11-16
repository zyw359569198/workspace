package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;

public class KfzbRuntimeResult implements IModel
{
    private int pk;
    private int competitorId;
    int seasonId;
    int layer;
    int round;
    int res;
    int lastres;
    int isfinsh;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getLayer() {
        return this.layer;
    }
    
    public void setLayer(final int layer) {
        this.layer = layer;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public int getRes() {
        return this.res;
    }
    
    public void setRes(final int res) {
        this.res = res;
    }
    
    public int getLastres() {
        return this.lastres;
    }
    
    public void setLastres(final int lastres) {
        this.lastres = lastres;
    }
    
    public int getIsfinsh() {
        return this.isfinsh;
    }
    
    public void setIsfinsh(final int isfinsh) {
        this.isfinsh = isfinsh;
    }
}
