package com.reign.gcld.kfwd.dto;

public class KfwdPlayerMatchKeyInfo
{
    int playerId;
    int completeId;
    long key;
    String matchAdress;
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getCompleteId() {
        return this.completeId;
    }
    
    public void setCompleteId(final int completeId) {
        this.completeId = completeId;
    }
    
    public long getKey() {
        return this.key;
    }
    
    public void setKey(final long key) {
        this.key = key;
    }
    
    public String getMatchAdress() {
        return this.matchAdress;
    }
    
    public void setMatchAdress(final String matchAdress) {
        this.matchAdress = matchAdress;
    }
}
