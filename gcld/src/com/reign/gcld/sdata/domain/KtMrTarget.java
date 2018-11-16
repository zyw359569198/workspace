package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtMrTarget implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer nation;
    private Integer kindomLv;
    private Integer period;
    private Integer soil;
    private Integer stone;
    private Integer lumber;
    private Integer rewardExp;
    private Integer rewardIron;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getNation() {
        return this.nation;
    }
    
    public void setNation(final Integer nation) {
        this.nation = nation;
    }
    
    public Integer getKindomLv() {
        return this.kindomLv;
    }
    
    public void setKindomLv(final Integer kindomLv) {
        this.kindomLv = kindomLv;
    }
    
    public Integer getPeriod() {
        return this.period;
    }
    
    public void setPeriod(final Integer period) {
        this.period = period;
    }
    
    public Integer getSoil() {
        return this.soil;
    }
    
    public void setSoil(final Integer soil) {
        this.soil = soil;
    }
    
    public Integer getStone() {
        return this.stone;
    }
    
    public void setStone(final Integer stone) {
        this.stone = stone;
    }
    
    public Integer getLumber() {
        return this.lumber;
    }
    
    public void setLumber(final Integer lumber) {
        this.lumber = lumber;
    }
    
    public Integer getRewardExp() {
        return this.rewardExp;
    }
    
    public void setRewardExp(final Integer rewardExp) {
        this.rewardExp = rewardExp;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
}
