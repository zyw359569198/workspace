package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HmBsGold implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer bsId;
    private Integer manLv;
    private Integer upGold;
    private Integer extra;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getBsId() {
        return this.bsId;
    }
    
    public void setBsId(final Integer bsId) {
        this.bsId = bsId;
    }
    
    public Integer getManLv() {
        return this.manLv;
    }
    
    public void setManLv(final Integer manLv) {
        this.manLv = manLv;
    }
    
    public Integer getUpGold() {
        return this.upGold;
    }
    
    public void setUpGold(final Integer upGold) {
        this.upGold = upGold;
    }
    
    public Integer getExtra() {
        return this.extra;
    }
    
    public void setExtra(final Integer extra) {
        this.extra = extra;
    }
}
