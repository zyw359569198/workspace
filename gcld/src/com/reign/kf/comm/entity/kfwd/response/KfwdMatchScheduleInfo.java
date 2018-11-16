package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdMatchScheduleInfo
{
    int seasonId;
    List<KfwdGwScheduleInfoDto> sList;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public List<KfwdGwScheduleInfoDto> getsList() {
        return this.sList;
    }
    
    public void setsList(final List<KfwdGwScheduleInfoDto> sList) {
        this.sList = sList;
    }
}
