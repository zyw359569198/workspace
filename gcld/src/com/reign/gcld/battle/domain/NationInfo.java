package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class NationInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer forceId;
    private String rankInfo;
    private Integer hzWinNum;
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public String getRankInfo() {
        return this.rankInfo;
    }
    
    public void setRankInfo(final String rankInfo) {
        this.rankInfo = rankInfo;
    }
    
    public Integer getHzWinNum() {
        return this.hzWinNum;
    }
    
    public void setHzWinNum(final Integer hzWinNum) {
        this.hzWinNum = hzWinNum;
    }
}
