package com.reign.gcld.kfzb.domain;

import com.reign.framework.mybatis.*;

public class KfzbFeast implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer freeCard;
    private Integer goldCard;
    private Integer buyCard;
    private Integer drinkNum;
    private Integer xiaoqian;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getFreeCard() {
        return this.freeCard;
    }
    
    public void setFreeCard(final Integer freeCard) {
        this.freeCard = freeCard;
    }
    
    public Integer getGoldCard() {
        return this.goldCard;
    }
    
    public void setGoldCard(final Integer goldCard) {
        this.goldCard = goldCard;
    }
    
    public Integer getBuyCard() {
        return this.buyCard;
    }
    
    public void setBuyCard(final Integer buyCard) {
        this.buyCard = buyCard;
    }
    
    public Integer getDrinkNum() {
        return this.drinkNum;
    }
    
    public void setDrinkNum(final Integer drinkNum) {
        this.drinkNum = drinkNum;
    }
    
    public Integer getXiaoqian() {
        return this.xiaoqian;
    }
    
    public void setXiaoqian(final Integer xiaoqian) {
        this.xiaoqian = xiaoqian;
    }
}
