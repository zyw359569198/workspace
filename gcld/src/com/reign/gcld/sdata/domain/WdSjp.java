package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjp implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pic;
    private Integer dis;
    private Integer numMax;
    private Integer intervalMin;
    private Integer intervalMax;
    private Integer open;
    private Integer close;
    
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
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getDis() {
        return this.dis;
    }
    
    public void setDis(final Integer dis) {
        this.dis = dis;
    }
    
    public Integer getNumMax() {
        return this.numMax;
    }
    
    public void setNumMax(final Integer numMax) {
        this.numMax = numMax;
    }
    
    public Integer getIntervalMin() {
        return this.intervalMin;
    }
    
    public void setIntervalMin(final Integer intervalMin) {
        this.intervalMin = intervalMin;
    }
    
    public Integer getIntervalMax() {
        return this.intervalMax;
    }
    
    public void setIntervalMax(final Integer intervalMax) {
        this.intervalMax = intervalMax;
    }
    
    public Integer getOpen() {
        return this.open;
    }
    
    public void setOpen(final Integer open) {
        this.open = open;
    }
    
    public Integer getClose() {
        return this.close;
    }
    
    public void setClose(final Integer close) {
        this.close = close;
    }
}
