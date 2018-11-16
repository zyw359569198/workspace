package com.reign.kfgz.dto.request;

import java.util.*;

public class KfgzBattleResultInfo
{
    List<KfgzNationResultReq> nationRes;
    List<KfgzPlayerRankingInfoReq> playerRes;
    
    public KfgzBattleResultInfo() {
        this.nationRes = new ArrayList<KfgzNationResultReq>();
        this.playerRes = new ArrayList<KfgzPlayerRankingInfoReq>();
    }
    
    public List<KfgzNationResultReq> getNationRes() {
        return this.nationRes;
    }
    
    public void setNationRes(final List<KfgzNationResultReq> nationRes) {
        this.nationRes = nationRes;
    }
    
    public List<KfgzPlayerRankingInfoReq> getPlayerRes() {
        return this.playerRes;
    }
    
    public void setPlayerRes(final List<KfgzPlayerRankingInfoReq> playerRes) {
        this.playerRes = playerRes;
    }
}
