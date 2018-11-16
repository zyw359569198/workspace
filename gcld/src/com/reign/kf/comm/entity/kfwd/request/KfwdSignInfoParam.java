package com.reign.kf.comm.entity.kfwd.request;

import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.protocol.*;
import org.codehaus.jackson.*;
import java.io.*;

public class KfwdSignInfoParam implements Serializable
{
    private static final long serialVersionUID = 668936986172946789L;
    private KfwdPlayerInfo playerInfo;
    private String campInfo;
    private CampArmyParam[] campDatas;
    private long serverStartTime;
    private int scheduleId;
    
    public void setCampList() throws JsonProcessingException, IOException {
        this.campDatas = (CampArmyParam[])Types.objectReader(Types.JAVATYPE_CAMPARMYDATALIST).readValue(this.getCampInfo());
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
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public KfwdPlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }
    
    public void setPlayerInfo(final KfwdPlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
    
    public long getServerStartTime() {
        return this.serverStartTime;
    }
    
    public void setServerStartTime(final long serverStartTime) {
        this.serverStartTime = serverStartTime;
    }
    
    @Override
    public String toString() {
        return "SignInfo [playerId=" + this.playerInfo.getPlayerId() + ", playerName=" + this.playerInfo.getPlayerName() + ", serverName=" + this.playerInfo.getServerName() + "]";
    }
}
