package com.reign.plugin.yx.common;

public class YxTencentPayInfo
{
    private String userId;
    private int playerId;
    private String token;
    private String ts;
    private String goodId;
    private String opneKey;
    private String pf;
    private int isYellowVip;
    private int yellowVipLevel;
    private int playerLv;
    private int consumeLv;
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public void setTs(final String ts) {
        this.ts = ts;
    }
    
    public String getTs() {
        return this.ts;
    }
    
    public void setOpneKey(final String opneKey) {
        this.opneKey = opneKey;
    }
    
    public String getOpneKey() {
        return this.opneKey;
    }
    
    public void setPf(final String pf) {
        this.pf = pf;
    }
    
    public String getPf() {
        return this.pf;
    }
    
    public void setGoodId(final String goodId) {
        this.goodId = goodId;
    }
    
    public String getGoodId() {
        return this.goodId;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setConsumeLv(final int consumeLv) {
        this.consumeLv = consumeLv;
    }
    
    public int getConsumeLv() {
        return this.consumeLv;
    }
    
    public void setIsYellowVip(final int isYellowVip) {
        this.isYellowVip = isYellowVip;
    }
    
    public int getIsYellowVip() {
        return this.isYellowVip;
    }
    
    public void setYellowVipLevel(final int yellowVipLevel) {
        this.yellowVipLevel = yellowVipLevel;
    }
    
    public int getYellowVipLevel() {
        return this.yellowVipLevel;
    }
}
