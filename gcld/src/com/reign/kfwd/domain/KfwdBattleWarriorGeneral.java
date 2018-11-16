package com.reign.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.protocol.*;
import org.codehaus.jackson.*;
import java.io.*;

public class KfwdBattleWarriorGeneral implements IModel
{
    private int competitorId;
    private Integer playerId;
    private String generalInfo;
    private String picInfo;
    private int seasonId;
    protected String[] pics;
    
    public CampArmyParam[] getCampList() {
        try {
            return (CampArmyParam[])Types.objectReader(Types.JAVATYPE_CAMPARMYDATALIST).readValue(this.generalInfo);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getGeneralInfo() {
        return this.generalInfo;
    }
    
    public void setGeneralInfo(final String generalInfo) {
        this.generalInfo = generalInfo;
    }
    
    public String getPicInfo() {
        return this.picInfo;
    }
    
    public void setPicInfo(final String picInfo) {
        this.picInfo = picInfo;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
}
