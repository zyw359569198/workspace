package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class BtRoad implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer sendNation;
    private Integer receiveNation;
    private String path;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSendNation() {
        return this.sendNation;
    }
    
    public void setSendNation(final Integer sendNation) {
        this.sendNation = sendNation;
    }
    
    public Integer getReceiveNation() {
        return this.receiveNation;
    }
    
    public void setReceiveNation(final Integer receiveNation) {
        this.receiveNation = receiveNation;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
}
