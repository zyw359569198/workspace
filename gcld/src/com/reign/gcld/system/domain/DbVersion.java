package com.reign.gcld.system.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class DbVersion implements IModel
{
    private static final long serialVersionUID = 1L;
    private String dbVersion;
    private Date serverTime;
    private Integer diffDay;
    private Integer seasonId;
    
    public String getDbVersion() {
        return this.dbVersion;
    }
    
    public void setDbVersion(final String dbVersion) {
        this.dbVersion = dbVersion;
    }
    
    public Date getServerTime() {
        return this.serverTime;
    }
    
    public void setServerTime(final Date serverTime) {
        this.serverTime = serverTime;
    }
    
    public Integer getDiffDay() {
        return this.diffDay;
    }
    
    public void setDiffDay(final Integer diffDay) {
        this.diffDay = diffDay;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
}
