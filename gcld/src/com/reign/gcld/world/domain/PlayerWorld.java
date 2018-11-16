package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class PlayerWorld implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer rewardNum;
    private String attedId;
    private String canAttId;
    private String rewards;
    private String reward;
    private String boxispicked;
    private Integer quizinfo;
    private String npcLostDetail;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getRewardNum() {
        return this.rewardNum;
    }
    
    public void setRewardNum(final Integer rewardNum) {
        this.rewardNum = rewardNum;
    }
    
    public String getAttedId() {
        return this.attedId;
    }
    
    public void setAttedId(final String attedId) {
        this.attedId = attedId;
    }
    
    public String getCanAttId() {
        return this.canAttId;
    }
    
    public void setCanAttId(final String canAttId) {
        this.canAttId = canAttId;
    }
    
    public String getRewards() {
        return this.rewards;
    }
    
    public void setRewards(final String rewards) {
        this.rewards = rewards;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public String getBoxispicked() {
        return this.boxispicked;
    }
    
    public void setBoxispicked(final String boxispicked) {
        this.boxispicked = boxispicked;
    }
    
    public Integer getQuizinfo() {
        return this.quizinfo;
    }
    
    public void setQuizinfo(final Integer quizinfo) {
        this.quizinfo = quizinfo;
    }
    
    public String getNpcLostDetail() {
        return this.npcLostDetail;
    }
    
    public void setNpcLostDetail(final String npcLostDetail) {
        this.npcLostDetail = npcLostDetail;
    }
}
