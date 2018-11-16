package com.reign.gcld.store.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class StoreHouseSell implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer itemId;
    private Integer type;
    private Integer goodsType;
    private Integer lv;
    private String attribute;
    private Integer quality;
    private Date sellTime;
    private Integer gemId;
    private Integer num;
    private String refreshAttribute;
    private Integer quenchingTimes;
    private Integer quenchingTimesFree;
    private Integer specialSkillId;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
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
    
    public Date getSellTime() {
        return this.sellTime;
    }
    
    public void setSellTime(final Date sellTime) {
        this.sellTime = sellTime;
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
}
