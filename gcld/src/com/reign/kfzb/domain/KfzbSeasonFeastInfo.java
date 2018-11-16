package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;

public class KfzbSeasonFeastInfo implements IModel
{
    private int pk;
    private int seasonId;
    private int pos;
    private int feastNum;
    private int goldAddFeastNum;
    private int goldFeastNum;
    private int weiNum;
    private int shuNum;
    private int wuNum;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public int getFeastNum() {
        return this.feastNum;
    }
    
    public void setFeastNum(final int feastNum) {
        this.feastNum = feastNum;
    }
    
    public int getGoldFeastNum() {
        return this.goldFeastNum;
    }
    
    public void setGoldFeastNum(final int goldFeastNum) {
        this.goldFeastNum = goldFeastNum;
    }
    
    public int getGoldAddFeastNum() {
        return this.goldAddFeastNum;
    }
    
    public void setGoldAddFeastNum(final int goldAddFeastNum) {
        this.goldAddFeastNum = goldAddFeastNum;
    }
    
    public int getWeiNum() {
        return this.weiNum;
    }
    
    public void setWeiNum(final int weiNum) {
        this.weiNum = weiNum;
    }
    
    public int getShuNum() {
        return this.shuNum;
    }
    
    public void setShuNum(final int shuNum) {
        this.shuNum = shuNum;
    }
    
    public int getWuNum() {
        return this.wuNum;
    }
    
    public void setWuNum(final int wuNum) {
        this.wuNum = wuNum;
    }
}
