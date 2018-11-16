package com.reign.kf.comm.entity.kfwd.response;

public class KfwdGwScheduleInfoDto
{
    private int pk;
    private int seasonId;
    private int scheduleId;
    private String matchAdress;
    private String matchName;
    private String gameServer;
    private int ruleId;
    private int state;
    public String levelRange;
    public int roundGold;
    public int rewardRule;
    public int levelRangeType;
    
    public KfwdGwScheduleInfoDto() {
        this.state = -1;
    }
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getMatchAdress() {
        return this.matchAdress;
    }
    
    public void setMatchAdress(final String matchAdress) {
        this.matchAdress = matchAdress;
    }
    
    public String getMatchName() {
        return this.matchName;
    }
    
    public void setMatchName(final String matchName) {
        this.matchName = matchName;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getRuleId() {
        return this.ruleId;
    }
    
    public void setRuleId(final int ruleId) {
        this.ruleId = ruleId;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getLevelRange() {
        return this.levelRange;
    }
    
    public void setLevelRange(final String levelRange) {
        this.levelRange = levelRange;
    }
    
    public int getRoundGold() {
        return this.roundGold;
    }
    
    public void setRoundGold(final int roundGold) {
        this.roundGold = roundGold;
    }
    
    public int getRewardRule() {
        return this.rewardRule;
    }
    
    public void setRewardRule(final int rewardRule) {
        this.rewardRule = rewardRule;
    }
    
    public int getLevelRangeType() {
        return this.levelRangeType;
    }
    
    public void setLevelRangeType(final int levelRangeType) {
        this.levelRangeType = levelRangeType;
    }
}
