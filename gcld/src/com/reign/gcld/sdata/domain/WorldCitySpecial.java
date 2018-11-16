package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldCitySpecial implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer cityId;
    private Double par1;
    private Double par2;
    private String par2Intro;
    private String par1Intro;
    private String name;
    private String pic;
    private String intro;
    private Integer key;
    private String keyStr;
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Double getPar1() {
        return this.par1;
    }
    
    public void setPar1(final Double par1) {
        this.par1 = par1;
    }
    
    public Double getPar2() {
        return this.par2;
    }
    
    public void setPar2(final Double par2) {
        this.par2 = par2;
    }
    
    public String getPar2Intro() {
        return this.par2Intro;
    }
    
    public void setPar2Intro(final String par2Intro) {
        this.par2Intro = par2Intro;
    }
    
    public String getPar1Intro() {
        return this.par1Intro;
    }
    
    public void setPar1Intro(final String par1Intro) {
        this.par1Intro = par1Intro;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Integer getKey() {
        return this.key;
    }
    
    public void setKey(final Integer key) {
        this.key = key;
    }
    
    public String getKeyStr() {
        return this.keyStr;
    }
    
    public void setKeyStr(final String keyStr) {
        this.keyStr = keyStr;
    }
}
