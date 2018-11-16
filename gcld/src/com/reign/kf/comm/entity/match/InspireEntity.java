package com.reign.kf.comm.entity.match;

public class InspireEntity
{
    public static final int ERROR_NO_MATCH = -1;
    public static final int ERROR_ERRORTIME = -2;
    public static final int ERROR_WRONGPLAYER = -3;
    public static final int ERROR_PARAM = -4;
    public static final int ERROR_DUPLICATE_INSPIRE = -5;
    public static final int SUCC = 1;
    private int state;
    private int competitorId;
    private int playerId;
    private int errorCode;
    private String matchTag;
    
    public InspireEntity(final int errorCode) {
        this.state = 2;
        this.errorCode = errorCode;
    }
    
    public InspireEntity() {
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
}
