package com.reign.gcld.score.domain;

import com.reign.framework.mybatis.*;

public class PlayerScoreRank implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer occupyNum;
    private Integer occupy;
    private Integer assistNum;
    private Integer assist;
    private Integer cheerNum;
    private Integer cheer;
    private Integer score;
    private Integer score2;
    private Integer lastRank;
    private Integer received;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getOccupyNum() {
        return this.occupyNum;
    }
    
    public void setOccupyNum(final Integer occupyNum) {
        this.occupyNum = occupyNum;
    }
    
    public Integer getOccupy() {
        return this.occupy;
    }
    
    public void setOccupy(final Integer occupy) {
        this.occupy = occupy;
    }
    
    public Integer getAssistNum() {
        return this.assistNum;
    }
    
    public void setAssistNum(final Integer assistNum) {
        this.assistNum = assistNum;
    }
    
    public Integer getAssist() {
        return this.assist;
    }
    
    public void setAssist(final Integer assist) {
        this.assist = assist;
    }
    
    public Integer getCheerNum() {
        return this.cheerNum;
    }
    
    public void setCheerNum(final Integer cheerNum) {
        this.cheerNum = cheerNum;
    }
    
    public Integer getCheer() {
        return this.cheer;
    }
    
    public void setCheer(final Integer cheer) {
        this.cheer = cheer;
    }
    
    public Integer getScore() {
        return this.score;
    }
    
    public void setScore(final Integer score) {
        this.score = score;
    }
    
    public Integer getScore2() {
        return this.score2;
    }
    
    public void setScore2(final Integer score2) {
        this.score2 = score2;
    }
    
    public Integer getLastRank() {
        return this.lastRank;
    }
    
    public void setLastRank(final Integer lastRank) {
        this.lastRank = lastRank;
    }
    
    public Integer getReceived() {
        return this.received;
    }
    
    public void setReceived(final Integer received) {
        this.received = received;
    }
}
