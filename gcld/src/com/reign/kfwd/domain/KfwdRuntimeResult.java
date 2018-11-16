package com.reign.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import com.reign.kfwd.constants.*;
import java.util.*;
import org.apache.commons.lang.*;
import javax.persistence.*;
import com.reign.kfwd.service.*;

public class KfwdRuntimeResult implements IModel
{
    public static final String SPLIT = ",";
    public static final String BATCID_SPLIT = ",";
    public static String EMPTYDAYSCOREINFO;
    int seasonId;
    int scheduleId;
    private int competitorId;
    int winNum;
    long winRes;
    String playerName;
    private String gameServer;
    private String serverName;
    private String serverId;
    private int score;
    private int lastScore;
    private String dayScore;
    int nation;
    private long serverStartTime;
    String lastBatcIds;
    private int plv;
    static final int key = 137;
    static final int key2 = 97;
    public static final Comparator<KfwdRuntimeResult> compare;
    public static final Comparator<KfwdRuntimeResult> compare2;
    
    static {
        KfwdRuntimeResult.EMPTYDAYSCOREINFO = "";
        for (int i = 0; i < KfwdConstantsAndMethod.MAXFIGHTDAY; ++i) {
            KfwdRuntimeResult.EMPTYDAYSCOREINFO = String.valueOf(KfwdRuntimeResult.EMPTYDAYSCOREINFO) + "0,";
        }
        compare = new Comparator<KfwdRuntimeResult>() {
            @Override
            public int compare(final KfwdRuntimeResult r1, final KfwdRuntimeResult r2) {
                if (r1.getScore() > r2.getScore()) {
                    return 1;
                }
                if (r1.getScore() < r2.getScore()) {
                    return -1;
                }
                if (r1.getWinNum() > r2.getWinNum()) {
                    return 1;
                }
                if (r1.getWinNum() < r2.getWinNum()) {
                    return -1;
                }
                if (r1.getWinRes() > r2.getWinRes()) {
                    return 1;
                }
                if (r1.getWinRes() < r2.getWinRes()) {
                    return -1;
                }
                if ((r1.getCompetitorId() * 97 + r1.getSeasonId()) % 137 > (r2.getCompetitorId() * 97 + r2.getSeasonId()) % 137) {
                    return 1;
                }
                if ((r1.getCompetitorId() * 97 + r1.getSeasonId()) % 137 < (r2.getCompetitorId() * 97 + r2.getSeasonId()) % 137) {
                    return -1;
                }
                if (r1.getCompetitorId() > r2.getCompetitorId()) {
                    return 1;
                }
                return -1;
            }
        };
        compare2 = new Comparator<KfwdRuntimeResult>() {
            @Override
            public int compare(final KfwdRuntimeResult r1, final KfwdRuntimeResult r2) {
                if (r1.getScore() > r2.getScore()) {
                    return -1;
                }
                if (r1.getScore() < r2.getScore()) {
                    return 1;
                }
                if (r1.getWinNum() > r2.getWinNum()) {
                    return -1;
                }
                if (r1.getWinNum() < r2.getWinNum()) {
                    return 1;
                }
                if (r1.getWinRes() > r2.getWinRes()) {
                    return -1;
                }
                if (r1.getWinRes() < r2.getWinRes()) {
                    return 1;
                }
                if ((r1.getCompetitorId() * 97 + r1.getSeasonId()) % 137 > (r2.getCompetitorId() * 97 + r2.getSeasonId()) % 137) {
                    return -1;
                }
                if ((r1.getCompetitorId() * 97 + r1.getSeasonId()) % 137 < (r2.getCompetitorId() * 97 + r2.getSeasonId()) % 137) {
                    return 1;
                }
                if (r1.getCompetitorId() > r2.getCompetitorId()) {
                    return -1;
                }
                return 1;
            }
        };
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
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
    
    public long getWinRes() {
        return this.winRes;
    }
    
    public void setWinRes(final long winRes) {
        this.winRes = winRes;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public long getServerStartTime() {
        return this.serverStartTime;
    }
    
    public void setServerStartTime(final long serverStartTime) {
        this.serverStartTime = serverStartTime;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getLastScore() {
        return this.lastScore;
    }
    
    public void setLastScore(final int lastScore) {
        this.lastScore = lastScore;
    }
    
    public String getDayScore() {
        return this.dayScore;
    }
    
    public void setDayScore(final String dayScore) {
        this.dayScore = dayScore;
    }
    
    public String getLastBatcIds() {
        return this.lastBatcIds;
    }
    
    public void setLastBatcIds(final String lastBatcIds) {
        this.lastBatcIds = lastBatcIds;
    }
    
    public int getPlv() {
        return this.plv;
    }
    
    public void setPlv(final int plv) {
        this.plv = plv;
    }
    
    @Transient
    public List<Integer> getLastBattleCIdSet() {
        final ArrayList<Integer> cIdset = new ArrayList<Integer>();
        if (StringUtils.isBlank(this.lastBatcIds)) {
            return cIdset;
        }
        final String[] ss = this.lastBatcIds.split(",");
        String[] array;
        for (int length = (array = ss).length, i = 0; i < length; ++i) {
            final String s = array[i];
            cIdset.add(Integer.valueOf(s));
        }
        return cIdset;
    }
    
    public void addNewLastBattleCIds(final int newPlayerId) {
        if (StringUtils.isBlank(this.lastBatcIds)) {
            this.lastBatcIds = String.valueOf(newPlayerId);
        }
        else {
            this.lastBatcIds = String.valueOf(this.lastBatcIds) + "," + newPlayerId;
        }
    }
    
    public void addScore(final int score, final int round) {
        this.score += score;
        final int dayNum = KfwdTimeControlService.getDayByRound(round);
        this.addDayScore(dayNum, score);
    }
    
    public void addDayScore(final int dayNum, final int score) {
        if (dayNum > KfwdConstantsAndMethod.MAXFIGHTDAY || dayNum <= 0) {
            return;
        }
        if (StringUtils.isBlank(this.dayScore)) {
            this.dayScore = "";
            for (int i = 0; i < KfwdConstantsAndMethod.MAXFIGHTDAY; ++i) {
                this.dayScore = String.valueOf(this.dayScore) + "0,";
            }
        }
        final String[] ss = this.dayScore.split(",");
        final StringBuilder sb = new StringBuilder();
        for (int j = 0; j < ss.length; ++j) {
            final String s = ss[j];
            if (j == dayNum - 1) {
                final int oldScore = Integer.valueOf(s);
                final int newScore = oldScore + score;
                sb.append(newScore);
            }
            else {
                sb.append(s);
            }
            sb.append(",");
        }
        this.dayScore = sb.toString();
    }
}
