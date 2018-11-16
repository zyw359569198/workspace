package com.reign.kfgz.battle;

public class PlayerBattleDropItem
{
    int competitorId;
    String type;
    int gId;
    int value;
    
    public PlayerBattleDropItem(final Integer competitorId, final String key, final int num) {
        this.competitorId = competitorId;
        this.type = key;
        this.value = num;
    }
    
    public PlayerBattleDropItem(final Integer competitorId, final int gId, final String key, final int num) {
        this.competitorId = competitorId;
        this.type = key;
        this.value = num;
        this.gId = gId;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public int getgId() {
        return this.gId;
    }
    
    public void setgId(final int gId) {
        this.gId = gId;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void setValue(final int value) {
        this.value = value;
    }
}
