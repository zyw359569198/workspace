package com.reign.gcld.user.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class UserLoginInfo implements IModel
{
    private static final long serialVersionUID = 1L;
    public static final int ONLINE = 1;
    public static final int OFFLINE = 0;
    private Integer vId;
    private String userId;
    private long onlineTime;
    private Date lastLoginTime;
    private Date lastLogoutTime;
    private Integer tag;
    private String yx;
    private long offlineTime;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public long getOnlineTime() {
        return this.onlineTime;
    }
    
    public void setOnlineTime(final long onlineTime) {
        this.onlineTime = onlineTime;
    }
    
    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }
    
    public void setLastLoginTime(final Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public Date getLastLogoutTime() {
        return this.lastLogoutTime;
    }
    
    public void setLastLogoutTime(final Date lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }
    
    public Integer getTag() {
        return this.tag;
    }
    
    public void setTag(final Integer tag) {
        this.tag = tag;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public long getOfflineTime() {
        return this.offlineTime;
    }
    
    public void setOfflineTime(final long offlineTime) {
        this.offlineTime = offlineTime;
    }
}
