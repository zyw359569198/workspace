package com.reign.gcld.kfwd.common;

import com.reign.gcld.player.domain.*;
import com.reign.gcld.kfwd.domain.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.gcld.common.*;
import com.reign.kf.comm.param.match.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;

public class MatchAttendee
{
    private int competitorId;
    private int matchId;
    private String matchTag;
    private int playerId;
    private String playerName;
    private int playerLv;
    private int playerPic;
    private int forceId;
    private String serverId;
    private String serverName;
    private String gIds;
    private int winTimes;
    private int lossTimes;
    private int seq;
    private MatchFight matchFight;
    private boolean lvPunishment;
    private int rewardMode;
    private int points;
    private int inspireTimes;
    
    public MatchAttendee(final Match match, final Player player) {
        this.rewardMode = 0;
        this.points = 0;
        this.inspireTimes = 0;
        this.competitorId = 0;
        this.matchId = match.getMatchId();
        this.matchTag = match.getMatchTag();
        this.playerId = player.getPlayerId();
        this.playerName = player.getPlayerName();
        this.playerLv = player.getPlayerLv();
        this.playerPic = player.getPic();
        this.forceId = player.getForceId();
        this.serverId = Configuration.getProperty(player.getYx(), "gcld.serverid");
        this.serverName = Configuration.getProperty(player.getYx(), "gcld.servername");
        this.winTimes = 0;
        this.lossTimes = 0;
        this.seq = 0;
        this.lvPunishment = false;
        this.rewardMode = 0;
        this.inspireTimes = 0;
    }
    
    public MatchAttendee(final KfwdMatchSign worldMatchSign, final Player player) {
        this.rewardMode = 0;
        this.points = 0;
        this.inspireTimes = 0;
        this.competitorId = worldMatchSign.getCompetitorId();
        this.matchId = 0;
        this.matchTag = worldMatchSign.getMatchTag();
        this.playerId = player.getPlayerId();
        this.playerName = player.getPlayerName();
        this.playerLv = player.getPlayerLv();
        this.playerPic = player.getPic();
        this.forceId = player.getForceId();
        this.serverId = Configuration.getProperty(player.getYx(), "gcld.serverid");
        this.serverName = Configuration.getProperty(player.getYx(), "gcld.servername");
        this.winTimes = 0;
        this.lossTimes = 0;
        this.seq = 0;
        this.lvPunishment = false;
        this.rewardMode = 0;
        this.gIds = worldMatchSign.getGIds();
    }
    
    public synchronized void assign(final Player player, final String gIds, final boolean lvPunishment) {
        this.playerLv = player.getPlayerLv();
        this.gIds = gIds;
        this.lvPunishment = lvPunishment;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public int getPlayerPic() {
        return this.playerPic;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public int getWinTimes() {
        return this.winTimes;
    }
    
    public void setWinTimes(final int winTimes) {
        this.winTimes = winTimes;
    }
    
    public int getLossTimes() {
        return this.lossTimes;
    }
    
    public void setLossTimes(final int lossTimes) {
        this.lossTimes = lossTimes;
    }
    
    public MatchFight getMatchFight() {
        return this.matchFight;
    }
    
    public void setMatchFight(final MatchFight matchFight) {
        this.matchFight = matchFight;
    }
    
    public int getSeq() {
        return this.seq;
    }
    
    public void setSeq(final int seq) {
        this.seq = seq;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public void addPoints(final int points) {
        this.points += points;
    }
    
    public void minusPoints(final int points) {
        this.points -= points;
    }
    
    public void setPoints(final int points) {
        this.points = points;
    }
    
    public int getRewardMode() {
        return this.rewardMode;
    }
    
    public void setRewardMode(final int rewardMode) {
        this.rewardMode = rewardMode;
    }
    
    public boolean isLvPunishment() {
        return this.lvPunishment;
    }
    
    public String getgIds() {
        return this.gIds;
    }
    
    public void setgIds(final String gIds) {
        this.gIds = gIds;
    }
    
    public int getInspireTimes() {
        return this.inspireTimes;
    }
    
    public void setInspireTimes(final int inspireTimes) {
        this.inspireTimes = inspireTimes;
    }
    
    public synchronized void setResult(final MatchResultEntity matchResultEntity) {
        this.winTimes = matchResultEntity.getWinNum();
        this.lossTimes = matchResultEntity.getFailNum();
        this.seq = matchResultEntity.getRank();
    }
    
    public SignAndSyncParam buildParam(final IDataGetter dataGetter, final String gIds) {
        final SignAndSyncParam param = new SignAndSyncParam();
        try {
            param.setCompetitorId(this.competitorId);
            param.setForceName(WorldCityCommon.nationIdNameMap.get(this.forceId));
            if (this.matchFight == null) {
                param.setMatchId(0);
            }
            else {
                param.setMatchId(this.matchFight.getMatchId());
            }
            param.setMatchTag(this.matchTag);
            param.setPlayerId(this.playerId);
            param.setPlayerLv(this.playerLv);
            param.setPlayerName(this.playerName);
            param.setPlayerPic(this.playerPic);
            param.setServerId(this.serverId);
            param.setServerName(this.serverName);
            param.setForceId(this.forceId);
            param.setCampInfo(Types.OBJECT_MAPPER.writeValueAsBytes(dataGetter.getBattleService().getKfCampDatas(this.playerId, gIds)));
        }
        catch (Exception ex) {}
        return param;
    }
}
