package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class SoloReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer soloId;
    private Integer difficulty;
    private Integer star;
    private String reward;
    private Integer reqTime;
    private Integer teqHyNumber;
    private Integer reqFood;
    private Map<Integer, Integer> rewardMap;
    
    public SoloReward() {
        this.rewardMap = new HashMap<Integer, Integer>();
    }
    
    public Map<Integer, Integer> getRewardMap() {
        return this.rewardMap;
    }
    
    public void setRewardMap(final Map<Integer, Integer> rewardMap) {
        this.rewardMap = rewardMap;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSoloId() {
        return this.soloId;
    }
    
    public void setSoloId(final Integer soloId) {
        this.soloId = soloId;
    }
    
    public Integer getDifficulty() {
        return this.difficulty;
    }
    
    public void setDifficulty(final Integer difficulty) {
        this.difficulty = difficulty;
    }
    
    public Integer getStar() {
        return this.star;
    }
    
    public void setStar(final Integer star) {
        this.star = star;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getReqTime() {
        return this.reqTime;
    }
    
    public void setReqTime(final Integer reqTime) {
        this.reqTime = reqTime;
    }
    
    public Integer getTeqHyNumber() {
        return this.teqHyNumber;
    }
    
    public void setTeqHyNumber(final Integer teqHyNumber) {
        this.teqHyNumber = teqHyNumber;
    }
    
    public Integer getReqFood() {
        return this.reqFood;
    }
    
    public void setReqFood(final Integer reqFood) {
        this.reqFood = reqFood;
    }
}
