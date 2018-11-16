package com.reign.kf.comm.entity.kfwd.response;

import java.util.*;

public class KfwdScheduleInfoDto
{
    int seasonId;
    List<KfwdGwScheduleInfoDto> list;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public List<KfwdGwScheduleInfoDto> getList() {
        return this.list;
    }
    
    public void setList(final List<KfwdGwScheduleInfoDto> list) {
        this.list = list;
    }
}
