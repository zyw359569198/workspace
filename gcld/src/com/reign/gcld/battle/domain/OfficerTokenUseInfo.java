package com.reign.gcld.battle.domain;

public class OfficerTokenUseInfo implements Comparable<OfficerTokenUseInfo>
{
    private int forceId;
    private int officerId;
    private String battleId;
    private long expireTime;
    private int cityId;
    private int playerId;
    
    public OfficerTokenUseInfo(final int forceId, final int officerId, final String battleId, final long expireTime, final int cityId, final int playerId) {
        this.forceId = forceId;
        this.officerId = officerId;
        this.battleId = battleId;
        this.expireTime = expireTime;
        this.cityId = cityId;
        this.playerId = playerId;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getOfficerId() {
        return this.officerId;
    }
    
    public void setOfficerId(final int officerId) {
        this.officerId = officerId;
    }
    
    public String getBattleId() {
        return this.battleId;
    }
    
    public void setBattleId(final String battleId) {
        this.battleId = battleId;
    }
    
    public long getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final long expireTime) {
        this.expireTime = expireTime;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    @Override
    public int compareTo(final OfficerTokenUseInfo o) {
        if (o == null) {
            return 0;
        }
        if (this.officerId >= o.getOfficerId()) {
            return 1;
        }
        return 0;
    }
}
