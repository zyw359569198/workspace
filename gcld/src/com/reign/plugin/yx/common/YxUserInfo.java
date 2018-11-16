package com.reign.plugin.yx.common;

public class YxUserInfo
{
    private String userId;
    private String userName;
    
    public YxUserInfo(final String userId, final String userName) {
        this.userId = userId;
        this.userName = userName;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public String getUserName() {
        return this.userName;
    }
}
