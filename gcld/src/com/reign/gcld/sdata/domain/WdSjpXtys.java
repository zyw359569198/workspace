package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjpXtys implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer xtysNum;
    private Integer consumeCopper;
    private Integer consumeLumber;
    private Integer consumeFood;
    private String intro;
    private Double prob;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getXtysNum() {
        return this.xtysNum;
    }
    
    public void setXtysNum(final Integer xtysNum) {
        this.xtysNum = xtysNum;
    }
    
    public Integer getConsumeCopper() {
        return this.consumeCopper;
    }
    
    public void setConsumeCopper(final Integer consumeCopper) {
        this.consumeCopper = consumeCopper;
    }
    
    public Integer getConsumeLumber() {
        return this.consumeLumber;
    }
    
    public void setConsumeLumber(final Integer consumeLumber) {
        this.consumeLumber = consumeLumber;
    }
    
    public Integer getConsumeFood() {
        return this.consumeFood;
    }
    
    public void setConsumeFood(final Integer consumeFood) {
        this.consumeFood = consumeFood;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
}
