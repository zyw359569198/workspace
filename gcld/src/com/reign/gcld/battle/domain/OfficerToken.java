package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class OfficerToken implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer officerid;
    private Integer num;
    private Integer forceid;
    private Integer killTokenNum;
    private String tokenInfo;
    
    public Integer getOfficerid() {
        return this.officerid;
    }
    
    public void setOfficerid(final Integer officerid) {
        this.officerid = officerid;
    }
    
    public Integer getNum() {
        return this.num;
    }
    
    public void setNum(final Integer num) {
        this.num = num;
    }
    
    public Integer getForceid() {
        return this.forceid;
    }
    
    public void setForceid(final Integer forceid) {
        this.forceid = forceid;
    }
    
    public Integer getKillTokenNum() {
        return this.killTokenNum;
    }
    
    public void setKillTokenNum(final Integer killTokenNum) {
        this.killTokenNum = killTokenNum;
    }
    
    public String getTokenInfo() {
        return this.tokenInfo;
    }
    
    public void setTokenInfo(final String tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
