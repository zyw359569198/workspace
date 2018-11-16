package com.reign.gcld.building.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class BluePrint implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer index;
    private Integer state;
    private Date cd;
    private Integer jobId;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getIndex() {
        return this.index;
    }
    
    public void setIndex(final Integer index) {
        this.index = index;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Date getCd() {
        return this.cd;
    }
    
    public void setCd(final Date cd) {
        this.cd = cd;
    }
    
    public Integer getJobId() {
        return this.jobId;
    }
    
    public void setJobId(final Integer jobId) {
        this.jobId = jobId;
    }
}
