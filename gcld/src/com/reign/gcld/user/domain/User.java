package com.reign.gcld.user.domain;

import com.reign.framework.mybatis.*;

public class User implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String userName;
    private String password;
    private Integer activate;
    private String activateCode;
    private Integer adult;
    private Integer rewardForce;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public Integer getActivate() {
        return this.activate;
    }
    
    public void setActivate(final Integer activate) {
        this.activate = activate;
    }
    
    public String getActivateCode() {
        return this.activateCode;
    }
    
    public void setActivateCode(final String activateCode) {
        this.activateCode = activateCode;
    }
    
    public Integer getAdult() {
        return this.adult;
    }
    
    public void setAdult(final Integer adult) {
        this.adult = adult;
    }
    
    public Integer getRewardForce() {
        return this.rewardForce;
    }
    
    public void setRewardForce(final Integer rewardForce) {
        this.rewardForce = rewardForce;
    }
}
