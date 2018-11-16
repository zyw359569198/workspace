package com.reign.gcld.kfwd.manager;

import org.apache.commons.logging.*;
import com.reign.gcld.log.*;

public class KfwdMatch
{
    private static final Log logger;
    int scheduleId;
    int seasonId;
    public String levelRange;
    public int roundGold;
    public int rewardRule;
    public int levelRangeType;
    public String matchAdress;
    public String matchPort;
    
    static {
        logger = new KfwdMatchOperationLogger();
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
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
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public String getMatchAdress() {
        return this.matchAdress;
    }
    
    public void setMatchAdress(final String matchAdress) {
        this.matchAdress = matchAdress;
    }
    
    public String getMatchPort() {
        return this.matchPort;
    }
    
    public void setMatchPort(final String matchPort) {
        this.matchPort = matchPort;
    }
}
