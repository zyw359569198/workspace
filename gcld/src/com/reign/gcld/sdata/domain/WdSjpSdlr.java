package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjpSdlr implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer num;
    private Integer type;
    private String intro;
    private Double prob;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
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
