package com.reign.kfwd.dto;

public class RewardInfo
{
    int basicScore;
    int winCoef;
    
    public RewardInfo(final int basicScore2, final int winCoef2) {
        this.basicScore = basicScore2;
        this.winCoef = winCoef2;
    }
    
    public int getBasicScore() {
        return this.basicScore;
    }
    
    public void setBasicScore(final int basicScore) {
        this.basicScore = basicScore;
    }
    
    public int getWinCoef() {
        return this.winCoef;
    }
    
    public void setWinCoef(final int winCoef) {
        this.winCoef = winCoef;
    }
}
