package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdSeasonBattleRes
{
    List<PTopPlayerInfo> topList;
    List<PResultInfo> selfNationInfo;
    
    public List<PTopPlayerInfo> getTopList() {
        return this.topList;
    }
    
    public void setTopList(final List<PTopPlayerInfo> topList) {
        this.topList = topList;
    }
    
    public List<PResultInfo> getSelfNationInfo() {
        return this.selfNationInfo;
    }
    
    public void setSelfNationInfo(final List<PResultInfo> selfNationInfo) {
        this.selfNationInfo = selfNationInfo;
    }
}
