package com.reign.kf.match.common.web.session;

import com.reign.kfwd.domain.*;
import com.reign.util.*;

public class PlayerDto extends ConnectorDto
{
    public static final int TYPE_KFWD_PLAYER = 1;
    public static final int TYPE_KFGZ_PLAYER = 2;
    public static final int TYPE_KFZB_PLAYER = 3;
    public static final int TYPE_MASK_LENGTH = 10;
    public static final int TYPE_MASK = 1023;
    private long uuid;
    private int playerType;
    private int competitorId;
    private String serverTag;
    private String serverName;
    private String serverId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private long lastLoginTime;
    private String logStr;
    
    public PlayerDto(final KfwdBattleWarrior bw) {
        this.type = 2;
        this.playerType = 1;
        this.competitorId = bw.getCompetitorId();
        this.lastLoginTime = System.currentTimeMillis();
        this.playerId = bw.getCompetitorId();
        this.playerName = bw.getPlayerName();
        this.playerLv = ((bw.getPlayerLevel() == null) ? 0 : bw.getPlayerLevel());
        this.serverName = bw.getServerName();
        this.serverId = bw.getServerId();
        this.serverTag = bw.getGameServer();
        this.calculateUId();
        this.updateLogStr();
    }
    
    public PlayerDto(final int competitorId, final int pType) {
        this.type = 2;
        this.competitorId = competitorId;
        this.playerType = pType;
        this.lastLoginTime = System.currentTimeMillis();
        this.calculateUId();
        this.updateLogStr();
    }
    
    private void calculateUId() {
        this.uuid = getUIdByCompetitorIdAndPlayerType(this.competitorId, this.playerType);
    }
    
    public static long getUIdByCompetitorIdAndPlayerType(final int competitorId, final int playerType) {
        return competitorId << 10 | playerType;
    }
    
    private void updateLogStr() {
        this.logStr = MessageFormatter.format("{0}#{1}#{2}#{3}#{4}#{5}#{6}", new Object[] { this.competitorId, this.serverName, this.serverTag, this.serverId, this.serverName, this.playerId, this.playerName });
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public String getServerTag() {
        return this.serverTag;
    }
    
    public void setServerTag(final String serverTag) {
        this.serverTag = serverTag;
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
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public long getLastLoginTime() {
        return this.lastLoginTime;
    }
    
    public void setLastLoginTime(final long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public long getUuid() {
        return this.uuid;
    }
    
    public void setUuid(final long uuid) {
        this.uuid = uuid;
    }
    
    public int getPlayerType() {
        return this.playerType;
    }
    
    public void setPlayerType(final int playerType) {
        this.playerType = playerType;
    }
    
    public String getLogStr() {
        return this.logStr;
    }
    
    public void setLogStr(final String logStr) {
        this.logStr = logStr;
    }
    
    @Override
    public String buildLogStr() {
        return this.logStr;
    }
}
