package com.reign.gcld.kfwd.dto;

public class KfwdNoticeInfo
{
    public static final int TYPE_BATTLE_PREPARE_NOTICE = 1;
    public static final int TYPE_BATTLE_BEGIN_NOTICE = 2;
    public static final int TYPE_BATTLE_FIN_NOTICE = 3;
    public static final int TYPE_BATTLE_START_IN30SECOND = 4;
    long beginTime;
    long endTime;
    int type;
    int day;
    String noticeInfo;
    int seasonId;
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public String getNoticeInfo() {
        return this.noticeInfo;
    }
    
    public void setNoticeInfo(final String noticeInfo) {
        this.noticeInfo = noticeInfo;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public long getBeginTime() {
        return this.beginTime;
    }
    
    public void setBeginTime(final long beginTime) {
        this.beginTime = beginTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public void setDay(final int day) {
        this.day = day;
    }
}
