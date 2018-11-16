package com.reign.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import com.reign.kfwd.constants.*;

public class KfwdRewardDouble implements IModel
{
    int seasonId;
    int scheduleId;
    private int competitorId;
    long doubleInfo;
    static final int ONEDOUBLEPOS = 3;
    static final long ONEDOUBLEMASK = 7L;
    static final int DOUBLEONETIME = 1;
    static final int MAXDOUBLETIME = 3;
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
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
    
    public long getDoubleInfo() {
        return this.doubleInfo;
    }
    
    public void setDoubleInfo(final long doubleInfo) {
        this.doubleInfo = doubleInfo;
    }
    
    public int getRoundDoubleTicket(final int round, final int ticket) {
        final int doubleCoef = this.getRoundDoubleCoef(round);
        return KfwdConstantsAndMethod.getTicketByDoubleCoef(ticket, doubleCoef);
    }
    
    public int getRoundDoubleCoef(final int round) {
        final long doubleCoef = this.doubleInfo >> 3 * (round - 1) & 0x7L;
        return (int)doubleCoef;
    }
    
    public void setRoundDoubleInfo(final int round, final long newCoef) {
        if (newCoef > 3L) {
            return;
        }
        long info = this.doubleInfo;
        info = ((info & ~(7L << 3 * (round - 1))) | newCoef << 3 * (round - 1));
        this.doubleInfo = info;
    }
}
