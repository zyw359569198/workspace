package com.reign.kfgz.dto.response;

import com.reign.kfgz.dto.request.*;
import java.util.*;

public class KfgzRankingInfoRes
{
    int state;
    List<KfgzNationResultReq> nationRes;
    List<KfgzPlayerRankingInfoReq> playerRes;
    
    public KfgzRankingInfoRes() {
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
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
}
