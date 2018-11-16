package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KillToplist implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer kill;
    private String treasureTypeList;
    private String treasureQualityList;
    private Integer treasureNum;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getKill() {
        return this.kill;
    }
    
    public void setKill(final Integer kill) {
        this.kill = kill;
    }
    
    public String getTreasureTypeList() {
        return this.treasureTypeList;
    }
    
    public void setTreasureTypeList(final String treasureTypeList) {
        this.treasureTypeList = treasureTypeList;
    }
    
    public String getTreasureQualityList() {
        return this.treasureQualityList;
    }
    
    public void setTreasureQualityList(final String treasureQualityList) {
        this.treasureQualityList = treasureQualityList;
    }
    
    public Integer getTreasureNum() {
        return this.treasureNum;
    }
    
    public void setTreasureNum(final Integer treasureNum) {
        this.treasureNum = treasureNum;
    }
}
