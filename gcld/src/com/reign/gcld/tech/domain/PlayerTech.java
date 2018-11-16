package com.reign.gcld.tech.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerTech implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer techId;
    private Integer num;
    private Date cd;
    private Integer status;
    private Integer jobId;
    private Integer isNew;
    private Integer keyId;
    private Integer finishNew;
    
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
    
    public Integer getTechId() {
        return this.techId;
    }
    
    public void setTechId(final Integer techId) {
        this.techId = techId;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Date getCd() {
        return this.cd;
    }
    
    public void setCd(final Date cd) {
        this.cd = cd;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public Integer getJobId() {
        return this.jobId;
    }
    
    public void setJobId(final Integer jobId) {
        this.jobId = jobId;
    }
    
    public Integer getIsNew() {
        return this.isNew;
    }
    
    public void setIsNew(final Integer isNew) {
        this.isNew = isNew;
    }
    
    public Integer getKeyId() {
        return this.keyId;
    }
    
    public void setKeyId(final Integer keyId) {
        this.keyId = keyId;
    }
    
    public Integer getFinishNew() {
        return this.finishNew;
    }
    
    public void setFinishNew(final Integer finishNew) {
        this.finishNew = finishNew;
    }
}
