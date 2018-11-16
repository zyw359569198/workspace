package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Tech implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer key;
    private String keyStr;
    private String name;
    private String pic;
    private String intro;
    private String resource;
    private Integer resourceTimes;
    private Integer researchTime;
    private Integer par1;
    private String par1Intro;
    private Integer par2;
    private String par2Intro;
    private Double par3;
    private String par3Intro;
    private Double par4;
    private String par4Intro;
    private Integer dropIndex;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
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
    
    public String getResource() {
        return this.resource;
    }
    
    public void setResource(final String resource) {
        this.resource = resource;
    }
    
    public Integer getResourceTimes() {
        return this.resourceTimes;
    }
    
    public void setResourceTimes(final Integer resourceTimes) {
        this.resourceTimes = resourceTimes;
    }
    
    public Integer getResearchTime() {
        return this.researchTime;
    }
    
    public void setResearchTime(final Integer researchTime) {
        this.researchTime = researchTime;
    }
    
    public Integer getPar1() {
        return this.par1;
    }
    
    public void setPar1(final Integer par1) {
        this.par1 = par1;
    }
    
    public String getPar1Intro() {
        return this.par1Intro;
    }
    
    public void setPar1Intro(final String par1Intro) {
        this.par1Intro = par1Intro;
    }
    
    public Integer getPar2() {
        return this.par2;
    }
    
    public void setPar2(final Integer par2) {
        this.par2 = par2;
    }
    
    public String getPar2Intro() {
        return this.par2Intro;
    }
    
    public void setPar2Intro(final String par2Intro) {
        this.par2Intro = par2Intro;
    }
    
    public Double getPar3() {
        return this.par3;
    }
    
    public void setPar3(final Double par3) {
        this.par3 = par3;
    }
    
    public String getPar3Intro() {
        return this.par3Intro;
    }
    
    public void setPar3Intro(final String par3Intro) {
        this.par3Intro = par3Intro;
    }
    
    public Double getPar4() {
        return this.par4;
    }
    
    public void setPar4(final Double par4) {
        this.par4 = par4;
    }
    
    public String getPar4Intro() {
        return this.par4Intro;
    }
    
    public void setPar4Intro(final String par4Intro) {
        this.par4Intro = par4Intro;
    }
    
    public Integer getDropIndex() {
        return this.dropIndex;
    }
    
    public void setDropIndex(final Integer dropIndex) {
        this.dropIndex = dropIndex;
    }
}
