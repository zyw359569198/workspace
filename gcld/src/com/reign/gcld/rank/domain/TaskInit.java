package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class TaskInit implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
}
