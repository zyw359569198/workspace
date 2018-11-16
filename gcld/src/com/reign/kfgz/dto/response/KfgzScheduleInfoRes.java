package com.reign.kfgz.dto.response;

import com.reign.kf.comm.util.*;
import org.codehaus.jackson.annotate.*;
import java.util.*;
import com.reign.kfgz.constants.*;

@JsonAutoDetect
public class KfgzScheduleInfoRes
{
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
    private int pos1;
    private int pos2;
    private int round;
    
    @JsonIgnore
    public Tuple<String, Integer> getOtherServerName(final String myGameServer, final int nation) {
        final Tuple<String, Integer> result = new Tuple<String, Integer>();
        if (this.gameServer1.equals(myGameServer) && this.nation1 == nation) {
            result.left = this.serverName2;
            result.right = this.nation2;
        }
        if (this.gameServer2.equals(myGameServer) && this.nation2 == nation) {
            result.left = this.serverName1;
            result.right = this.nation1;
        }
        return result;
    }
    
    @JsonIgnore
    public List<Integer> getMyNations(final String myGameServer) {
        final List<Integer> result = new ArrayList<Integer>();
        if (this.gameServer1.equals(myGameServer)) {
            result.add(this.nation1);
        }
        if (this.gameServer2.equals(myGameServer)) {
            result.add(this.nation2);
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object r) {
        if (r instanceof KfgzScheduleInfoRes) {
            final KfgzScheduleInfoRes rr = (KfgzScheduleInfoRes)r;
            return rr.getBattleDate().equals(this.battleDate) && rr.getGameServer1().equals(this.gameServer1) && rr.getGameServer2().equals(this.gameServer2) && rr.getGzId() == this.gzId && rr.getLayerId() == this.layerId && rr.getMatchAddress().equals(this.matchAddress) && rr.getMatchName().equals(this.matchName) && rr.getNation1() == this.nation1 && rr.getNation2() == this.nation2 && rr.getRewardgId() == this.rewardgId && rr.getRound() == this.round && rr.getRuleId() == this.ruleId && rr.getSeasonId() == this.seasonId;
        }
        return false;
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
    
    @JsonIgnore
    public int getGId() {
        return KfgzCommConstants.getGIdByGzID(this.gzId);
    }
    
    @JsonIgnore
    public int getPos1() {
        return this.pos1;
    }
    
    public void setPos1(final int pos1) {
        this.pos1 = pos1;
    }
    
    @JsonIgnore
    public int getPos2() {
        return this.pos2;
    }
    
    public void setPos2(final int pos2) {
        this.pos2 = pos2;
    }
}
