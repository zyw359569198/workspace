package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class KfgzGwScheduleInfo implements IModel
{
    private int pk;
    private int seasonId;
    private int layerId;
    private int gzId;
    private String matchAddress;
    private String matchName;
    private String gameServer1;
    private String gameServer2;
    private int nation1;
    private int nation2;
    private String serverName1;
    private String serverName2;
    private Date battleDate;
    private int ruleId;
    private int rewardgId;
    private int round;
    private int gId;
    
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
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public String getMatchAddress() {
        return this.matchAddress;
    }
    
    public void setMatchAddress(final String matchAddress) {
        this.matchAddress = matchAddress;
    }
    
    public String getMatchName() {
        return this.matchName;
    }
    
    public void setMatchName(final String matchName) {
        this.matchName = matchName;
    }
    
    public String getGameServer1() {
        return this.gameServer1;
    }
    
    public void setGameServer1(final String gameServer1) {
        this.gameServer1 = gameServer1;
    }
    
    public String getGameServer2() {
        return this.gameServer2;
    }
    
    public void setGameServer2(final String gameServer2) {
        this.gameServer2 = gameServer2;
    }
    
    public int getNation1() {
        return this.nation1;
    }
    
    public void setNation1(final int nation1) {
        this.nation1 = nation1;
    }
    
    public int getNation2() {
        return this.nation2;
    }
    
    public void setNation2(final int nation2) {
        this.nation2 = nation2;
    }
    
    public Date getBattleDate() {
        return this.battleDate;
    }
    
    public void setBattleDate(final Date battleDate) {
        this.battleDate = battleDate;
    }
    
    public int getRuleId() {
        return this.ruleId;
    }
    
    public void setRuleId(final int ruleId) {
        this.ruleId = ruleId;
    }
    
    public int getRewardgId() {
        return this.rewardgId;
    }
    
    public void setRewardgId(final int rewardgId) {
        this.rewardgId = rewardgId;
    }
    
    public int getLayerId() {
        return this.layerId;
    }
    
    public void setLayerId(final int layerId) {
        this.layerId = layerId;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    public String getServerName1() {
        return this.serverName1;
    }
    
    public void setServerName1(final String serverName1) {
        this.serverName1 = serverName1;
    }
    
    public String getServerName2() {
        return this.serverName2;
    }
    
    public void setServerName2(final String serverName2) {
        this.serverName2 = serverName2;
    }
    
    public int getgId() {
        return this.gId;
    }
    
    public void setgId(final int gId) {
        this.gId = gId;
    }
}
