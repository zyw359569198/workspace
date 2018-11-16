package com.reign.kfzb.dto.response;

import java.util.*;

public class KfzbPhase1RewardInfoList
{
    List<KfzbPhase1RewardInfo> list;
    
    public KfzbPhase1RewardInfoList() {
        this.list = new ArrayList<KfzbPhase1RewardInfo>();
    }
    
    public List<KfzbPhase1RewardInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfzbPhase1RewardInfo> list) {
        this.list = list;
    }
}
