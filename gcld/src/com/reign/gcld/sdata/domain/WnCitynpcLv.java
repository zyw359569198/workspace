package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WnCitynpcLv implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private Integer day;
    private Integer guardE;
    private Integer cexpMin;
    private Integer cexpMax;
    private Integer gLv;
    private String weiName;
    private String shuName;
    private String wuName;
    private String weiArmies;
    private String shuArmies;
    private String wuArmies;
    private String ndArmies;
    private Integer[] weiArmyIds;
    private Integer[] shuArmyIds;
    private Integer[] wuArmyIds;
    private Integer[] ndArmyIds;
    
    public WnCitynpcLv() {
        this.weiArmyIds = null;
        this.shuArmyIds = null;
        this.wuArmyIds = null;
        this.ndArmyIds = null;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getDay() {
        return this.day;
    }
    
    public void setDay(final Integer day) {
        this.day = day;
    }
    
    public Integer getGuardE() {
        return this.guardE;
    }
    
    public void setGuardE(final Integer guardE) {
        this.guardE = guardE;
    }
    
    public Integer getCexpMin() {
        return this.cexpMin;
    }
    
    public void setCexpMin(final Integer cexpMin) {
        this.cexpMin = cexpMin;
    }
    
    public Integer getCexpMax() {
        return this.cexpMax;
    }
    
    public void setCexpMax(final Integer cexpMax) {
        this.cexpMax = cexpMax;
    }
    
    public Integer getGLv() {
        return this.gLv;
    }
    
    public void setGLv(final Integer gLv) {
        this.gLv = gLv;
    }
    
    public String getWeiName() {
        return this.weiName;
    }
    
    public void setWeiName(final String weiName) {
        this.weiName = weiName;
    }
    
    public String getShuName() {
        return this.shuName;
    }
    
    public void setShuName(final String shuName) {
        this.shuName = shuName;
    }
    
    public String getWuName() {
        return this.wuName;
    }
    
    public void setWuName(final String wuName) {
        this.wuName = wuName;
    }
    
    public String getWeiArmies() {
        return this.weiArmies;
    }
    
    public void setWeiArmies(final String weiArmies) {
        this.weiArmies = weiArmies;
    }
    
    public String getShuArmies() {
        return this.shuArmies;
    }
    
    public void setShuArmies(final String shuArmies) {
        this.shuArmies = shuArmies;
    }
    
    public String getWuArmies() {
        return this.wuArmies;
    }
    
    public void setWuArmies(final String wuArmies) {
        this.wuArmies = wuArmies;
    }
    
    public String getNdArmies() {
        return this.ndArmies;
    }
    
    public void setNdArmies(final String ndArmies) {
        this.ndArmies = ndArmies;
    }
    
    public Integer[] getWeiArmyIds() {
        return this.weiArmyIds;
    }
    
    public void setWeiArmyIds(final Integer[] weiArmyIds) {
        this.weiArmyIds = weiArmyIds;
    }
    
    public Integer[] getShuArmyIds() {
        return this.shuArmyIds;
    }
    
    public void setShuArmyIds(final Integer[] shuArmyIds) {
        this.shuArmyIds = shuArmyIds;
    }
    
    public Integer[] getWuArmyIds() {
        return this.wuArmyIds;
    }
    
    public void setWuArmyIds(final Integer[] wuArmyIds) {
        this.wuArmyIds = wuArmyIds;
    }
    
    public Integer[] getNdArmyIds() {
        return this.ndArmyIds;
    }
    
    public void setNdArmyIds(final Integer[] ndArmyIds) {
        this.ndArmyIds = ndArmyIds;
    }
}
