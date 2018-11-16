package com.reign.kfzb.dto.request;

import com.reign.kf.comm.param.match.*;

public class KfzbSignInfo
{
    private int seasonId;
    private KfzbPlayerInfo playerInfo;
    private String campInfo;
    private CampArmyParam[] campDatas;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public KfzbPlayerInfo getPlayerInfo() {
        return this.playerInfo;
    }
    
    public void setPlayerInfo(final KfzbPlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
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
}
