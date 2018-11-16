package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjpDrama implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer dramaId;
    private Integer difficulty;
    private Integer numMax;
    private Integer openLv;
    private Integer closeLv;
    private Integer openTech;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getDramaId() {
        return this.dramaId;
    }
    
    public void setDramaId(final Integer dramaId) {
        this.dramaId = dramaId;
    }
    
    public Integer getDifficulty() {
        return this.difficulty;
    }
    
    public void setDifficulty(final Integer difficulty) {
        this.difficulty = difficulty;
    }
    
    public Integer getNumMax() {
        return this.numMax;
    }
    
    public void setNumMax(final Integer numMax) {
        this.numMax = numMax;
    }
    
    public Integer getOpenLv() {
        return this.openLv;
    }
    
    public void setOpenLv(final Integer openLv) {
        this.openLv = openLv;
    }
    
    public Integer getCloseLv() {
        return this.closeLv;
    }
    
    public void setCloseLv(final Integer closeLv) {
        this.closeLv = closeLv;
    }
    
    public Integer getOpenTech() {
        return this.openTech;
    }
    
    public void setOpenTech(final Integer openTech) {
        this.openTech = openTech;
    }
}
