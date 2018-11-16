package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class City implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer forceId;
    private Integer state;
    private Integer title;
    private Integer gNum;
    private String trickinfo;
    private Integer border;
    private Integer stateJobId;
    private Integer hp;
    private Integer hpMax;
    private String otherInfo;
    public static final int OTHER_INFO_TYPE_1_SHA_DI_LING = 1;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getTitle() {
        return this.title;
    }
    
    public void setTitle(final Integer title) {
        this.title = title;
    }
    
    public Integer getGNum() {
        return this.gNum;
    }
    
    public void setGNum(final Integer gNum) {
        this.gNum = gNum;
    }
    
    public String getTrickinfo() {
        return this.trickinfo;
    }
    
    public void setTrickinfo(final String trickinfo) {
        this.trickinfo = trickinfo;
    }
    
    public Integer getBorder() {
        return this.border;
    }
    
    public void setBorder(final Integer border) {
        this.border = border;
    }
    
    public Integer getStateJobId() {
        return this.stateJobId;
    }
    
    public void setStateJobId(final Integer stateJobId) {
        this.stateJobId = stateJobId;
    }
    
    public Integer getHp() {
        return this.hp;
    }
    
    public void setHp(final Integer hp) {
        this.hp = hp;
    }
    
    public Integer getHpMax() {
        return this.hpMax;
    }
    
    public void setHpMax(final Integer hpMax) {
        this.hpMax = hpMax;
    }
    
    public String getOtherInfo() {
        return this.otherInfo;
    }
    
    public void setOtherInfo(final String otherInfo) {
        this.otherInfo = otherInfo;
    }
}
