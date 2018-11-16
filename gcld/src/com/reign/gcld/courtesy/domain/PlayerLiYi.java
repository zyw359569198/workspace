package com.reign.gcld.courtesy.domain;

import com.reign.framework.mybatis.*;

public class PlayerLiYi implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer liYiDu;
    private String rewardInfo;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getLiYiDu() {
        return this.liYiDu;
    }
    
    public void setLiYiDu(final Integer liYiDu) {
        this.liYiDu = liYiDu;
    }
    
    public String getRewardInfo() {
        return this.rewardInfo;
    }
    
    public void setRewardInfo(final String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }
}
