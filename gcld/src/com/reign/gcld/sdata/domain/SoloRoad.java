package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class SoloRoad implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer soloId;
    private Integer start;
    private Integer end;
    private Integer length;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSoloId() {
        return this.soloId;
    }
    
    public void setSoloId(final Integer soloId) {
        this.soloId = soloId;
    }
    
    public Integer getStart() {
        return this.start;
    }
    
    public void setStart(final Integer start) {
        this.start = start;
    }
    
    public Integer getEnd() {
        return this.end;
    }
    
    public void setEnd(final Integer end) {
        this.end = end;
    }
    
    public Integer getLength() {
        return this.length;
    }
    
    public void setLength(final Integer length) {
        this.length = length;
    }
}
