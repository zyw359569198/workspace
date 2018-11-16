package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class SearchComboEvent implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer comboPos;
    private String eventInfo;
    private Integer reveal;
    
    public Integer getComboPos() {
        return this.comboPos;
    }
    
    public void setComboPos(final Integer comboPos) {
        this.comboPos = comboPos;
    }
    
    public String getEventInfo() {
        return this.eventInfo;
    }
    
    public void setEventInfo(final String eventInfo) {
        this.eventInfo = eventInfo;
    }
    
    public Integer getReveal() {
        return this.reveal;
    }
    
    public void setReveal(final Integer reveal) {
        this.reveal = reveal;
    }
}
