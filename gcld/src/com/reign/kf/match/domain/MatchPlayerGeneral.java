package com.reign.kf.match.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;

@JdbcEntity
public class MatchPlayerGeneral implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    @AutoGenerator
    private int competitorId;
    private byte[] generalInfo;
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public byte[] getGeneralInfo() {
        return this.generalInfo;
    }
    
    public void setGeneralInfo(final byte[] generalInfo) {
        this.generalInfo = generalInfo;
    }
}
