package com.reign.kfgz.dto.request;

public class KfgzSyncDataParam
{
    private Integer playerId;
    private Integer cId;
    private Long version;
    private Integer gold;
    private Long copper;
    private Long wood;
    private Long food;
    private Long iron;
    private Long exp;
    private Integer recruitToken;
    private Integer phantomCount;
    private int mubing;
    
    public void setVersion(final Long version) {
        this.version = version;
    }
    
    public Long getVersion() {
        return this.version;
    }
    
    public void setGold(final Integer gold) {
        this.gold = gold;
    }
    
    public Integer getGold() {
        return this.gold;
    }
    
    public void setCopper(final Long copper) {
        this.copper = copper;
    }
    
    public Long getCopper() {
        return this.copper;
    }
    
    public void setWood(final Long wood) {
        this.wood = wood;
    }
    
    public Long getWood() {
        return this.wood;
    }
    
    public void setFood(final Long food) {
        this.food = food;
    }
    
    public Long getFood() {
        return this.food;
    }
    
    public void setIron(final Long iron) {
        this.iron = iron;
    }
    
    public Long getIron() {
        return this.iron;
    }
    
    public void setcId(final Integer cId) {
        this.cId = cId;
    }
    
    public Integer getcId() {
        return this.cId;
    }
    
    public void setExp(final Long exp) {
        this.exp = exp;
    }
    
    public Long getExp() {
        return this.exp;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setMubing(final int mubing) {
        this.mubing = mubing;
    }
    
    public int getMubing() {
        return this.mubing;
    }
    
    public void setRecruitToken(final Integer recruitToken) {
        this.recruitToken = recruitToken;
    }
    
    public Integer getRecruitToken() {
        return this.recruitToken;
    }
    
    public void setPhantomCount(final Integer phantomCount) {
        this.phantomCount = phantomCount;
    }
    
    public Integer getPhantomCount() {
        return this.phantomCount;
    }
}
