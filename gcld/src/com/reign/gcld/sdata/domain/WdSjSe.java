package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjSe implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String intro;
    private String table;
    private Double prob;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getTable() {
        return this.table;
    }
    
    public void setTable(final String table) {
        this.table = table;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
}
