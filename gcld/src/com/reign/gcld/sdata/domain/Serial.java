package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Serial implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer index;
    private Integer point;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getIndex() {
        return this.index;
    }
    
    public void setIndex(final Integer index) {
        this.index = index;
    }
    
    public Integer getPoint() {
        return this.point;
    }
    
    public void setPoint(final Integer point) {
        this.point = point;
    }
}
