package com.reign.gcld.kfwd.common;

import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.kf.comm.protocol.*;
import org.codehaus.jackson.*;
import java.io.*;

public class MatchFightMember
{
    private int competitorId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private String forceName;
    private int playerPic;
    private String serverName;
    private String serverId;
    private String legionName;
    private CampArmyParam[] campDatas;
    private int winTurns;
    private int winMatch;
    private int point;
    
    public int getPoint() {
        return this.point;
    }
    
    public void setPoint(final int point) {
        this.point = point;
    }
    
    public MatchFightMember(final int competitorId) {
        this.competitorId = competitorId;
        this.winMatch = 0;
    }
    
    public void assign(final MatchPlayerEntity matchPlayerEntity) {
        this.playerId = matchPlayerEntity.getPlayerId();
        this.playerName = matchPlayerEntity.getPlayerName();
        this.playerLv = matchPlayerEntity.getPlayerLv();
        this.forceName = matchPlayerEntity.getForceName();
        this.playerPic = matchPlayerEntity.getPlayerPic();
        this.serverName = matchPlayerEntity.getServerName();
        this.serverId = matchPlayerEntity.getServerId();
        this.setFormation(matchPlayerEntity.getCampInfo());
        this.winTurns = matchPlayerEntity.getWinNum();
    }
    
    public synchronized void assign(final String gIds) {
        final int nowSize = this.campDatas.length;
        int index = 0;
        final CampArmyParam[] temp = new CampArmyParam[nowSize];
        String[] split;
        for (int length = (split = gIds.split("#")).length, i = 0; i < length; ++i) {
            final String sId = split[i];
            final int id = Integer.valueOf(sId);
            CampArmyParam[] campDatas;
            for (int length2 = (campDatas = this.campDatas).length, j = 0; j < length2; ++j) {
                final CampArmyParam cap = campDatas[j];
                if (cap.getGeneralId() == id) {
                    temp[index++] = cap;
                }
            }
        }
        this.campDatas = temp;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
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
    
    public String getForceName() {
        return this.forceName;
    }
    
    public int getPlayerPic() {
        return this.playerPic;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public String getLegionName() {
        return this.legionName;
    }
    
    public int getWinMatch() {
        return this.winMatch;
    }
    
    public void setWinMatch(final int winMatch) {
        this.winMatch = winMatch;
    }
    
    public int getWinTurns() {
        return this.winTurns;
    }
    
    public CampArmyParam[] getCampDatas() {
        return this.campDatas;
    }
    
    public void setCampDatas(final CampArmyParam[] campDatas) {
        this.campDatas = campDatas;
    }
    
    private void setFormation(final byte[] campInfo) {
        try {
            this.campDatas = (CampArmyParam[])Types.objectReader(Types.JAVATYPE_CAMPARMYDATALIST).readValue(campInfo);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
