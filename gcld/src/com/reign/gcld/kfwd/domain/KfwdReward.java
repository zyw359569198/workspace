package com.reign.gcld.kfwd.domain;

import com.reign.framework.mybatis.*;
import java.util.*;
import org.apache.commons.lang.*;
import com.reign.kfwd.constants.*;

public class KfwdReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer pk;
    private Integer seasonId;
    private Integer playerId;
    private Integer cid;
    private Integer version;
    private int tickets;
    private String rewardinfo;
    private String dayRanking;
    private int dayReward;
    private String dayRewardTicket;
    private int getTreasure;
    public static final String SPLIT = ",";
    
    public Integer getPk() {
        return this.pk;
    }
    
    public void setPk(final Integer pk) {
        this.pk = pk;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getCid() {
        return this.cid;
    }
    
    public void setCid(final Integer cid) {
        this.cid = cid;
    }
    
    public Integer getVersion() {
        return this.version;
    }
    
    public void setVersion(final Integer version) {
        this.version = version;
    }
    
    public int getTickets() {
        return this.tickets;
    }
    
    public void setTickets(final int tickets) {
        this.tickets = tickets;
    }
    
    public String getRewardinfo() {
        return this.rewardinfo;
    }
    
    public void setRewardinfo(final String rewardinfo) {
        this.rewardinfo = rewardinfo;
    }
    
    public String getDayRanking() {
        return this.dayRanking;
    }
    
    public void setDayRanking(final String dayRanking) {
        this.dayRanking = dayRanking;
    }
    
    public int getDayReward() {
        return this.dayReward;
    }
    
    public void setDayReward(final int dayReward) {
        this.dayReward = dayReward;
    }
    
    public int getGetTreasure() {
        return this.getTreasure;
    }
    
    public void setGetTreasure(final int getTreasure) {
        this.getTreasure = getTreasure;
    }
    
    public String getDayRewardTicket() {
        return this.dayRewardTicket;
    }
    
    public void setDayRewardTicket(final String dayRewardTicket) {
        this.dayRewardTicket = dayRewardTicket;
    }
    
    public int getAllGetedTicket() {
        final String[] ss1 = this.rewardinfo.split(",");
        int allTicket = 0;
        if (this.rewardinfo != null) {
            String[] array;
            for (int length = (array = ss1).length, i = 0; i < length; ++i) {
                final String s = array[i];
                allTicket += Integer.parseInt(s);
            }
        }
        return allTicket;
    }
    
    public List<Integer[]> setNewTicket(String oldRewardInfo, final String newRewardInfo, final long winRes) {
        final List<Integer[]> resList = new ArrayList<Integer[]>();
        if (StringUtils.isBlank(newRewardInfo)) {
            this.tickets = 0;
            return resList;
        }
        if (oldRewardInfo == null) {
            oldRewardInfo = "";
        }
        final String[] ss1 = oldRewardInfo.split(",");
        final String[] ss2 = newRewardInfo.split(",");
        int sslLength = ss1.length;
        if (oldRewardInfo.equals("")) {
            sslLength = 0;
        }
        int addTicket = 0;
        final int[] res = KfwdConstantsAndMethod.getLastWinInfo(winRes);
        for (int rlen = res.length, i = sslLength; i < ss2.length && i < rlen; ++i) {
            final int roundRes = res[i];
            final int round = i + 1;
            addTicket += Integer.parseInt(ss2[i]);
            resList.add(new Integer[] { round, roundRes, Integer.parseInt(ss2[i]) });
        }
        this.tickets += addTicket;
        this.rewardinfo = newRewardInfo;
        return resList;
    }
    
    public List<Integer[]> checkAndSetNewTicket(final int newDayReward, final String newDayRanking, final String dayTicket) {
        final List<Integer[]> resList = new ArrayList<Integer[]>();
        if (newDayReward == this.dayReward) {
            return resList;
        }
        for (int i = 1; i <= KfwdConstantsAndMethod.MAXFIGHTDAY; ++i) {
            final int[] newRes = KfwdConstantsAndMethod.getRewardInfoByDay(i, dayTicket, newDayReward);
            final int[] oldRes = KfwdConstantsAndMethod.getRewardInfoByDay(i, dayTicket, this.dayReward);
            if (newRes[0] != oldRes[0]) {
                if (newRes[0] == 1) {
                    this.dayReward = KfwdConstantsAndMethod.addGetDayReward(i, this.dayReward);
                    final int day = i;
                    this.tickets += newRes[1];
                    resList.add(new Integer[] { day, newRes[1] });
                }
            }
        }
        return resList;
    }
    
    public int[] getLastDayRankingRewardInfo() {
        final int[] oldRes = KfwdConstantsAndMethod.getRewardInfoByDay(KfwdConstantsAndMethod.MAXFIGHTDAY, this.dayRewardTicket, this.dayReward);
        return oldRes;
    }
    
    public int getDay3Ranking() {
        return getDay3RankingByDayRanking(this.dayRanking);
    }
    
    public static int getDay3RankingByDayRanking(final String dayRanking) {
        if (StringUtils.isBlank(dayRanking)) {
            return 0;
        }
        final String[] rs = dayRanking.split(",");
        if (rs.length < 3) {
            return 0;
        }
        final String r3 = rs[2];
        if (r3.matches("\\d+")) {
            return Integer.parseInt(r3);
        }
        return 0;
    }
}
