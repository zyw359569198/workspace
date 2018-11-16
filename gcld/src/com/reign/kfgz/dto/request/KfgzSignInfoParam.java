package com.reign.kfgz.dto.request;

import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.protocol.*;
import org.codehaus.jackson.*;
import java.io.*;

public class KfgzSignInfoParam
{
    private KfgzPlayerInfo playerInfo;
    private String campInfo;
    private CampArmyParam[] campDatas;
    private long serverStartTime;
    int seasonId;
    int gzId;
    
    public void setCampList(final int forceId) throws JsonProcessingException, IOException {
        this.campDatas = (CampArmyParam[])Types.objectReader(Types.JAVATYPE_CAMPARMYDATALIST).readValue(this.getCampInfo());
        if (this.campDatas != null) {
            CampArmyParam[] campDatas;
            for (int length = (campDatas = this.campDatas).length, i = 0; i < length; ++i) {
                final CampArmyParam cap = campDatas[i];
                cap.setForceId(forceId);
            }
        }
    }
    
    public String getCampInfo() {
        return this.campInfo;
    }
    
    public void setCampInfo(final String campInfo) {
        this.campInfo = campInfo;
    }
    
    public CampArmyParam[] getCampDatas() {
        return this.campDatas;
    }
    
    public void setCampDatas(final CampArmyParam[] campDatas) {
        this.campDatas = campDatas;
    }
    
    public KfgzPlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }
    
    public void setPlayerInfo(final KfgzPlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
    
    public long getServerStartTime() {
        return this.serverStartTime;
    }
    
    public void setServerStartTime(final long serverStartTime) {
        this.serverStartTime = serverStartTime;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getGzId() {
        return this.gzId;
    }
    
    public void setGzId(final int gzId) {
        this.gzId = gzId;
    }
    
    @Override
    public String toString() {
        return "SignInfo [playerId=" + this.playerInfo.getPlayerId() + ", playerName=" + this.playerInfo.getPlayerName() + ", serverName=" + this.playerInfo.getServerName() + "]";
    }
}
