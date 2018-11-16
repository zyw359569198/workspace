package com.reign.gcld.store.domain;

import com.reign.framework.mybatis.*;

public class StoreHouseBak implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer suitId;
    private Integer playerId;
    private Integer itemId;
    private Integer type;
    private Integer goodsType;
    private Integer owner;
    private Integer lv;
    private String attribute;
    private Integer quality;
    private Integer gemId;
    private Integer num;
    private Integer state;
    private String refreshAttribute;
    private Integer quenchingTimes;
    private Integer quenchingTimesFree;
    private Integer specialSkillId;
    private Integer suitIndex;
    private Long bindExpireTime;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getSuitId() {
        return this.suitId;
    }
    
    public void setSuitId(final Integer suitId) {
        this.suitId = suitId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getItemId() {
        return this.itemId;
    }
    
    public void setItemId(final Integer itemId) {
        this.itemId = itemId;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getGoodsType() {
        return this.goodsType;
    }
    
    public void setGoodsType(final Integer goodsType) {
        this.goodsType = goodsType;
    }
    
    public Integer getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Integer owner) {
        this.owner = owner;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getAttribute() {
        return this.attribute;
    }
    
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public Integer getGemId() {
        return this.gemId;
    }
    
    public void setGemId(final Integer gemId) {
        this.gemId = gemId;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public String getRefreshAttribute() {
        return this.refreshAttribute;
    }
    
    public void setRefreshAttribute(final String refreshAttribute) {
        this.refreshAttribute = refreshAttribute;
    }
    
    public Integer getQuenchingTimes() {
        return this.quenchingTimes;
    }
    
    public void setQuenchingTimes(final Integer quenchingTimes) {
        this.quenchingTimes = quenchingTimes;
    }
    
    public Integer getQuenchingTimesFree() {
        return this.quenchingTimesFree;
    }
    
    public void setQuenchingTimesFree(final Integer quenchingTimesFree) {
        this.quenchingTimesFree = quenchingTimesFree;
    }
    
    public Integer getSpecialSkillId() {
        return this.specialSkillId;
    }
    
    public void setSpecialSkillId(final Integer specialSkillId) {
        this.specialSkillId = specialSkillId;
    }
    
    public Integer getSuitIndex() {
        return this.suitIndex;
    }
    
    public void setSuitIndex(final Integer suitIndex) {
        this.suitIndex = suitIndex;
    }
    
    public Long getBindExpireTime() {
        return this.bindExpireTime;
    }
    
    public void setBindExpireTime(final Long bindExpireTime) {
        this.bindExpireTime = bindExpireTime;
    }
}
