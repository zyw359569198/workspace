package com.reign.kfgz.comm;

import java.util.*;
import com.reign.kfgz.dto.*;
import com.reign.kf.match.sdata.cache.*;
import com.reign.kf.match.sdata.domain.*;

public class KfPlayerInfo
{
    private Integer competitorId;
    private Integer playerId;
    private String playerName;
    private int playerLevel;
    private String serverName;
    private String serverId;
    private int nation;
    private int forceId;
    private int gzId;
    private String pic;
    private int tech16;
    private int tech40;
    private int tech49;
    private int tech50;
    private int tech39;
    private int tech28;
    private boolean autoStg;
    private long nextSoloTime;
    private int officerId;
    private int officeTokenNum;
    private int buyPhantom15Min;
    private int buyPhantom30Min;
    private Map<Integer, KfGeneralInfo> gMap;
    
    public KfPlayerInfo(final int cId, final int gzId) {
        this.autoStg = false;
        this.buyPhantom15Min = 0;
        this.buyPhantom30Min = 0;
        this.gMap = new HashMap<Integer, KfGeneralInfo>();
        this.competitorId = cId;
        this.gzId = gzId;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public void setgMap(final Map<Integer, KfGeneralInfo> gMap) {
        this.gMap = gMap;
    }
    
    public Map<Integer, KfGeneralInfo> getgMap() {
        return this.gMap;
    }
    
    public Integer getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final Integer competitorId) {
        this.competitorId = competitorId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public Integer getPlayerLevel() {
        return this.playerLevel;
    }
    
    public void setPlayerLevel(final Integer playerLevel) {
        this.playerLevel = playerLevel;
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
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    public boolean isAutoStg() {
        return this.autoStg;
    }
    
    public void setAutoStg(final boolean autoStg) {
        this.autoStg = autoStg;
    }
    
    public void setPlayerLevel(final int playerLevel) {
        this.playerLevel = playerLevel;
    }
    
    public int getTech16() {
        return this.tech16;
    }
    
    public void setTech16(final int tech16) {
        this.tech16 = tech16;
    }
    
    public int getTech40() {
        return this.tech40;
    }
    
    public void setTech40(final int tech40) {
        this.tech40 = tech40;
    }
    
    public long getNextSoloTime() {
        return this.nextSoloTime;
    }
    
    public void setNextSoloTime(final long nextSoloTime) {
        this.nextSoloTime = nextSoloTime;
    }
    
    public long getSoloCD() {
        long cd = this.nextSoloTime - System.currentTimeMillis();
        if (cd < 0L) {
            cd = 0L;
        }
        return cd;
    }
    
    public void addSoloCD(final boolean isAtt) {
        if (isAtt) {
            this.nextSoloTime = System.currentTimeMillis() + 60000L;
        }
        else {
            this.nextSoloTime = System.currentTimeMillis() + 30000L;
        }
    }
    
    public void clearCD() {
        this.nextSoloTime = 0L;
    }
    
    public int getOfficerId() {
        return this.officerId;
    }
    
    public void setOfficerId(final int officerId) {
        this.officerId = officerId;
    }
    
    public int getOfficeTokenNum() {
        return this.officeTokenNum;
    }
    
    public void setOfficeTokenNum(final int officeTokenNum) {
        this.officeTokenNum = officeTokenNum;
    }
    
    public int getTech49() {
        return this.tech49;
    }
    
    public void setTech49(final int tech49) {
        this.tech49 = tech49;
    }
    
    public int getTech50() {
        return this.tech50;
    }
    
    public void setTech50(final int tech50) {
        this.tech50 = tech50;
    }
    
    public int getTech39() {
        return this.tech39;
    }
    
    public void setTech39(final int tech39) {
        this.tech39 = tech39;
    }
    
    public int getBuyPhantom15Min() {
        return this.buyPhantom15Min;
    }
    
    public void setBuyPhantom15Min(final int buyPhantom15Min) {
        this.buyPhantom15Min = buyPhantom15Min;
    }
    
    public int getBuyPhantom30Min() {
        return this.buyPhantom30Min;
    }
    
    public void setBuyPhantom30Min(final int buyPhantom30Min) {
        this.buyPhantom30Min = buyPhantom30Min;
    }
    
    public int getTech28() {
        return this.tech28;
    }
    
    public void setTech28(final int tech28) {
        this.tech28 = tech28;
    }
    
    public int getRemainBuyPhantomTimes(final KfgzBaseInfo baseInfo) {
        final long cd = baseInfo.getEndCD();
        if (cd > 1800000L) {
            return 0;
        }
        if (cd <= 1800000L && cd > 900000L) {
            final int remain = 50 - this.getBuyPhantom15Min();
            return (remain >= 0) ? remain : 0;
        }
        if (cd <= 900000L) {
            final int remain = 50 - this.getBuyPhantom30Min();
            return (remain >= 0) ? remain : 0;
        }
        return 0;
    }
    
    public int getHallOfficeId() {
        final Halls hall = HallsCache.getHallsById(this.officerId);
        if (hall == null) {
            return 0;
        }
        return hall.getOfficialId();
    }
    
    public Halls getHallInfo() {
        final Halls hall = HallsCache.getHallsById(this.officerId);
        return hall;
    }
}
