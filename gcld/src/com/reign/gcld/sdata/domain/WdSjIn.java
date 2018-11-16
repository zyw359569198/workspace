package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjIn implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer g;
    private Integer i;
    private Integer c;
    private Integer d;
    private Integer tn;
    private Integer pn;
    private Integer cv;
    private Integer v;
    private Integer du;
    private Integer inMin;
    private Integer inMax;
    private Integer rewardLv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getG() {
        return this.g;
    }
    
    public void setG(final Integer g) {
        this.g = g;
    }
    
    public Integer getI() {
        return this.i;
    }
    
    public void setI(final Integer i) {
        this.i = i;
    }
    
    public Integer getC() {
        return this.c;
    }
    
    public void setC(final Integer c) {
        this.c = c;
    }
    
    public Integer getD() {
        return this.d;
    }
    
    public void setD(final Integer d) {
        this.d = d;
    }
    
    public Integer getTn() {
        return this.tn;
    }
    
    public void setTn(final Integer tn) {
        this.tn = tn;
    }
    
    public Integer getPn() {
        return this.pn;
    }
    
    public void setPn(final Integer pn) {
        this.pn = pn;
    }
    
    public Integer getCv() {
        return this.cv;
    }
    
    public void setCv(final Integer cv) {
        this.cv = cv;
    }
    
    public Integer getV() {
        return this.v;
    }
    
    public void setV(final Integer v) {
        this.v = v;
    }
    
    public Integer getDu() {
        return this.du;
    }
    
    public void setDu(final Integer du) {
        this.du = du;
    }
    
    public Integer getInMin() {
        return this.inMin;
    }
    
    public void setInMin(final Integer inMin) {
        this.inMin = inMin;
    }
    
    public Integer getInMax() {
        return this.inMax;
    }
    
    public void setInMax(final Integer inMax) {
        this.inMax = inMax;
    }
    
    public Integer getRewardLv() {
        return this.rewardLv;
    }
    
    public void setRewardLv(final Integer rewardLv) {
        this.rewardLv = rewardLv;
    }
}
