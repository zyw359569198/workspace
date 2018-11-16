package com.reign.kfzb.dto.request;

import java.util.*;

public class KfzbMatchKeyList
{
    List<KfzbPhase2MatchKey> keyList;
    
    public KfzbMatchKeyList() {
        this.keyList = new ArrayList<KfzbPhase2MatchKey>();
    }
    
    public List<KfzbPhase2MatchKey> getKeyList() {
        return this.keyList;
    }
    
    public void setKeyList(final List<KfzbPhase2MatchKey> keyList) {
        this.keyList = keyList;
    }
}
