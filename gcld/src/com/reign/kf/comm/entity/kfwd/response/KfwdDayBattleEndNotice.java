package com.reign.kf.comm.entity.kfwd.response;

import com.reign.kf.comm.entity.kfwd.request.*;
import java.util.*;

public class KfwdDayBattleEndNotice
{
    int seasonId;
    int scheduleId;
    int day;
    List<KfwdPlayerInfo> list;
    
    public KfwdDayBattleEndNotice() {
        this.list = new ArrayList<KfwdPlayerInfo>();
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public void setDay(final int day) {
        this.day = day;
    }
    
    public List<KfwdPlayerInfo> getList() {
        return this.list;
    }
    
    public void setList(final List<KfwdPlayerInfo> list) {
        this.list = list;
    }
}
