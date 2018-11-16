package com.reign.kf.match.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;

@JdbcEntity
public class MatchScore implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    private int competitorId;
    private int winNum;
    private int failNum;
    private String matchTag;
    private String reportId;
    private int score;
    private int totalScore;
    private long updateTime;
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getTotalScore() {
        return this.totalScore;
    }
    
    public void setTotalScore(final int totalScore) {
        this.totalScore = totalScore;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
    
    public int getFailNum() {
        return this.failNum;
    }
    
    public void setFailNum(final int failNum) {
        this.failNum = failNum;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public String getReportId() {
        return this.reportId;
    }
    
    public void setReportId(final String reportId) {
        this.reportId = reportId;
    }
    
    public long getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final long updateTime) {
        this.updateTime = updateTime;
    }
}
