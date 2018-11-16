package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HmPwMain implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer dMax;
    private Integer pConsume;
    private Integer pBase;
    private Integer pTime;
    private Integer lv1Consume;
    private Integer lv1Num;
    private Integer lv2Consume;
    private Integer lv2Num;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
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
    
    public Integer getPConsume() {
        return this.pConsume;
    }
    
    public void setPConsume(final Integer pConsume) {
        this.pConsume = pConsume;
    }
    
    public Integer getPBase() {
        return this.pBase;
    }
    
    public void setPBase(final Integer pBase) {
        this.pBase = pBase;
    }
    
    public Integer getPTime() {
        return this.pTime;
    }
    
    public void setPTime(final Integer pTime) {
        this.pTime = pTime;
    }
    
    public Integer getLv1Consume() {
        return this.lv1Consume;
    }
    
    public void setLv1Consume(final Integer lv1Consume) {
        this.lv1Consume = lv1Consume;
    }
    
    public Integer getLv1Num() {
        return this.lv1Num;
    }
    
    public void setLv1Num(final Integer lv1Num) {
        this.lv1Num = lv1Num;
    }
    
    public Integer getLv2Consume() {
        return this.lv2Consume;
    }
    
    public void setLv2Consume(final Integer lv2Consume) {
        this.lv2Consume = lv2Consume;
    }
    
    public Integer getLv2Num() {
        return this.lv2Num;
    }
    
    public void setLv2Num(final Integer lv2Num) {
        this.lv2Num = lv2Num;
    }
}
