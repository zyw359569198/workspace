package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;

public class KfzbBattleWarrior implements IModel
{
    private int pk;
    private Integer competitorId;
    private Integer seasonId;
    private int playerId;
    private String playerName;
    private Integer playerLevel;
    private String gameServer;
    private String serverName;
    private String serverPinyin;
    private String serverId;
    private int nation;
    private String officeName;
    private String pic;
    private int battlePos;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public Integer getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final Integer competitorId) {
        this.competitorId = competitorId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
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
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerPinyin() {
        return this.serverPinyin;
    }
    
    public void setServerPinyin(final String serverPinyin) {
        this.serverPinyin = serverPinyin;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    public int getNation() {
        return this.nation;
    }
    
    public void setNation(final int nation) {
        this.nation = nation;
    }
    
    public String getOfficeName() {
        return this.officeName;
    }
    
    public void setOfficeName(final String officeName) {
        this.officeName = officeName;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public int getBattlePos() {
        return this.battlePos;
    }
    
    public void setBattlePos(final int battlePos) {
        this.battlePos = battlePos;
    }
}
