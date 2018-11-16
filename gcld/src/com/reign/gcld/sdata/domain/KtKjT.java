package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtKjT implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private Integer win;
    private Integer tc;
    private Double reR;
    private Double reT;
    private Integer expC;
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getWin() {
        return this.win;
    }
    
    public void setWin(final Integer win) {
        this.win = win;
    }
    
    public Integer getTc() {
        return this.tc;
    }
    
    public void setTc(final Integer tc) {
        this.tc = tc;
    }
    
    public Double getReR() {
        return this.reR;
    }
    
    public void setReR(final Double reR) {
        this.reR = reR;
    }
    
    public Double getReT() {
        return this.reT;
    }
    
    public void setReT(final Double reT) {
        this.reT = reT;
    }
    
    public Integer getExpC() {
        return this.expC;
    }
    
    public void setExpC(final Integer expC) {
        this.expC = expC;
    }
}
