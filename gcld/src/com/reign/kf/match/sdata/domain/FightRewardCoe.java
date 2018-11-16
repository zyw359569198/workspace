package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class FightRewardCoe implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Double c;
    private Double m;
    private Double e;
    private Integer delta;
    private Integer lvCoe;
    private String intro;
    
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
    
    public Double getC() {
        return this.c;
    }
    
    public void setC(final Double c) {
        this.c = c;
    }
    
    public Double getM() {
        return this.m;
    }
    
    public void setM(final Double m) {
        this.m = m;
    }
    
    public Double getE() {
        return this.e;
    }
    
    public void setE(final Double e) {
        this.e = e;
    }
    
    public Integer getDelta() {
        return this.delta;
    }
    
    public void setDelta(final Integer delta) {
        this.delta = delta;
    }
    
    public Integer getLvCoe() {
        return this.lvCoe;
    }
    
    public void setLvCoe(final Integer lvCoe) {
        this.lvCoe = lvCoe;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
}
