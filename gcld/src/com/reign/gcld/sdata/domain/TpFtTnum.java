package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TpFtTnum implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer feats;
    private Integer tNum;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getFeats() {
        return this.feats;
    }
    
    public void setFeats(final Integer feats) {
        this.feats = feats;
    }
    
    public Integer getTNum() {
        return this.tNum;
    }
    
    public void setTNum(final Integer tNum) {
        this.tNum = tNum;
    }
}
