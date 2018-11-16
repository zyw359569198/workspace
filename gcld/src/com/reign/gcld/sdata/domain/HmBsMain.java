package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HmBsMain implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer bsId;
    private String name;
    private Integer dMax;
    private Integer num;
    private Integer qualityLow;
    private Integer qualityHigh;
    private Integer output;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getBsId() {
        return this.bsId;
    }
    
    public void setBsId(final Integer bsId) {
        this.bsId = bsId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getDMax() {
        return this.dMax;
    }
    
    public void setDMax(final Integer dMax) {
        this.dMax = dMax;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getQualityLow() {
        return this.qualityLow;
    }
    
    public void setQualityLow(final Integer qualityLow) {
        this.qualityLow = qualityLow;
    }
    
    public Integer getQualityHigh() {
        return this.qualityHigh;
    }
    
    public void setQualityHigh(final Integer qualityHigh) {
        this.qualityHigh = qualityHigh;
    }
    
    public Integer getOutput() {
        return this.output;
    }
    
    public void setOutput(final Integer output) {
        this.output = output;
    }
}
