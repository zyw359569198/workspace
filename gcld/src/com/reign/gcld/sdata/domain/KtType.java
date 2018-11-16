package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtType implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intro;
    private Double prob;
    private Integer j1;
    private Integer j2;
    private Integer j3;
    
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
    
    public Integer getJ1() {
        return this.j1;
    }
    
    public void setJ1(final Integer j1) {
        this.j1 = j1;
    }
    
    public Integer getJ2() {
        return this.j2;
    }
    
    public void setJ2(final Integer j2) {
        this.j2 = j2;
    }
    
    public Integer getJ3() {
        return this.j3;
    }
    
    public void setJ3(final Integer j3) {
        this.j3 = j3;
    }
}
