package com.reign.kfzb.dto.request;

import java.io.*;

public class KfzbPlayerKey implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int seasonId;
    private int competitorId;
    
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
}
