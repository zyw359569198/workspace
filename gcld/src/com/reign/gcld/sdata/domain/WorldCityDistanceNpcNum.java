package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldCityDistanceNpcNum implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer npcNum;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getNpcNum() {
        return this.npcNum;
    }
    
    public void setNpcNum(final Integer npcNum) {
        this.npcNum = npcNum;
    }
}
