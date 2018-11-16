package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class EfL implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer c;
    private Integer i;
    private Integer n;
    private Integer p;
    private String l;
    private Integer[] cityIds;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getC() {
        return this.c;
    }
    
    public void setC(final Integer c) {
        this.c = c;
    }
    
    public Integer getI() {
        return this.i;
    }
    
    public void setI(final Integer i) {
        this.i = i;
    }
    
    public Integer getN() {
        return this.n;
    }
    
    public void setN(final Integer n) {
        this.n = n;
    }
    
    public Integer getP() {
        return this.p;
    }
    
    public void setP(final Integer p) {
        this.p = p;
    }
    
    public String getL() {
        return this.l;
    }
    
    public void setL(final String l) {
        this.l = l;
    }
    
    public Integer[] getCityIds() {
        return this.cityIds;
    }
    
    public void setCityIds(final Integer[] cityIds) {
        this.cityIds = cityIds;
    }
}
