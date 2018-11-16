package com.reign.kfgz.dto.response;

public class KfgzSignResult
{
    private int state;
    private Integer playerId;
    private Integer competitor;
    private Integer forceId;
    private int worldId;
    private long version;
    private String[] messages;
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getCompetitor() {
        return this.competitor;
    }
    
    public void setCompetitor(final Integer competitor) {
        this.competitor = competitor;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setVersion(final long version) {
        this.version = version;
    }
    
    public long getVersion() {
        return this.version;
    }
    
    public void setMessages(final String[] messages) {
        this.messages = messages;
    }
    
    public String[] getMessages() {
        return this.messages;
    }
    
    public void setWorldId(final int worldId) {
        this.worldId = worldId;
    }
    
    public int getWorldId() {
        return this.worldId;
    }
}
