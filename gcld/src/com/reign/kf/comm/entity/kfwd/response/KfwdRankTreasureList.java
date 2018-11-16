package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdRankTreasureList
{
    List<KfwdRankTreasureInfo> list;
    
    public KfwdRankTreasureList() {
        this.list = new ArrayList<KfwdRankTreasureInfo>();
    }
    
    public List<KfwdRankTreasureInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfwdRankTreasureInfo> list) {
        this.list = list;
    }
}
