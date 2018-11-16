package com.reign.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import javax.persistence.*;

public class KfwdRuntimeInspire implements IModel
{
    private int pk;
    private int seasonId;
    private int scheduleId;
    private int competitorId;
    private int round;
    private int attNum;
    private int defNum;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
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
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getAttNum() {
        return this.attNum;
    }
    
    public void setAttNum(final int attNum) {
        this.attNum = attNum;
    }
    
    public int getDefNum() {
        return this.defNum;
    }
    
    public void setDefNum(final int defNum) {
        this.defNum = defNum;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
    
    @Transient
    public long getKey() {
        final long key = this.round | this.competitorId << 5;
        return key;
    }
    
    public static long makeKey(final int competitorId, final int round) {
        final long key = round | competitorId << 5;
        return key;
    }
}
