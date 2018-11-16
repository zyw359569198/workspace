package com.reign.kfgz.dto.request;

import java.util.*;

public class KfgzNationResKey
{
    int seasonId;
    int gzId;
    int forceId;
    List<Integer> cIdList;
    
    public KfgzNationResKey() {
        this.cIdList = new ArrayList<Integer>();
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
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public List<Integer> getcIdList() {
        return this.cIdList;
    }
    
    public void setcIdList(final List<Integer> cIdList) {
        this.cIdList = cIdList;
    }
}
