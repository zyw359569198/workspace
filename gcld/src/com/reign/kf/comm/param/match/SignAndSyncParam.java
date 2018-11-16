package com.reign.kf.comm.param.match;

import com.reign.kf.comm.protocol.*;
import org.codehaus.jackson.*;
import java.io.*;

public class SignAndSyncParam
{
    private int matchId;
    private String matchTag;
    private int competitorId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private String forceName;
    private int forceId;
    private int playerPic;
    private String serverName;
    private String serverId;
    private byte[] campInfo;
    private CampArmyParam[] campDatas;
    
    public void setCampList() throws JsonProcessingException, IOException {
        this.campDatas = (CampArmyParam[])Types.objectReader(Types.JAVATYPE_CAMPARMYDATALIST).readValue(this.getCampInfo());
    }
    
    public byte[] getCampInfo() {
        return this.campInfo;
    }
    
    public void setCampInfo(final byte[] campInfo) {
        this.campInfo = campInfo;
    }
    
    public CampArmyParam[] getCampDatas() {
        return this.campDatas;
    }
    
    public void setCampDatas(final CampArmyParam[] campDatas) {
        this.campDatas = campDatas;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
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
    
    public String getForceName() {
        return this.forceName;
    }
    
    public void setForceName(final String forceName) {
        this.forceName = forceName;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
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
    
    public int getPlayerPic() {
        return this.playerPic;
    }
    
    public void setPlayerPic(final int playerPic) {
        this.playerPic = playerPic;
    }
}
