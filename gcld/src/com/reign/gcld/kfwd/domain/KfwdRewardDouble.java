package com.reign.gcld.kfwd.domain;

import com.reign.framework.mybatis.*;
import org.apache.commons.lang.*;

public class KfwdRewardDouble implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer pk;
    private Integer seasonId;
    private Integer playerId;
    private String doubleinfo;
    private Integer cid;
    static final int ONEDOUBLEPOS = 3;
    static final long ONEDOUBLEMASK = 7L;
    public static final int DOUBLEONEMAXTIME = 3;
    
    public Integer getPk() {
        return this.pk;
    }
    
    public void setPk(final Integer pk) {
        this.pk = pk;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getDoubleinfo() {
        return this.doubleinfo;
    }
    
    public void setDoubleinfo(final String doubleinfo) {
        this.doubleinfo = doubleinfo;
    }
    
    public Integer getCid() {
        return this.cid;
    }
    
    public void setCid(final Integer cid) {
        this.cid = cid;
    }
    
    public int getRoundDoubleCoef(final int round) {
        if (StringUtils.isBlank(this.doubleinfo)) {
            this.doubleinfo = "0";
        }
        final long doubleCoef = Long.parseLong(this.doubleinfo) >> 3 * (round - 1) & 0x7L;
        return (int)doubleCoef;
    }
    
    public void setRoundDoubleInfo(final int round, final long newCoef) {
        if (StringUtils.isBlank(this.doubleinfo)) {
            this.doubleinfo = "0";
        }
        long info = Long.parseLong(this.doubleinfo);
        info = ((info & ~(7L << 3 * (round - 1))) | newCoef << 3 * (round - 1));
        this.doubleinfo = String.valueOf(info);
    }
}
