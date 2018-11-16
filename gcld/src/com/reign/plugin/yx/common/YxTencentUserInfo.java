package com.reign.plugin.yx.common;

public class YxTencentUserInfo
{
    private String userId;
    private String userName;
    private String openId;
    private String openKey;
    private String pf;
    private String pfKey;
    private String userIp;
    private int isYellowVip;
    private int isYellowYearVip;
    private int isYellowHighVip;
    private int yellowVipLevel;
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setOpenId(final String openId) {
        this.openId = openId;
    }
    
    public String getOpenId() {
        return this.openId;
    }
    
    public void setOpenKey(final String openKey) {
        this.openKey = openKey;
    }
    
    public String getOpenKey() {
        return this.openKey;
    }
    
    public void setPf(final String pf) {
        this.pf = pf;
    }
    
    public String getPf() {
        return this.pf;
    }
    
    public void setPfKey(final String pfKey) {
        this.pfKey = pfKey;
    }
    
    public String getPfKey() {
        return this.pfKey;
    }
    
    public void setUserIp(final String userIp) {
        this.userIp = userIp;
    }
    
    public String getUserIp() {
        return this.userIp;
    }
    
    public void setIsYellowVip(final int isYellowVip) {
        this.isYellowVip = isYellowVip;
    }
    
    public int getIsYellowVip() {
        return this.isYellowVip;
    }
    
    public void setIsYellowYearVip(final int isYellowYearVip) {
        this.isYellowYearVip = isYellowYearVip;
    }
    
    public int getIsYellowYearVip() {
        return this.isYellowYearVip;
    }
    
    public void setIsYellowHighVip(final int isYellowHighVip) {
        this.isYellowHighVip = isYellowHighVip;
    }
    
    public int getIsYellowHighVip() {
        return this.isYellowHighVip;
    }
    
    public void setYellowVipLevel(final int yellowVipLevel) {
        this.yellowVipLevel = yellowVipLevel;
    }
    
    public int getYellowVipLevel() {
        return this.yellowVipLevel;
    }
}
