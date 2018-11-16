package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class CdExamsRanking implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer highLv;
    private Integer lowLv;
    private Double e;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getHighLv() {
        return this.highLv;
    }
    
    public void setHighLv(final Integer highLv) {
        this.highLv = highLv;
    }
    
    public Integer getLowLv() {
        return this.lowLv;
    }
    
    public void setLowLv(final Integer lowLv) {
        this.lowLv = lowLv;
    }
    
    public Double getE() {
        return this.e;
    }
    
    public void setE(final Double e) {
        this.e = e;
    }
}
