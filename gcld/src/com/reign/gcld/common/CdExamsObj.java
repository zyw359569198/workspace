package com.reign.gcld.common;

public class CdExamsObj
{
    private String name;
    private String armyIds;
    private String cityIds;
    private int generalLv;
    private int generalNum;
    private int openNextNum;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getArmyIds() {
        return this.armyIds;
    }
    
    public void setArmyIds(final String armyIds) {
        this.armyIds = armyIds;
    }
    
    public void setCityIds(final String cityIds) {
        this.cityIds = cityIds;
    }
    
    public String getCityIds() {
        return this.cityIds;
    }
    
    public int getGeneralLv() {
        return this.generalLv;
    }
    
    public void setGeneralLv(final int generalLv) {
        this.generalLv = generalLv;
    }
    
    public int getGeneralNum() {
        return this.generalNum;
    }
    
    public void setGeneralNum(final int generalNum) {
        this.generalNum = generalNum;
    }
    
    public int getOpenNextNum() {
        return this.openNextNum;
    }
    
    public void setOpenNextNum(final int openNextNum) {
        this.openNextNum = openNextNum;
    }
}
