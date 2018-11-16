package com.reign.kf.match.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;
import java.util.*;

@JdbcEntity
public class MatchPlayer implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    @AutoGenerator
    private int id;
    private int playerId;
    private String playerName;
    private int playerLv;
    private int playerPic;
    private String forceName;
    private int forceId;
    private String serverId;
    private String serverName;
    private int state;
    private String matchTag;
    private int season;
    private String queryCode;
    private String machineId;
    private Date signTime;
    private Date updateTime;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
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
    
    public int getPlayerLv() {
        return this.playerLv;
    }
    
    public void setPlayerLv(final int playerLv) {
        this.playerLv = playerLv;
    }
    
    public int getPlayerPic() {
        return this.playerPic;
    }
    
    public void setPlayerPic(final int playerPic) {
        this.playerPic = playerPic;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public String getForceName() {
        return this.forceName;
    }
    
    public void setForceName(final String forceName) {
        this.forceName = forceName;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public int getSeason() {
        return this.season;
    }
    
    public void setSeason(final int season) {
        this.season = season;
    }
    
    public String getQueryCode() {
        return this.queryCode;
    }
    
    public void setQueryCode(final String queryCode) {
        this.queryCode = queryCode;
    }
    
    public String getMachineId() {
        return this.machineId;
    }
    
    public void setMachineId(final String machineId) {
        this.machineId = machineId;
    }
    
    public Date getSignTime() {
        return this.signTime;
    }
    
    public void setSignTime(final Date signTime) {
        this.signTime = signTime;
    }
    
    public Date getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }
}
