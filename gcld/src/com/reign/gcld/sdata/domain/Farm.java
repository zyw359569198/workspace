package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Farm implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private String name;
    private Integer nationLv;
    private Integer upCopper;
    private Integer foodReward;
    private Integer foodTime;
    private Integer expReward;
    private Integer expTime;
    private Integer expExtra;
    private Integer consumeFood;
    private Integer expExtra2;
    private Integer consumeFood2;
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getNationLv() {
        return this.nationLv;
    }
    
    public void setNationLv(final Integer nationLv) {
        this.nationLv = nationLv;
    }
    
    public Integer getUpCopper() {
        return this.upCopper;
    }
    
    public void setUpCopper(final Integer upCopper) {
        this.upCopper = upCopper;
    }
    
    public Integer getFoodReward() {
        return this.foodReward;
    }
    
    public void setFoodReward(final Integer foodReward) {
        this.foodReward = foodReward;
    }
    
    public Integer getFoodTime() {
        return this.foodTime;
    }
    
    public void setFoodTime(final Integer foodTime) {
        this.foodTime = foodTime;
    }
    
    public Integer getExpReward() {
        return this.expReward;
    }
    
    public void setExpReward(final Integer expReward) {
        this.expReward = expReward;
    }
    
    public Integer getExpTime() {
        return this.expTime;
    }
    
    public void setExpTime(final Integer expTime) {
        this.expTime = expTime;
    }
    
    public Integer getExpExtra() {
        return this.expExtra;
    }
    
    public void setExpExtra(final Integer expExtra) {
        this.expExtra = expExtra;
    }
    
    public Integer getConsumeFood() {
        return this.consumeFood;
    }
    
    public void setConsumeFood(final Integer consumeFood) {
        this.consumeFood = consumeFood;
    }
    
    public Integer getExpExtra2() {
        return this.expExtra2;
    }
    
    public void setExpExtra2(final Integer expExtra2) {
        this.expExtra2 = expExtra2;
    }
    
    public Integer getConsumeFood2() {
        return this.consumeFood2;
    }
    
    public void setConsumeFood2(final Integer consumeFood2) {
        this.consumeFood2 = consumeFood2;
    }
}
