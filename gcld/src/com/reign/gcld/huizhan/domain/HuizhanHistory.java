package com.reign.gcld.huizhan.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class HuizhanHistory implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Date startTime;
    private Integer cityId;
    private Integer attForceId1;
    private Long attForce1;
    private Integer attForceId2;
    private Long attForce2;
    private Integer defForceId;
    private Long defForce;
    private Integer winner;
    private Integer gatherFlag;
    private Integer state;
    private Date endTime;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Integer getAttForceId1() {
        return this.attForceId1;
    }
    
    public void setAttForceId1(final Integer attForceId1) {
        this.attForceId1 = attForceId1;
    }
    
    public Long getAttForce1() {
        return this.attForce1;
    }
    
    public void setAttForce1(final Long attForce1) {
        this.attForce1 = attForce1;
    }
    
    public Integer getAttForceId2() {
        return this.attForceId2;
    }
    
    public void setAttForceId2(final Integer attForceId2) {
        this.attForceId2 = attForceId2;
    }
    
    public Long getAttForce2() {
        return this.attForce2;
    }
    
    public void setAttForce2(final Long attForce2) {
        this.attForce2 = attForce2;
    }
    
    public Integer getDefForceId() {
        return this.defForceId;
    }
    
    public void setDefForceId(final Integer defForceId) {
        this.defForceId = defForceId;
    }
    
    public Long getDefForce() {
        return this.defForce;
    }
    
    public void setDefForce(final Long defForce) {
        this.defForce = defForce;
    }
    
    public Integer getWinner() {
        return this.winner;
    }
    
    public void setWinner(final Integer winner) {
        this.winner = winner;
    }
    
    public Integer getGatherFlag() {
        return this.gatherFlag;
    }
    
    public void setGatherFlag(final Integer gatherFlag) {
        this.gatherFlag = gatherFlag;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
}
