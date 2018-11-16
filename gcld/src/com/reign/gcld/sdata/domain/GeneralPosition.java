package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class GeneralPosition implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer openLv;
    private String openIntro;
    private String openTips;
    
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
    
    public Integer getOpenLv() {
        return this.openLv;
    }
    
    public void setOpenLv(final Integer openLv) {
        this.openLv = openLv;
    }
    
    public String getOpenIntro() {
        return this.openIntro;
    }
    
    public void setOpenIntro(final String openIntro) {
        this.openIntro = openIntro;
    }
    
    public String getOpenTips() {
        return this.openTips;
    }
    
    public void setOpenTips(final String openTips) {
        this.openTips = openTips;
    }
}
