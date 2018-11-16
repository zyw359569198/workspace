package com.reign.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import com.reign.kfwd.constants.*;
import org.apache.commons.lang.*;
import com.reign.kfwd.service.*;

public class KfwdTicketReward implements IModel
{
    int seasonId;
    int scheduleId;
    private int competitorId;
    String gameServer;
    String rewardInfo;
    String dayRanking;
    long winRes;
    int dayReward;
    String dayTicket;
    public static final String SPLIT = ",";
    public static String EMPTYRANKINGINFO;
    
    static {
        KfwdTicketReward.EMPTYRANKINGINFO = "";
        for (int i = 0; i < KfwdConstantsAndMethod.MAXFIGHTDAY; ++i) {
            KfwdTicketReward.EMPTYRANKINGINFO = String.valueOf(KfwdTicketReward.EMPTYRANKINGINFO) + "0,";
        }
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
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
    
    public String getRewardInfo() {
        return this.rewardInfo;
    }
    
    public void setRewardInfo(final String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
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
    
    public String getDayTicket() {
        return this.dayTicket;
    }
    
    public void setDayTicket(final String dayTicket) {
        this.dayTicket = dayTicket;
    }
    
    public long getWinRes() {
        return this.winRes;
    }
    
    public void setWinRes(final long winRes) {
        this.winRes = winRes;
    }
    
    public int getRoundTicket(final int round) {
        final String newInfo = this.rewardInfo;
        if (newInfo == null) {
            return 0;
        }
        final String[] ss = newInfo.split(",");
        final int length = ss.length;
        if (round > length) {
            return 0;
        }
        return Integer.parseInt(ss[round - 1]);
    }
    
    public void setRoundTicket(final int round, final int ticket) {
        String newInfo = this.rewardInfo;
        if (newInfo == null) {
            newInfo = "";
        }
        final StringBuilder sb = new StringBuilder();
        final String[] ss = newInfo.split(",");
        final int length = ss.length;
        for (int maxL = Math.max(length - 1, round), i = 0; i < maxL; ++i) {
            if (i == round - 1) {
                sb.append(String.valueOf(ticket));
            }
            else if (i < length) {
                sb.append(ss[i]);
            }
            else {
                sb.append(0);
            }
            sb.append(",");
        }
        this.rewardInfo = sb.toString();
    }
    
    public void addDayRanking(final int day, final int ranking) {
        if (StringUtils.isBlank(this.dayRanking)) {
            this.dayRanking = KfwdTicketReward.EMPTYRANKINGINFO;
        }
        if (StringUtils.isBlank(this.dayTicket)) {
            this.dayTicket = KfwdTicketReward.EMPTYRANKINGINFO;
        }
        final int ticket = KfwdTimeControlService.getTicketByRanking(day, ranking);
        final String[] ss = this.dayRanking.split(",");
        final String[] st = this.dayTicket.split(",");
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < KfwdConstantsAndMethod.MAXFIGHTDAY; ++i) {
            final String s = ss[i];
            final String t = st[i];
            if (i == day - 1) {
                sb.append(ranking);
                sb2.append(ticket);
            }
            else {
                sb.append(s);
                sb2.append(t);
            }
            sb.append(",");
            sb2.append(",");
        }
        this.dayRanking = sb.toString();
        this.dayTicket = sb2.toString();
    }
    
    public int[] getRewardInfoByDay(final int rewardDay) {
        return KfwdConstantsAndMethod.getRewardInfoByDay(rewardDay, this.dayTicket, this.dayReward);
    }
    
    public void addReward(final int day) {
        this.dayReward = KfwdConstantsAndMethod.addGetDayReward(day, this.dayReward);
    }
}
