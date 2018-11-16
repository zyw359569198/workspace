package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class BtSoloReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer num;
    private Integer rewardFood;
    private Integer rewardIron;
    private Integer npcNum;
    private Integer weiNpc;
    private Integer shuNpc;
    private Integer wuNpc;
    private Integer rewardToken;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getRewardFood() {
        return this.rewardFood;
    }
    
    public void setRewardFood(final Integer rewardFood) {
        this.rewardFood = rewardFood;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
    
    public Integer getNpcNum() {
        return this.npcNum;
    }
    
    public void setNpcNum(final Integer npcNum) {
        this.npcNum = npcNum;
    }
    
    public Integer getWeiNpc() {
        return this.weiNpc;
    }
    
    public void setWeiNpc(final Integer weiNpc) {
        this.weiNpc = weiNpc;
    }
    
    public Integer getShuNpc() {
        return this.shuNpc;
    }
    
    public void setShuNpc(final Integer shuNpc) {
        this.shuNpc = shuNpc;
    }
    
    public Integer getWuNpc() {
        return this.wuNpc;
    }
    
    public void setWuNpc(final Integer wuNpc) {
        this.wuNpc = wuNpc;
    }
    
    public Integer getRewardToken() {
        return this.rewardToken;
    }
    
    public void setRewardToken(final Integer rewardToken) {
        this.rewardToken = rewardToken;
    }
}
