package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtHjNpc implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer kindomLv;
    private Integer type;
    private String cityId;
    private Integer armyId;
    private Integer num;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getKindomLv() {
        return this.kindomLv;
    }
    
    public void setKindomLv(final Integer kindomLv) {
        this.kindomLv = kindomLv;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final String cityId) {
        this.cityId = cityId;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
}
